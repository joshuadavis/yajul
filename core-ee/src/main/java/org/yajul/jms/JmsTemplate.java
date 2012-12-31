package org.yajul.jms;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IoC template for working with multiple JMS destinations.
 * Automagically cleans up JMS connections, sessions, consumers
 * and producers.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 3:30:07 PM
 */
public class JmsTemplate implements JmsContext {
    private ConnectionFactoryProvider connectionFactoryReference;
    private List<Connection> connections = new ArrayList<Connection>();
    private boolean transacted;
    private int acknowledgeMode = javax.jms.Session.AUTO_ACKNOWLEDGE;
    private boolean noLocal;
    private List<Session> sessions = new ArrayList<Session>();
    private List<MessageConsumer> consumers = new ArrayList<MessageConsumer>();
    private List<MessageProducer> producers = new ArrayList<MessageProducer>();

    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
    }

    public boolean isNoLocal() {
        return noLocal;
    }

    public void setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
    }

    public JmsTemplate(ConnectionFactoryProvider connectionFactoryReference) {
        this.connectionFactoryReference = connectionFactoryReference;
    }

    public TopicConnection createTopicConnection() throws JMSException {
        TopicConnection con = connectionFactoryReference.getTopicConnectionFactory().createTopicConnection();
        connections.add(con);
        return con;
    }

    public TopicSession createTopicSession(TopicConnection con) throws JMSException {
        TopicSession session = con.createTopicSession(isTransacted(), getAcknowledgeMode());
        sessions.add(session);
        return session;
    }

    public TopicSubscriber createSubscriber(TopicSession session, Topic topic, String selector) throws JMSException {
        TopicSubscriber subscriber = session.createSubscriber(topic, selector, isNoLocal());
        consumers.add(subscriber);
        return subscriber;
    }

    public TopicPublisher createPublisher(TopicSession session, Topic topic) throws JMSException {
        TopicPublisher publisher = session.createPublisher(topic);
        producers.add(publisher);
        return publisher;
    }

    public <T> T doAction(JmsAction<T> action) throws JMSException {
        T rv = null;
        try {
            rv = action.run(this);
        }
        finally {
            cleanup();
        }
        return rv;
    }

    private void cleanup() {
        // Clean up all the resources that were used during the transaction.

        // Stop the connections if there are consumers.
        if (!consumers.isEmpty()) {
            for (Connection connection : connections) {
                JmsHelper.stop(connection);
            }
        }
        for (MessageProducer producer : producers) {
            JmsHelper.close(producer);
        }
        producers.clear();
        for (MessageConsumer consumer : consumers) {
            JmsHelper.close(consumer);
        }
        consumers.clear();
        for (Session session : sessions) {
            JmsHelper.close(session);
        }
        sessions.clear();
        for (Connection connection : connections) {
            JmsHelper.close(connection);
        }
        connections.clear();
    }

    public interface JmsAction<T> {
        T run(JmsContext ctx) throws JMSException;
    }
}
