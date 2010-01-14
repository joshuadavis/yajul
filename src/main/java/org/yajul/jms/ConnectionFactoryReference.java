package org.yajul.jms;

import org.yajul.jndi.JndiHelper;
import org.yajul.jndi.JndiReference;

import javax.naming.InitialContext;
import javax.jms.ConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.jms.QueueConnectionFactory;

/**
 * References a JMS connection factory, either by name or directly.
 * Cast methods for convenience.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 2:29:38 PM
 */
public class ConnectionFactoryReference extends JndiReference<ConnectionFactory> {

    public ConnectionFactoryReference(InitialContext ic, String connectionFactoryName) {
        super(ic,ConnectionFactory.class,connectionFactoryName);
    }

    public ConnectionFactoryReference(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public TopicConnectionFactory getTopicConnectionFactory() {
        return (TopicConnectionFactory)get();
    }

    public QueueConnectionFactory getQueueConnectionFactory() {
        return (QueueConnectionFactory)get();
    }
}

