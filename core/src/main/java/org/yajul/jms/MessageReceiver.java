package org.yajul.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.naming.InitialContext;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

/**
 * Generic message receiver.  Creates a thread.
 * <br>User: Joshua Davis
 * Date: Sep 18, 2007
 * Time: 6:17:07 AM
 */
public class MessageReceiver extends Endpoint {
    private static Logger log = LoggerFactory.getLogger(MessageReceiver.class);
    private boolean shouldListen;
    private MessageListener listener;
    private Runnable idleAction;
    private static final int TIMEOUT = 1000;
    private Thread thread;
    private final int reconnectDelay;


    public MessageReceiver(InitialContext ic, String factoryJndiName, String destinationName,
                           MessageListener listener, String messageSelector, int reconnectDelay) {
        super(ic, factoryJndiName, destinationName, messageSelector);
        this.listener = listener;
        this.reconnectDelay = reconnectDelay;
    }

    public MessageReceiver(ConnectionFactoryProvider factoryReference,
                           DestinationProvider destinationReference,
                           MessageListener listener, String messageSelector, int reconnectDelay) {
        super(factoryReference, destinationReference, messageSelector);
        this.listener = listener;
        this.reconnectDelay = reconnectDelay;
    }

    public void shutdown() {
        stopListening();
        super.close();
        if (thread != null) {
            try {
                if (log.isDebugEnabled())
                    log.debug("shutdown() : waiting for thread to join...");
                thread.join();
                if (log.isDebugEnabled())
                    log.debug("shutdown() : thread stopped.");
            }
            catch (InterruptedException e) {
                log.error("Unexpected: " + e, e);
            }
        }
    }

    private void stopListening() {
        synchronized (this) {
            shouldListen = false;
        }
    }

    public List<Message> receiveSync(long timeout) {
        if (thread != null)
            throw new IllegalStateException("Started in async mode!");
        try {
            startConsumer();
            MessageConsumer consumer = getConsumer();
            List<Message> list = new LinkedList<Message>();
            // Receive a message.
            Message m = consumer.receive(timeout);
            // If we got one, keep receiving until there are no more.
            while (m != null) {
                list.add(m);
                m = consumer.receiveNoWait();
            }
            return list;
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of message objects of a particular class.  Messages that are not of class ObjectMessage
     * will be ignored.
     *
     * @param messages The list of JMS messages.
     * @param clazz    the class to filter by
     * @return a list of all message objects of the specified class.
     */
    public static <T> List<T> filterByMessageObjectClass(List<Message> messages, Class<T> clazz) {
        List<T> list = new LinkedList<T>();
        for (Message message : messages) {
            Object object = JmsHelper.getObject(message);
            if (object == null)
                continue;
            Class objectClass = object.getClass();
            if (clazz.isAssignableFrom(objectClass)) {
                list.add(clazz.cast(object));
            }
        }
        return list;
    }

    public void start(Runnable idleAction, ThreadFactory factory) {
        try {
            this.idleAction = idleAction;
            if (listener == null)
                throw new IllegalStateException("No listener!");
            super.startConsumer();
            shouldListen = true;
            if (factory == null)
                factory = new DefaultThreadFactory();
            thread = factory.newThread(new ListenerRunnable());
            thread.setDaemon(true);
            thread.start();
        }
        catch (JMSException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void reconnect() {
        boolean notConnected = true;
        close();
        int attempts = 0;
        while (notConnected) {
            try {
                try {
                    Thread.sleep(reconnectDelay);
                }
                catch (InterruptedException e1) {
                    log.warn("Can't sleep: " + e1);
                }
                attempts++;
                super.startConsumer();
                notConnected = false;
            }
            catch (JMSException e) {
                log.error("Attempt # " + attempts + ", unable to connect due to " + e, e);
                close();
            }
        } // while not connected
    }

    private boolean shouldRun() {
        synchronized (this) {
            return shouldListen && super.hasConsumer();
        }
    }

    public Message peek() throws JMSException {
        return JmsHelper.peek(getSession(), getDestination());
    }

    /**
     * For automatic naming.
     */
    private static AtomicInteger serialNumber = new AtomicInteger(0);

    private class DefaultThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            String threadName = "Receiver-" + serialNumber.incrementAndGet();
            return new Thread(r, threadName);
        }
    }

    private class ListenerRunnable implements Runnable {
        /**
         * Runs the JMS message consumer.
         */
        public void run() {
            while (shouldRun()) {
                try {
                    Message m = receive(TIMEOUT);
                    if (m != null) {
                        listener.onMessage(m);
                    } else {
                        if (idleAction != null)
                            idleAction.run();
                    }
                }
                catch (javax.jms.JMSException jmse) {
                    log.error("*** Reconnecting due to " + jmse, jmse);
                    reconnect();
                }
                catch (Throwable e) {
                    log.error(e.getMessage(), e);
                    log.info("Receiver loop continuing...");
                }
            }
            log.info("Listener thread stopped.");
        }
    }

}
