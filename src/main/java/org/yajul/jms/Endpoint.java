package org.yajul.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.IllegalStateException;

/**
 * Generic JMS producer or consumer.
 * <br>User: Joshua Davis
 * Date: Sep 18, 2007
 * Time: 7:07:35 AM
 */
public class Endpoint
{
    private static Logger log = LoggerFactory.getLogger(Endpoint.class);

    private InitialContext ic;
    private ConnectionFactory connectionFactory;
    private Session session;
    private Destination destination;
    private Connection connection;

    private MessageConsumer consumer;
    private MessageProducer producer;
    private final boolean transacted = false;
    private final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    private boolean consumerStarted;
    private boolean queueEndpoint;
    private String messageSelector;

    public Endpoint(String factoryJndiName, String destinationName)
    {
        this(factoryJndiName, destinationName, null);
    }

    public Endpoint(String factoryJndiName, String destinationName, String messageSelector)
    {
        try
        {
            ic = new InitialContext();
            this.connectionFactory = (ConnectionFactory) ic.lookup(factoryJndiName);
            this.destination = (Destination) ic.lookup(destinationName);
            this.messageSelector = messageSelector;
        }
        catch (NamingException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Endpoint(String factoryJndiName, Destination destination)
    {
        try
        {
            this.connectionFactory = (ConnectionFactory) ic.lookup(factoryJndiName);
            this.destination = destination;
        }
        catch (NamingException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean isQueueEndpoint()
    {
        return queueEndpoint;
    }

    public void setQueueEndpoint(boolean queueEndpoint)
    {
        this.queueEndpoint = queueEndpoint;
    }

    public void close()
    {
        consumerStarted = false;
        if (consumer != null)
        {
            JmsHelper.close(consumer, session, connection);
            consumer = null;
            session = null;
            connection = null;
        }
        else if (producer != null)
        {
            JmsHelper.close(producer, session, connection);
            producer = null;
            session = null;
            connection = null;
        }
    }


    public void startConsumer() throws JMSException
    {
        if (!consumerStarted)
        {
            getConsumer();
            connection.start();
            consumerStarted = true;
        }
    }

    public Message receive(long timeout) throws JMSException
    {
        if (consumer == null)
            throw new IllegalStateException("No consumer!");
        return consumer.receive(timeout);
    }

    public Message receiveNowait() throws JMSException
    {
        if (consumer == null)
            throw new IllegalStateException("No consumer!");
        return consumer.receiveNoWait();
    }

    protected boolean hasConsumer()
    {
        return consumer != null;
    }

    protected MessageConsumer getConsumer() throws JMSException
    {
        if (producer != null)
            throw new IllegalStateException("This is already a consumer!");
        if (consumer == null)
        {
            consumer = getSession().createConsumer(destination, messageSelector);
        }
        return consumer;
    }

    public MessageProducer getProducer() throws JMSException
    {
        if (consumer != null)
            throw new IllegalStateException("This is already a consumer!");
        if (producer == null)
        {
            producer = getSession().createProducer(destination);
        }
        return producer;
    }

    public Session getSession() throws JMSException
    {
        if (session == null)
        {
            if (queueEndpoint)
            {
                session = ((QueueConnection)getConnection()).createQueueSession(transacted,acknowledgeMode);
            }
            else
            {
                session = getConnection().createSession(transacted, acknowledgeMode);
            }
        }
        return session;
    }

    public Connection getConnection() throws JMSException
    {
        if (connection == null)
        {
            if (queueEndpoint)
                connection = ((QueueConnectionFactory)connectionFactory).createQueueConnection();
            else
                connection = connectionFactory.createConnection();
            onConnectionCreated(connection);
        }
        return connection;
    }

    protected void onConnectionCreated(Connection connection) throws JMSException
    {
    }

    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }

    protected Destination getDestination()
    {
        return destination;
    }
}
