package org.yajul.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.io.SerializationStats;

import javax.jms.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * Utility methods for JMS.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 10:41:52 AM
 */
public class JmsHelper {
    private static final Logger log = LoggerFactory.getLogger(JmsHelper.class);

    /**
     * Clean up JMS producer objects.  Typically used in a finally block.
     *
     * @param sender  the sender
     * @param session the session (may be null)
     * @param conn    the connection (may be null)
     */
    public static void close(MessageProducer sender, Session session, Connection conn) {
        close(sender);
        close(session);
        close(conn);
    }

    /**
     * Clean up JMS consumer objects.    Typically used in a finally block.
     *
     * @param consumer the consumer
     * @param session  the session (may be null)
     * @param conn     the connection (may be null)
     */
    public static void close(MessageConsumer consumer, Session session, Connection conn) {
        stop(conn);
        close(consumer);
        close(session);
        close(conn);
    }

    public static void stop(Connection conn) {
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.stop();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(MessageProducer sender) {
        if (sender != null) {
            //noinspection EmptyCatchBlock
            try {
                sender.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(MessageConsumer consumer) {
        if (consumer != null) {
            //noinspection EmptyCatchBlock
            try {
                consumer.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(Session session) {
        if (session != null) {
            //noinspection EmptyCatchBlock
            try {
                session.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.close();
            } catch (Exception ignore) {
            }
        }
    }


    public static void close(Session session, Connection connection) {
        close(session);
        close(connection);
    }

    /**
     * Returns the object in the JMS message if it's an object message.
     *
     * @param message the JMS message
     * @param clazz the class
     * @param autoUnwrap true to automatically unwrap SerializableWrapper, false to never unwrap
     * (just return the object).
     * @return the object in the JMS message if it's an object message, null otherwise.
     */
    public static <T extends Serializable> T getObject(Message message,Class<T> clazz, boolean autoUnwrap) {
        return clazz.cast(getObject(message,autoUnwrap));
    }

    /**
     * Returns the object in the JMS message if it's an object message.
     *
     * @param message the JMS message
     * @param autoUnwrap true to automatically unwrap SerializableWrapper, false to never unwrap
     * (just return the object).
     * @return the object in the JMS message if it's an object message, null otherwise.
     */
    public static Object getObject(Message message,boolean autoUnwrap) {
        if (message == null)
            return null;
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                Serializable serializable = objectMessage.getObject();
                if (autoUnwrap)
                    return SerializationStats.autoUnwrap(serializable);
                else
                    return serializable;
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else
            return null;
    }

    /**
     * Returns the object in the JMS message if it's an object message.
     *
     * @param message the JMS message
     * @return the object in the JMS message if it's an object message, null otherwise.
     */
    public static Object getObject(Message message) {
        return getObject(message,false);
    }

    /**
     * Returns the text in the JMS message if it's a text message.
     *
     * @param message the JMS message
     * @return the text in the JMS message if it's a text message, null otherwise.
     */
    public static String getText(Message message) {
        if (message == null)
            return null;
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                return textMessage.getText();
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else
            return null;
    }


    public static Destination getReplyTo(Message message) {
        try {
            return message.getJMSReplyTo();
        } catch (JMSException e) {
            log.warn("Unable to get JMSReplyTo due to: " + e);
            return null;
        }

    }
    
    public static Long getNullableLongProperty(Message m, String property) throws JMSException {
        return (Long) (m == null ? null : m.getObjectProperty(property));
    }

    public static String getStringProperty(Message m, String property) throws JMSException {
        return m == null ? null : m.getStringProperty(property);
    }

    public static Message peek(Session session, Destination destination) throws JMSException {
        QueueSession ses = (QueueSession) session;
        Queue queue = (Queue) destination;
        QueueBrowser browser = ses.createBrowser(queue);
        Enumeration enumeration = browser.getEnumeration();
        return enumeration.hasMoreElements() ? (Message) enumeration.nextElement() : null;
    }

    public static boolean messagePropertyNullOrEqualTo(Message message, String propertyName, Long aLong) {
        try {
            boolean exists = message.propertyExists(propertyName);
            if (exists) {
                long val = message.getLongProperty(propertyName);
                return val == aLong;
            } else
                return true;
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
