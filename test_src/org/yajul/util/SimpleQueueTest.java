/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jun 29, 2002
 * Time: 12:04:24 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.yajul.log.Logger;

public class SimpleQueueTest  extends TestCase
{
    private static Logger log = Logger.getLogger(SimpleQueueTest.class);
    private Object[] object = new Object[5];

    static class Consumer implements Runnable
    {
        private SimpleQueue queue;
        private Object current;
        private int count;
        private int messagesToConsume;
        private long pause;

        /**
         * Creates a new consumer, which will read the specified number of messages
         * pausing for the specified amout of time after consuming the message.
         * @param queue - The queue to read from.
         * @param messagesToConsume - The number of messages to consume, or zero (consume until empty).
         * @param pause - The amount of time to sleep after each messsage is consumed.
         */
        Consumer(SimpleQueue queue,int messagesToConsume,long pause)
        {
            this.queue = queue;
            this.messagesToConsume = messagesToConsume;
            this.pause = pause;
            count = 0;
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
                if (messagesToConsume > 0)
                    return count < messagesToConsume;
                else
                    return queue.size() > 0;
            }
        }

        public void run()
        {
            Object obj = null;
            while (isProcessing())
            {
                try { obj = queue.get(); } catch (InterruptedException ignore) {}
                synchronized(this)
                {
                    current = obj;
                    count++;
                }
                // log.debug("Recieved " + obj);
                if (pause > 0)
                {
                    // log.debug("Sleeping...");
                    try { Thread.sleep(pause); } catch (InterruptedException ignore) {}
                }
            } // while
        }

    }

    public SimpleQueueTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        // Initialize the array of test objects.
        for (int i = 0; i < object.length ; i++)
        {
            object[i] = new Character((char)((int)'A' + i));
        }
    }

    private void fillQueue(SimpleQueue q)
    {
        // Add some objects
        for(int i = 0; i < object.length ; i++)
            q.add(object[i]);
    }

    public void basicTest()
    {
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        fillQueue(q);
        // Make sure the size is right.
        assertEquals(object.length,q.size());
        // Dequeue them.
        Object obj = null;
        int j = 0;
        while (q.size() > 0)
        {
            try
            {
                obj = q.get();
            }
            catch (InterruptedException ie)
            {
                fail("Unexpected exception: " + ie.getMessage());
            }
            assertEquals(object[j],obj);
            j++;
        }
    }

    public void oneConsumer()
    {
        log.debug("oneConsumer()");
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        // Add some objects
        fillQueue(q);
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c = new Consumer(q,object.length * 2,0);
        Thread thread = new Thread(c,"ConsumerThread");
        thread.start();
        // Add some more objects
        fillQueue(q);
        // Wait for the consumer to stop.
        try { thread.join(); } catch (InterruptedException ignore) {}
        // The consumer should have stopped.
        assertTrue("Consumer should have stopped!",!c.isProcessing());
    }


    public void twoConsumers()
    {
        // log.debug("twoConsumers()");
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        // Add some objects
        fillQueue(q);
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c1 = new Consumer(q,0,2);
        Thread thread1 = new Thread(c1,"ConsumerThread1");
        Consumer c2 = new Consumer(q,0,3);
        Thread thread2 = new Thread(c2,"ConsumerThread2");
        thread1.start();
        thread2.start();
        // Add some more objects
        fillQueue(q);
        // Wait for the consumer to stop.
        try { thread1.join(); } catch (InterruptedException ignore) {}
        // Wait for the consumer to stop.
        try { thread2.join(); } catch (InterruptedException ignore) {}
        // log.debug("Threads joined.");
        // Make sure all messages were recieved by one of the consumers.
        assertEquals(object.length * 2, c1.getCount() + c2.getCount());
    }

    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new SimpleQueueTest("basicTest"));
        suite.addTest(new SimpleQueueTest("oneConsumer"));
        suite.addTest(new SimpleQueueTest("twoConsumers"));
        return suite;
    }
}
