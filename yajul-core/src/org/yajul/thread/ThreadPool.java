
package org.yajul.thread;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A 'simple pool' of threads.
 * <br>
 * Created on Aug 28, 2002 6:00:01 PM
 *
 * @author jdavis
 */
public class ThreadPool
{
    private static class Request
    {
        private Runnable target;
        private Object lock;

        Request(Runnable target, Object lock)
        {
            if (target == null)
                throw new IllegalArgumentException("ThreadPool.RequestHeaders: Target cannot be null!");
            this.target = target;
            this.lock = lock;
        }
    } // class RequestHeaders

    private static class PooledThread extends Thread
    {
        private static Logger log = Logger.getLogger(PooledThread.class);

        private ThreadPool parent;
        private boolean shutdown;

        PooledThread(ThreadPool parent, int number)
        {
            super(parent.getGroup().getName() + "-" + number);
            this.parent = parent;
            this.shutdown = false;
        }

        void shutdown()
        {
            shutdown = true;
        }

        boolean isRunning()
        {
            return !shutdown;
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p/>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see java.lang.Thread#start()
         * @see java.lang.Thread#stop()
         * @see java.lang.Thread#Thread(java.lang.ThreadGroup,
                *      java.lang.Runnable, java.lang.String)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            // log.debug("run() : ENTER");
            Request request = null;
            while (isRunning())
            {
                // Get the next request from the queue, if it is empty, wait.
                BusyFlag flag = parent.flag;
                try
                {
                    flag.acquire();
                    LinkedList queue = parent.requestQueue;
                    while (request == null && isRunning())
                    {
                        if (queue.size() > 0)
                        {
                            try
                            {
                                request = (Request) queue.removeFirst();
                                if (request.target == null)
                                    throw new IllegalStateException("Target is null!");
                                parent.activeThreads++;     // Increment the active thread count.
                                if ((queue.size() == 0))    // If the queue is empty...
                                    parent.ready.signal();  // Signal a 'ready' condition.
                            }
                            catch (NoSuchElementException nsee)
                            {
                                // log.debug("Queue is empty.");
                                request = null; // The list is empty.
                            }
                            catch (ClassCastException cce)
                            {
                                // DOH!  What the heck is in the queue!
                                log.error(cce);
                                request = null;
                            }
                        }
                        else
                            request = null;

                        if (request == null)
                        {
                            try
                            {
                                // log.debug("Waiting for available request condition...");
                                parent.pending.cvWait();
                            }
                            catch (InterruptedException ie)
                            {
                                // log.info("run() : LEAVE (InterruptedException)");
                                return; // Interrupted, stop now!
                            }
                        } // if
                    } // while
                }
                finally
                {
                    flag.release();
                }

                // If a shutdown was requested, stop now.
                if (!isRunning())
                {
                    // log.info("run() : LEAVE (shutdown after queue queried)");
                    return;
                }

                // Run the target!
                request.target.run();

                // Signal the empty condition, if it is so.
                try
                {
                    flag.acquire();
                    parent.activeThreads--;                 // One less active thread.
                    parent.activeRequests--;                // One less request is running or enqueued.
                    if (parent.activeRequests == 0)
                    {
                        // log.debug("Signalling: Idle pool condition.");
                        parent.idle.signal();
                    }
                    // If there is more than one thread available, signal 'ready'.
                    if (parent.activeThreads < parent.threads.length)
                    {
                        parent.ready.signal();
                    }
                }
                finally
                {
                    flag.release();
                }

                // If there is a lock, notify on it.
                if (request.lock != null)
                {
                    synchronized (request.lock)
                    {
                        request.lock.notify();
                    }
                }

                // Be nice before grabbing another request.
                yield();
                // DONE!! Set the request variable to null so the loop doesn't keep processing the same request.
                request = null;
            } // while
            // log.info("run() : LEAVE (shutdown)");
        } // run()
    } // class

    /**
     * A logger for this class. *
     */
    private static Logger log = Logger.getLogger(ThreadPool.class);
    /**
     * The thread group for all threads in the pool. *
     */
    private ThreadGroup group;
    /**
     * Mutex lock that protects the request queue, and the condvars. *
     */
    private BusyFlag flag;
    /**
     * Condition: Signalled when there are pending jobs. *
     */
    private CondVar pending;
    /**
     * Condition: Signalled when *all* threads in the pool are idle. *
     */
    private CondVar idle;
    /**
     * Condition: Signalled when a thread becomes idle, and the queue is empty. *
     */
    private CondVar ready;
    /**
     * The list of requests that are wating to be executed by the pool. *
     */
    private LinkedList requestQueue;
    /**
     * The threads in the pool. *
     */
    private PooledThread[] threads;
    /**
     * Pool termination flag. *
     */
    private boolean terminated;

    /**
     * The number of requests that are in the queue or being processed by a thread. *
     */
    private int activeRequests;

    /**
     * The number of threads that are active. *
     */
    private int activeThreads;

    /**
     * Creates a new thread pool of the specified size.
     *
     * @param size      The number of threads in the pool.
     * @param groupName The nameof the thread group for the threads.
     */
    public ThreadPool(int size, String groupName)
    {
        this(size, new ThreadGroup(groupName));
    }

    /**
     * Returns the number of threads in the pool.
     *
     * @return int - The number of threads in the pool.
     */
    public int getSize()
    {
        return threads.length;
    }

    /**
     * Creates a new thread pool of the specified size.
     *
     * @param size  The number of threads in the pool.
     * @param group The thread group that the threads will be created in.
     */
    public ThreadPool(int size, ThreadGroup group)
    {
        this.group = group;
        flag = new BusyFlag();
        pending = new CondVar(flag);
        idle = new CondVar(flag);
        ready = new CondVar(flag);

        requestQueue = new LinkedList();
        terminated = false;

        activeRequests = 0;
        activeThreads = 0;

        // Create the threads and start them.
        threads = new PooledThread[size];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new PooledThread(this, i);
            threads[i].start();
            if (log.isDebugEnabled())
                log.debug("Thread " + threads[i].getName() + " started.");
        }
    }

    /**
     * Sets the priority for all pooled threads.
     * @param priority The new priority.
     * @see Thread#setPriority(int)
     */
    public void setPooledThreadPriority(int priority)
    {
        for (int i = 0; i < threads.length; i++)
            threads[i].setPriority(priority);
    }

    private void add(Runnable target, Object lock)
    {
        try
        {
            flag.acquire();         // Get a lock.
            if (terminated)
                throw new IllegalStateException("Cannot add targets, the thread pool has been shut down.");
            requestQueue.add(new Request(target, lock));
            activeRequests++;       // One more request is queued, or running.
            // log.debug("Signalling available (queue size = " + requestQueue.size() + ") ...");
            pending.signal();       // There is work pending, wake up a waiting thread.
        }
        finally
        {
            flag.release();         // Release the lock.
        }
    }

    /**
     * Adds the target to the queue of runnable requests for the pool.  The runnable target will
     * be executed at some later point by one of the threads.
     *
     * @param target The target runnable object.
     */
    public void add(Runnable target)
    {
        add(target, null);
    }

    /**
     * Adds the target to the queue, and blocks the calling thread until the target has been fully executed
     * by one of the threads in the pool.
     *
     * @param target The target runnable object.
     * @throws InterruptedException If the wait was interrupted.
     */
    public void addAndWait(Runnable target) throws InterruptedException
    {
        Object lock = new Object();

        synchronized (lock)
        {
            add(target, lock);      // Add the target to the queue.
            lock.wait();            // Wait for the lock.
        }
    }

    /**
     * Waits for all running targets to complete.
     *
     * @param terminate True if the thread pool is to terminate after all targets complete.
     * @throws InterruptedException If the wait was interrupted.
     */
    public void waitForAll(boolean terminate) throws InterruptedException
    {
        try
        {
            flag.acquire();
            while (activeRequests > 0)
            {
                // log.debug("Waiting for empty condition...");
                idle.cvWait();
            }
            if (terminate)
            {
                log.info("Shutting down...");
                for (int i = 0; i < threads.length; i++)
                    threads[i].shutdown();  // Flag all threads for shutdown.
                log.debug("Signalling available (shutdown)...");
                pending.broadcast();        // Wake up all idle threads.
                terminated = true;
                // TODO: Interrupt the threads?
                log.info("Shutdown complete.");
            }
        }
        finally
        {
            flag.release();
        }
    }

    /**
     * Shut down the pool.
     *
     * @see ThreadPool#waitForAll(boolean)
     */
    public void shutdown()
    {
        try
        {
            waitForAll(true);
        }
        catch (InterruptedException e)
        {
            log.error(e);
        }
    }

    /**
     * Waits for all running targets to complete.
     *
     * @throws InterruptedException If the wait was interrupted.
     */
    public void waitForAll() throws InterruptedException
    {
        waitForAll(false);
    }

    /**
     * Returns the number of idle threads in the pool.
     *
     * @return int The number of idle threads.
     */
    public int getIdleThreadCount()
    {
        try
        {
            flag.acquire();
            return threads.length - activeThreads;
        }
        finally
        {
            flag.release();
        }
    }

    /**
     * Waits for a ready condition (at least one idle thread).
     *
     * @throws InterruptedException If the calling thread is interrupted.
     */
    public void waitForReady() throws InterruptedException
    {
        waitForReady(0);    // Wait forever.
    }

    /**
     * Waits for a ready condition (at least one idle thread).
     *
     * @throws InterruptedException If the calling thread is interrupted.
     */
    public void waitForReady(long millis) throws InterruptedException
    {
        try
        {
            flag.acquire();             // Get the lock.
            // If there are requests in the queue, or there are no
            // idle threads, wait.
            if ((requestQueue.size() > 0) ||
                    (activeThreads >= threads.length))
            {
                // log.debug("Waiting for ready condition...");
                ready.timedWait(millis);
            }
        }
        finally
        {
            flag.release();
        }
    }

    /**
     * Returns the thread group for the pool.
     *
     * @return ThreadGroup The thread group for the pool.
     */
    public ThreadGroup getGroup()
    {
        return group;
    }

    /**
     * Returns the array of threads in the pool.<br>
     * WARNING: Modifying the state of the threads in the pool may
     * cause the pool to become inconsistent.  Be careful!
     *
     * @return Thread[] - The array of threads the make up the pool.
     */
    public Thread[] getThreads()
    {
        return threads;
    }

    /**
     * Returns the number of requests that are waiting to be processed.
     *
     * @return int The size of the queue.
     */
    public int getQueueSize()
    {
        try
        {
            flag.acquire();
            return requestQueue.size();
        }
        finally
        {
            flag.release();
        }
    }
}
