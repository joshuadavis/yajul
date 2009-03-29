package org.yajul.jms;

import org.yajul.jndi.JndiHelper;
import org.yajul.jndi.JndiReference;

import javax.naming.InitialContext;
import javax.jms.*;

/**
 * References a JMS destination, either by name or directly.
 * Cast methods for convenience.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 2:29:38 PM
 */
public class DestinationReference extends JndiReference<Destination> {

    public DestinationReference(InitialContext ic, String destinationName) {
        super(ic,destinationName);
    }

    public DestinationReference(Destination destination) {
        super(destination);
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