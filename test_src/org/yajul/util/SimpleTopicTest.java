package org.yajul.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.yajul.log.Logger;


/**
 * TODO: Add class comments here.
 * <br>
 * Created by IntelliJ IDEA on Aug 9, 2002 10:22:04 AM
 * @author jdavis
 */
public class SimpleTopicTest extends TestCase
{
    private static Logger log = Logger.getLogger(SimpleTopicTest.class);
    private Object[] object = new Object[5];

    static class Consumer implements Runnable
    {
        private SimpleTopic topic;
        private Object current;
        private int count;
        private int messagesToConsume;
        private long pause;
        private SimpleTopic.Subscriber subscriber;

        /**
         * Creates a new consumer, which will read the specified number of messages
         * pausing for the specified amout of time after consuming the message.
         * @param topic - The topic to read from.
         * @param messagesToConsume - The number of messages to consume, or zero (consume until empty).
         * @param pause - The amount of time to sleep after each messsage is consumed.
         */
        Consumer(SimpleTopic topic, int messagesToConsume, long pause)
        {
            this.topic = topic;
            this.messagesToConsume = messagesToConsume;
            this.pause = pause;
            count = 0;
            subscriber = topic.subscribe();
            // log.debug("Subscribed to topic.");
        }

        public Object getCurrentObject()
        {
            synchronized (this)
            {
                return current;
            }
        }

        public int getCount()
        {
            synchronized (this)
            {
                return count;
            }
        }

        public boolean isProcessing()
        {
            synchronized (this)
            {
                return count < messagesToConsume;
            }
        }

        public void run()
        {
            Object obj = null;
            while (isProcessing())
            {
                // log.debug("Waiting...");
                try
                {
                    obj = subscriber.get();
                }
                catch (InterruptedException ignore)
                {
                }
                synchronized (this)
                {
                    current = obj;
                    count++;
                }
                // log.debug("Recieved " + obj);
                if (pause > 0)
                {
                    log.debug("Sleeping...");
                    try
                    {
                        Thread.sleep(pause);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                }
            } // while
            subscriber.unsubscribe();
            // log.debug("Unsubscribed.");
        }

    }

    public SimpleTopicTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        // Initialize the array of test objects.
        for (int i = 0; i < object.length; i++)
        {
            object[i] = new Character((char) ((int) 'A' + i));
        }
    }

    private void fillTopic(SimpleTopic topic)
    {
        // log.debug("Publishing messages...");
        // Add some objects
        for (int i = 0; i < object.length; i++)
            topic.publish(object[i]);
        // log.debug("Published.");
    }

    public void basicTest()
    {
        SimpleTopic topic = new SimpleTopic();
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c = new Consumer(topic, object.length, 0);
        Thread thread = new Thread(c, "ConsumerThread");
        thread.start();
        // log.debug("Consumer started.");
        // Add some more objects
        fillTopic(topic);
        // Wait for the consumer to stop.
        try
        {
            thread.join();
        }
        catch (InterruptedException ignore)
        {
        }
        // The consumer should have stopped.
        assertTrue("Consumer should have stopped!", !c.isProcessing());

    }

    public void twoConsumers()
    {
        SimpleTopic topic = new SimpleTopic();
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c1 = new Consumer(topic, object.length, 0);
        Thread thread1 = new Thread(c1, "ConsumerThread");
        thread1.start();
        // log.debug("Consumer1 started.");
        Consumer c2 = new Consumer(topic, object.length, 0);
        Thread thread2 = new Thread(c2, "ConsumerThread");
        thread2.start();
        // log.debug("Consumer2 started.");
        // Add some more objects
        fillTopic(topic);
        // Wait for the consumer to stop.
        try
        {
            thread1.join();
        }
        catch (InterruptedException ignore)
        {
        }
        // Wait for the consumer to stop.
        try
        {
            thread2.join();
        }
        catch (InterruptedException ignore)
        {
        }
        // The consumer should have stopped.
        assertTrue("Consumer1 should have stopped!", !c1.isProcessing());
        assertTrue("Consumer2 should have stopped!", !c2.isProcessing());

    }

    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new SimpleTopicTest("basicTest"));
        suite.addTest(new SimpleTopicTest("twoConsumers"));
        return suite;
    }

}
