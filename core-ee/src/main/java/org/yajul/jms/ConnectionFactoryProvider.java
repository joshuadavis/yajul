package org.yajul.jms;

import org.yajul.jndi.CachedJndiObjectProvider;
import org.yajul.jndi.JndiLookup;
import org.yajul.jndi.JndiObjectProvider;
import org.yajul.util.ObjectProvider;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

/**
 * Cached JNDI lookup of the JMS ConnectionFactory.  Casts into either Queue or Topic connection factory.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 12:20 PM
 */
public class ConnectionFactoryProvider extends CachedJndiObjectProvider<ConnectionFactory> {

    public ConnectionFactoryProvider(ObjectProvider<ConnectionFactory> delegate) {
        super(delegate);
    }

    public ConnectionFactoryProvider(InitialContext ic, String factoryJndiName) {
        this(new JndiObjectProvider<ConnectionFactory>(ic,ConnectionFactory.class,factoryJndiName));
    }

    public TopicConnectionFactory getTopicConnectionFactory() {
        return (TopicConnectionFactory)getObject();
    }

    public QueueConnectionFactory getQueueConnectionFactory() {
        return (QueueConnectionFactory)getObject();
    }
}
