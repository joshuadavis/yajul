package org.yajul.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import java.lang.IllegalStateException;

/**
 * Generic JMS producer or consumer.  NOTE: This will cache the JMS Connection object, so it should never be used
 * by multiple threads.
 * <br>User: Joshua Davis
 * Date: Sep 18, 2007
 * Time: 7:07:35 AM
 */
public class Endpoint {
    private static Logger log = LoggerFactory.getLogger(Endpoint.class);

    private ConnectionFactoryProvider cfProvider;
    private DestinationProvider destinationReference;
    private Session session;
    private Connection connection;

    private MessageConsumer consumer;
    private MessageProducer producer;
    private final boolean transacted = false;
    private final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    private boolean consumerStarted;
    private boolean queueEndpoint;
    private String messageSelector;

    public Endpoint(InitialContext ic, String factoryJndiName, String destinationName, String messageSelector) {
        this(new ConnectionFactoryProvider(ic, factoryJndiName),
                new DestinationProvider(ic, destinationName), messageSelector);
    }


    public Endpoint(ConnectionFactoryProvider factoryReference, DestinationProvider destinationReference,
                    String messageSelector) {
        this.cfProvider = factoryReference;
        this.destinationReference = destinationReference;
        this.messageSelector = messageSelector;
    }

    public Endpoint(InitialContext ic, String factoryJndiName, Destination destination, String messageSelector) {
        this(new ConnectionFactoryProvider(ic, factoryJndiName),
                new DestinationProvider(destination), messageSelector);
    }

    public boolean isQueueEndpoint() {
        return queueEndpoint;
    }

    public void setQueueEndpoint(boolean queueEndpoint) {
        this.queueEndpoint = queueEndpoint;
    }

    public void close() {
        consumerStarted = false;
        if (consumer != null) {
            JmsHelper.close(consumer);
            consumer = null;
        }
        if (producer != null) {
            JmsHelper.close(producer);
            producer = null;
        }
        JmsHelper.close(session, connection);
        session = null;
        connection = null;
    }


    public void startConsumer() throws JMSException {
        if (!consumerStarted) {
            getConsumer();
            connection.start();
            consumerStarted = true;
        }
    }

    public Message receive(long timeout) throws JMSException {
        if (consumer == null)
            throw new IllegalStateException("No consumer!");
        return consumer.receive(timeout);
    }

    public Message receiveNowait() throws JMSException {
        if (consumer == null)
            throw new IllegalStateException("No consumer!");
        return consumer.receiveNoWait();
    }

    protected boolean hasConsumer() {
        return consumer != null;
    }

    protected MessageConsumer getConsumer() throws JMSException {
        if (producer != null)
            throw new IllegalStateException("This is already a consumer!");
        if (consumer == null) {
            consumer = getSession().createConsumer(getDestination(), messageSelector);
        }
        return consumer;
    }

    public MessageProducer getProducer() throws JMSException {
        if (consumer != null)
            throw new IllegalStateException("This is already a consumer!");
        if (producer == null) {
            producer = getSession().createProducer(getDestination());
        }
        return producer;
    }

    public Session getSession() throws JMSException {
        if (session == null) {
            if (queueEndpoint) {
                session = ((QueueConnection) getConnection()).createQueueSession(transacted, acknowledgeMode);
            } else {
                session = getConnection().createSession(transacted, acknowledgeMode);
            }
        }
        return session;
    }

    public Connection getConnection() throws JMSException {
        if (connection == null) {
            if (queueEndpoint)
                connection = cfProvider.getQueueConnectionFactory().createQueueConnection();
            else
                connection = cfProvider.getTopicConnectionFactory().createTopicConnection();
            onConnectionCreated(connection);
        }
        return connection;
    }

    protected void onConnectionCreated(Connection connection) throws JMSException {
    }

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    protected Destination getDestination() {
        return destinationReference.getDestination();
    }
}
