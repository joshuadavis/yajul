package org.yajul.jms;

import org.yajul.jndi.JndiHelper;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * Simple JavaBean that has all the properties required for connecting to a JMS destination.
 * <br>User: Josh
 * Date: Mar 3, 2009
 * Time: 6:12:18 AM
 */
public class JmsAttributes {
    private InitialContext ic;
    private String connectionFactoryName;
    private String destinationName;
    private ConnectionFactory connectionFactory;
    private Destination destination;
    private boolean transacted;
    private int acknowledgeMode = javax.jms.Session.AUTO_ACKNOWLEDGE;
    private boolean noLocal;
    private String messageSelector;

    public JmsAttributes(InitialContext ic, String connectionFactoryName, String destinationName) {
        this.ic = ic;
        this.connectionFactoryName = connectionFactoryName;
        this.destinationName = destinationName;
    }

    public JmsAttributes(ConnectionFactory connectionFactory, Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

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

    public String getMessageSelector() {
        return messageSelector;
    }

    public void setMessageSelector(String messageSelector) {
        this.messageSelector = messageSelector;
    }

    public ConnectionFactory getConnectionFactory() {
        if (connectionFactory != null)
            return connectionFactory;
        else
            return JndiHelper.lookup(ic, ConnectionFactory.class, connectionFactoryName);
    }

    public Destination getDestination() {
        if (destination != null)
            return destination;
        else
            return JndiHelper.lookup(ic, Destination.class, destinationName);
    }

    public Topic getTopic() {
        if (destination != null)
            return (Topic)destination;
        else
            return JndiHelper.lookup(ic,Topic.class,destinationName);
    }
    
    public TopicConnectionFactory getTopicConnectionFactory() {
        if (connectionFactory != null)
            return (TopicConnectionFactory) connectionFactory;
        else
            return JndiHelper.lookup(ic, TopicConnectionFactory.class, connectionFactoryName);
    }

    public QueueConnectionFactory getQueueConnectionFactory() {
        if (connectionFactory != null)
            return (QueueConnectionFactory) connectionFactory;
        else
            return JndiHelper.lookup(ic, QueueConnectionFactory.class, connectionFactoryName);
    }

    public TopicConnection createTopicConnection() throws JMSException {
        return getTopicConnectionFactory().createTopicConnection();
    }

    public TopicSession createTopicSession(TopicConnection connection) throws JMSException {
        return connection.createTopicSession(isTransacted(),getAcknowledgeMode());
    }

    public TopicSubscriber createSubscriber(TopicSession topicSession) throws JMSException {
        return topicSession.createSubscriber(getTopic(),getMessageSelector(),isNoLocal());
    }

    public TopicPublisher createPublisher(TopicSession topicSession) throws JMSException {
        return topicSession.createPublisher(getTopic());
    }
}
