/*
 * Copyright (C) 2001-2002 Kiodex, Inc.
 * Proprietary and Confidential
 */

package org.yajul.thread;

import java.util.Random;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

/**
 * Tests ThreadPool
 * <br>
 * Created on Aug 28, 2002 7:15:24 PM
 * @author jdavis
 */
public class ThreadPoolTest extends TestCase
{
    private static Logger log = Logger.getLogger(ThreadPoolTest.class);

    private static class TestRunnable implements Runnable
    {
        private int count = 0;
        private long wait;
        private static Random random = new Random(System.currentTimeMillis());

        public TestRunnable(long wait)
        {
            this.wait = wait;
        }

        public TestRunnable()
        {
            // Sleep a random amount of time.
            wait = random.nextInt(10);
        }

        public int getCount()
        {
            synchronized (this)
            {
                return count;
            }
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see     java.lang.Thread#run()
         */
        public void run()
        {
            // log.debug("Sleeping for " + wait + " msec ...");
            try
            {
                Thread.sleep(wait);
            }
            catch (InterruptedException ie)
            {
                // ignore
            }

            synchronized (this)
            {
                // Increase the counter.
                count++;
                // log.debug("count = " + count);
            }
        }
    }

    public ThreadPoolTest(String name)
    {
        super(name);
    }

    public void testAddAndWaitForAll()
    {
        log.info("***** testAddAndWaitForAll() : ENTER *****");
        ThreadPool pool = new ThreadPool(3, "ThreadPool");
        TestRunnable tr = new TestRunnable();
        pool.add(tr);
        try
        {
            pool.waitForAll(true);
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }
        assertEquals(1, tr.getCount());

        boolean okay = false;
        try
        {
            pool.add(tr);
        }
        catch (IllegalStateException ise)
        {
            log.debug("Got expected exception (this is good).");
            okay = true;
        }

        assertTrue("Did not get expected exception!", okay);

        pool = new ThreadPool(3, "Pool[2]");
        tr = new TestRunnable();
        // Add a whole bunch of requests really fast.
        int limit = 64;
        for (int i = 0; i < limit; i++)
        {
            pool.add(tr);
        }
        try
        {
            pool.waitForAll();
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        assertEquals(limit, tr.getCount());

        log.info("***** testAddAndWaitForAll() : LEAVE *****");
    }

    public void testThreadGroup()
    {
        log.info("***** testThreadGroup() : ENTER *****");
        ThreadPool pool = new ThreadPool(3, new ThreadGroup("Group1"));
        TestRunnable tr = new TestRunnable();

        // Add a whole bunch of requests really fast.
        int limit = 32;
        for (int i = 0; i < limit; i++)
        {
            pool.add(tr);
        }
        try
        {
            pool.waitForAll(true);
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        assertEquals(limit, tr.getCount());
        log.info("***** testThreadGroup() : LEAVE *****");
    }

    public void testWaitForReady()
    {
        log.info("***** testWaitForReady() : ENTER *****");

        ThreadPool pool = new ThreadPool(3, new ThreadGroup("Group1"));
        TestRunnable tr = new TestRunnable();

        // Make sure that the pool starts in the ready state.
        try
        {
            pool.waitForReady();
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        log.debug("Pool is ready.");

        TestRunnable takes500ms = new TestRunnable(500);
        // Before adding, the idle thread count should be the total thread count.
        assertEquals(3,pool.getIdleThreadCount());
        try
        {
            log.debug("Adding one job (500ms)...");
            pool.add(takes500ms);
            // We *should* be ready if there is more than one thread in the pool.
            pool.waitForReady();
            log.debug("Pool is ready!");
            // There *should* be two threads available.
            assertEquals(2,pool.getIdleThreadCount());
            // Now, wait for all threads to be idle.
            log.debug("Waiting for 500ms job to finish (all idle)...");
            pool.waitForAll(false);
            log.debug("Pool idle.");
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        // Add a whole bunch of requests really fast.
        int limit = 64;
        int i = 0;
        for (i = 0; i < limit / 2; i++)
        {
            pool.add(tr);
        }
        try
        {
            pool.waitForReady();
            // At least one thread is ready.
            log.info("Ready, " + tr.getCount() + " of " + (limit / 2) + " executed, "
                    + pool.getQueueSize() + " in the queue.");
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        // Add the rest of the elements.
        for (; i < limit; i++)
        {
            pool.add(tr);
        }

        try
        {
            log.info("Waiting, " + tr.getCount() + " of " + limit + " executed, "
                    + pool.getQueueSize() + " in the queue.");
            pool.waitForAll(true);
        }
        catch (InterruptedException e)
        {
            log.error(e);
            throw new AssertionFailedError("Unexpected exception! " + e.getMessage());
        }

        assertEquals(limit, tr.getCount());

        log.info("***** testWaitForReady() : LEAVE *****");

    }

    public static Test suite()
    {
        // Suppress log messages for this test.
//        return new LogSuppressingSetup(new TestSuite(ThreadPoolTest.class));
        return new TestSuite(ThreadPoolTest.class);
    }
}
