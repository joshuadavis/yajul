

package org.yajul.util;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A simple broadcast messaging class.  Each subscriber has it's own queue of messages.
 * <br>
 * Created by IntelliJ IDEA on Aug 9, 2002 10:06:45 AM
 * @author jdavis
 */
public class SimpleTopic
{
    /**
     * A simple subscriber that will receive all messages on the topic
     * that is was created for.
     * @author jdavis
     */
    public static class Subscriber
    {
        private SimpleQueue queue;
        private SimpleTopic topic;

        private Subscriber(SimpleTopic topic)
        {
            queue = new SimpleQueue();
            this.topic = topic;
        }

        /**
         * Unsubscribe from the topic.
         */
        public void unsubscribe()
        {
            topic.unsubscribe(this);
        }

        /** Get the first object in the queue.  Blocks the thread that calls it until
         * there is an object in the queue.
         * @throws InterruptedException This is thrown when the waiter is interrupted.
         * @return The first object in the queue.
         */
        public Object get() throws InterruptedException
        {
            return queue.get();
        }

        /** Get the first object in the queue.  Blocks the thread that calls it until
         * there is an object in the queue.
         * @param waitTime - The maximum number of milliseconds to wait.
         * @throws InterruptedException This is thrown when the waiter is interrupted.
         * @return The first object in the queue.
         */
        public Object get(long waitTime) throws InterruptedException
        {
            return queue.get(waitTime);
        }
    }

    private Set subscribers = new HashSet();

    /**
     * Creates a new simple topic.
     */
    public SimpleTopic()
    {
    }

    /**
     * Adds a new subscriber to the topic.
     * @return Subscriber - The new subscriber.
     */
    public Subscriber subscribe()
    {
        synchronized (this)
        {
            Subscriber s = new Subscriber(this);
            subscribers.add(s);
            return s;
        }
    }

    /**
     * Stop recieving messages from the topic.
     * @param s - The subscriber to unsubscribe.
     */
    public void unsubscribe(Subscriber s)
    {
        synchronized(this)
        {
            subscribers.remove(s);
        }
    }

    /**
     * Publishes an object to all subscribers.
     * @param obj - The object (messsage) to publish.
     */
    public void publish(Object obj)
    {
        // Lock this object, so that a new subscriber doesn't
        // get added while the message is being published.
        synchronized(this)
        {
            // Iterate through the subscribers and add the message
            // to each subscriber's queue.
            Iterator iter = subscribers.iterator();
            Subscriber s = null;
            while (iter.hasNext())
            {
                s = (Subscriber)iter.next();
                s.queue.add(obj);
            }
        }
    }
}
