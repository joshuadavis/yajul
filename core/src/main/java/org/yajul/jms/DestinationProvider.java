package org.yajul.jms;

import org.yajul.jndi.CachedJndiObjectProvider;
import org.yajul.jndi.JndiLookup;
import org.yajul.jndi.JndiObjectProvider;
import org.yajul.util.InstanceProvider;
import org.yajul.util.ObjectProvider;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;

/**
 * Cached JNDI lookup for topics and queues.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 12:22 PM
 */
public class DestinationProvider extends CachedJndiObjectProvider<Destination> {
    public DestinationProvider(ObjectProvider<Destination> delegate) {
        super(delegate);
    }

    public DestinationProvider(Destination destination) {
        this(new InstanceProvider<Destination>(destination));
    }

    public DestinationProvider(InitialContext ic, String name) {
        this(new JndiObjectProvider<Destination>(ic, Destination.class, name));
    }

    public Destination getDestination() {
        return getObject();
    }

    public Topic getTopic() {
        return (Topic)getDestination();
    }

    public Queue getQueue() {
        return (Queue)getDestination();
    }

}
