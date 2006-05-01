// $Id$
/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jun 29, 2002
 * Time: 12:04:24 PM
 */
package org.yajul.util.test;

import junit.framework.TestCase;

import org.yajul.util.SimpleQueue;

public class SimpleQueueTest extends TestCase
{
    private Object[] object = new Object[5];

    public SimpleQueueTest(String name)
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

    private void fillQueue(SimpleQueue q)
    {
        // Add some objects
        for (int i = 0; i < object.length; i++)
            q.add(object[i]);
    }

    public void testSingleThread()
    {
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        fillQueue(q);
        // Make sure the size is right.
        assertEquals(object.length, q.size());
        // Dequeue them.
        Object obj = null;
        int j = 0;
        while (q.size() > 0)
        {
            try
            {
                obj = q.get();
            }
            catch (InterruptedException ignore)
            {
            }
            assertEquals(object[j], obj);
            j++;
        }
    }

    public void testOneConsumer()
    {
        // Create an empty queue queue
        SimpleQueue q = new SimpleQueue();
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c = new Consumer(q, object.length * 2, 0);
        Thread thread = new Thread(c, "ConsumerThread");
        thread.start();
        // The consumer should now be waiting on the thread.
        // Add some objects
        fillQueue(q);
        // Wait for a few milliseconds.
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException ignore)
        {
        }
        // Add some more objects
        fillQueue(q);
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

        // Create a queue
        q = new SimpleQueue();
        // Add some objects
        fillQueue(q);
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        c = new Consumer(q, object.length * 2, 0);
        thread = new Thread(c, "ConsumerThread");
        thread.start();
        // Add some more objects
        fillQueue(q);
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


    public void testTwoConsumers()
    {
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        // Add some objects
        fillQueue(q);
        // Create a consumer and a thread.  The consumer will consume 2 * the number of test objects.
        Consumer c1 = new Consumer(q, 0, 2);
        Thread thread1 = new Thread(c1, "ConsumerThread1");
        Consumer c2 = new Consumer(q, 0, 3);
        Thread thread2 = new Thread(c2, "ConsumerThread2");
        thread1.start();
        thread2.start();
        // Add some more objects
        fillQueue(q);
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
        // log.debug("Threads joined.");
        // Make sure all messages were recieved by one of the consumers.
        assertEquals(object.length * 2, c1.getCount() + c2.getCount());
    }

    public void testTimeout()
    {
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        int timeouts = runProducerAndConsumer(q, 20, 5);
        // Make sure there were some timeouts.
        assertTrue(timeouts > 0);
        // Make sure that the queue is now empty.
        assertEquals(0, q.size());

        timeouts = runProducerAndConsumer(q, 5, 20);
        // Make sure that the queue is now empty.
        assertEquals(0, q.size());
    }

    private int runProducerAndConsumer(SimpleQueue q, int producerInterval, int consumerInterval)
    {
        // Create a producer that emits a speicified number of messages at a specified interval.
        Producer p = new Producer(object, q, producerInterval);
        Thread t = new Thread(p, "ProducerThread");
        t.start();
        int timeouts = 0;
        q.add(new Object());    // Add an object to the queue, so it isn't empty.
        // Retrieve using a get with a timeout, making sure that the timeout is less the message interval.
        for (int i = 0; i < object.length + 1; i++)
        {
            try
            {
                Object o = null;
                while (o == null)
                {
                    o = q.get(consumerInterval);
                    if (o == null)
                        timeouts++;
                }
            }
            catch (InterruptedException ignore)
            {
            }
        }
        try
        {
            t.join();
        }
        catch (InterruptedException ignore)
        {
        }
        return timeouts;
    }

    public void testNoTimeout()
    {
        // Create a queue
        SimpleQueue q = new SimpleQueue();
        // Create a producer that emits a speicified number of messages at a specified interval.
        Producer p = new Producer(object, q, 20);
        Thread t = new Thread(p, "ProducerThread");
        t.start();
        int empty = 0;
        int interval = 5;
        // Retrieve by polling the queue.
        for (int i = 0; i < object.length; i++)
        {
            try
            {
                Object o = null;
                while (o == null)
                {
                    o = q.get(0);
                    if (o == null)
                        empty++;
                    try
                    {
                        Thread.sleep(interval);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                }
            }
            catch (InterruptedException ignore)
            {
            }
        }
        try
        {
            t.join();
        }
        catch (InterruptedException ignore)
        {
        }
        // Make sure there were some empty polling intervals.
        assertTrue(empty > 0);
    }

    static class Consumer implements Runnable
    {
        private SimpleQueue queue;
        private int count;
        private int messagesToConsume;
        private long pause;

        /**
         * Creates a new consumer, which will read the specified number of messages
         * pausing for the specified amout of time after consuming the message.
         *
         * @param queue             - The queue to read from.
         * @param messagesToConsume - The number of messages to consume, or zero (consume until empty).
         * @param pause             - The amount of time to sleep after each messsage is consumed.
         */
        Consumer(SimpleQueue queue, int messagesToConsume, long pause)
        {
            this.queue = queue;
            this.messagesToConsume = messagesToConsume;
            this.pause = pause;
            count = 0;
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
            while (isProcessing())
            {
                try
                {
                    queue.get();
                }
                catch (InterruptedException ignore)
                {
                }
                synchronized (this)
                {
                    count++;
                }
                // log.debug("Recieved " + obj);
                if (pause > 0)
                {
                    // log.debug("Sleeping...");
                    try
                    {
                        Thread.sleep(pause);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                }
            } // while
        }

    }

    static class Producer implements Runnable
    {
        private Object[] objects;
        private SimpleQueue queue;
        private long interval;

        public Producer(Object[] objects, SimpleQueue queue, long interval)
        {
            this.objects = objects;
            this.queue = queue;
            this.interval = interval;
        }

        public void run()
        {
            for (int i = 0; i < objects.length; i++)
            {
                queue.add(objects[i]);
                try
                {
                    Thread.sleep(interval);
                }
                catch (InterruptedException ignore)
                {
                }
            }
        }
    }
}
