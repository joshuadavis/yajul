package org.yajul.jms;

import org.yajul.util.Lifecycle;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.jms.*;
import java.lang.IllegalStateException;

/**
 * Listens on a JMS topic.  Reconnects if needed.
 * <br>User: Josh
 * Date: Mar 3, 2009
 * Time: 5:59:51 AM
 */
public class TopicListener implements Lifecycle {
    private final static Logger log = LoggerFactory.getLogger(TopicListener.class);

    private JmsAttributes jms;

    private TopicConnection connection;
    private TopicSession topicSession;
    private TopicSubscriber subscriber;

    /**
     * The actual message listener.
     */
    private MessageListener messageListener;
    /**
     * A lifecycle callback interface for the message listener.
     */
    private Lifecycle lifecycle;
    private boolean started;

    /**
     * @param jms JMS setttings
     * @param messageListener the object that will process messages
     * @param lifecycle a lifecycle interface for the message listener
     */
    public TopicListener(JmsAttributes jms, MessageListener messageListener,Lifecycle lifecycle) {
        assert jms != null;
        assert messageListener != null;
        assert lifecycle != null;
        this.jms = jms;
        this.messageListener = messageListener;
        this.lifecycle = lifecycle;
        started = false;
    }

    public void start() {
        synchronized (this) {
            doStart();
        }
    }

    private void doStart() {
        //noinspection InfiniteLoopStatement
        while (!started) {
            connect();
        }
    }

    private void connect() {
        try {
            if (log.isDebugEnabled())
                log.debug("connect() : starting...");
            connection = jms.createTopicConnection();
            connection.setExceptionListener(new RestartingListener(this));
            topicSession = jms.createTopicSession(connection);
            subscriber = jms.createSubscriber(topicSession);
            subscriber.setMessageListener(messageListener);
            lifecycle.start();  // Tell the listener we're going to start now.
            connection.start(); // Start recieving messages.
            started = true;
            if (log.isDebugEnabled())
                log.debug("connect() : started");
        } catch (Exception e) {
            log.warn("Unable to start listener due to: " + e.getMessage());
            pause();
        }
    }

    private static void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // ignore
        }
    }

    public void stop() {
        synchronized (this) {
            disconnect();
            subscriber = null;
            topicSession = null;
            connection = null;
            started = false;
        }
    }

    private void disconnect() {
        if (log.isDebugEnabled())
           log.debug("disconnect() : stopping...");
        JmsHelper.close(subscriber, topicSession, connection);
        if (started)
            lifecycle.stop();   // Tell the listener we're stopping.
        if (log.isDebugEnabled())
           log.debug("disconnect() : stopped");
    }
}
