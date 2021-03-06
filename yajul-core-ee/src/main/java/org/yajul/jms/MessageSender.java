package org.yajul.jms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.jta.JtaHelper;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.transaction.UserTransaction;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.yajul.util.Callable;

/**
 * Generic message sender.
 * <br>User: Joshua Davis
 * Date: Sep 18, 2007
 * Time: 7:06:18 AM
 */
public class MessageSender extends Endpoint {
    private static Logger log = LoggerFactory.getLogger(MessageSender.class);

    private int mode = DeliveryMode.PERSISTENT;
    private int priority = 4;
    private long ttl = 30000L;
    private static final long REPLY_TIMEOUT = 5000;
    private long replyTimeout = REPLY_TIMEOUT;

    public MessageSender(InitialContext ic, String factoryJndiName, String destinationName, String messageSelector) {
        super(ic, factoryJndiName, destinationName, messageSelector);
    }

    public MessageSender(InitialContext ic, String factoryJndiName, Destination destination, String messageSelector) {
        super(ic, factoryJndiName, destination, messageSelector);
    }

    @Override
    protected void onConnectionCreated(Connection connection) throws JMSException {
        connection.start();
    }

    public long getReplyTimeout() {
        return replyTimeout;
    }

    public void setReplyTimeout(long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    public ObjectMessage createObjectMessage() {
        try {
            Session session = getSession();
            return session.createObjectMessage();
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Message message) {
        try {
            MessageProducer sender = getProducer();
            sender.setDeliveryMode(mode);
            sender.setPriority(priority);
            sender.setTimeToLive(ttl);
            sender.send(message);
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public Topic createTemporaryTopic() {
        try {
            return getSession().createTemporaryTopic();
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public Message sendAndWaitForReply(Message msg, UserTransaction ut) {
        Topic replyto;
        MessageConsumer consumer = null;
        Message reply = null;
        try {
            replyto = createTemporaryTopic();
            consumer = getSession().createConsumer(replyto);
            msg.setJMSReplyTo(replyto);

            final Message m = msg;
            JtaHelper.doInTx(
                    ut,
                    new Callable<Object>() {
                        public Object call() {
                            send(m);
                            return null;
                        }
                    });
            long start = System.currentTimeMillis();
            reply = consumer.receive(replyTimeout);
            if (reply == null)
                throw new RuntimeException("Timed out waiting for reply.");
            if (log.isDebugEnabled())
                log.debug("sendAndWaitForReply() : Received reply after " +
                        (System.currentTimeMillis() - start) + "ms");

        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
        finally {
            JmsHelper.close(consumer, null, null);
        }

        return reply;
    }

    public void sendObjectMessage(Serializable object) {
        sendObjectMessage(object, null);
    }

    public void sendObjectMessage(Serializable object, Map<String, Object> properties) {
        try {
            Session session = getSession();
            ObjectMessage objectMessage;
            objectMessage = session.createObjectMessage(object);

            if (properties != null) {
                Iterator<String> keys = properties.keySet().iterator();
                //noinspection WhileLoopReplaceableByForEach
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = properties.get(key);

                    //Add the other types here if needed.
                    if (value instanceof Long) {
                        objectMessage.setLongProperty(key, (Long) value);
                    } else if (value instanceof String) {
                        objectMessage.setStringProperty(key, (String) value);
                    } else {
                        objectMessage.setObjectProperty(key, value);
                    }
                }
            }

            send(objectMessage);
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendReply(InitialContext ic, String factoryJndiName, Message message, Serializable replyObject) {
        sendReply(ic, factoryJndiName, message, replyObject, null);
    }

    public static void sendReply(final InitialContext ic, final String factoryJndiName, Message message, Serializable replyObject, Map<String, Object> properties) {
        final Destination jmsReplyTo = JmsHelper.getReplyTo(message);
        if (jmsReplyTo != null) {
            sendObject(new SenderFactory() {
                public MessageSender createSender() {
                    return new MessageSender(ic, factoryJndiName, jmsReplyTo, null);
                }
            }, replyObject, properties);
        }
    }

    public static void closeSender(MessageSender sender) {
        if (sender != null) {
            try {
                sender.close();
            }
            catch (Exception e) {
                log.error("Error while closing sender: " + e, e);
            }
        }
    }

    private static void sendObject(SenderFactory senderFactory, Serializable messageObject,
                                   Map<String, Object> properties) {
        MessageSender sender = null;
        try {
            sender = senderFactory.createSender();
            if (properties == null)
                sender.sendObjectMessage(messageObject);
            else
                sender.sendObjectMessage(messageObject, properties);
        }
        catch (Exception e) {
            log.warn("Unable to send message due to: " + e, e);
        }
        finally {
            closeSender(sender);
        }
    }

    public static void sendObject(
            final InitialContext ic, final String factoryJndiName,
            final String destinationName,
            Serializable messageObject) {
        sendObject(ic, factoryJndiName, destinationName, messageObject, null);
    }

    public static void sendObject(
            final InitialContext ic,
            final String factoryJndiName,
            final String destinationName,
            Serializable messageObject,
            Map<String, Object> properties) {
        sendObject(new SenderFactory() {
            public MessageSender createSender() {
                return new MessageSender(ic, factoryJndiName, destinationName, null);
            }
        }, messageObject, properties);
    }

    public interface SenderFactory {
        MessageSender createSender();
    }
}
