

package org.yajul.thread;

/**
 * Provides a semaphore-like nested synchronization object.  A thread can
 * establish multiple locks on the flag.   When they are all released,
 * another thread will be permitted to establish a lock.
 * <br>
 * Created on Aug 28, 2002 11:47:17 AM
 * @author jdavis
 */
public class BusyFlag
{
    /** The current owner of this flag. **/
    private Thread owner = null;
    /** The number of nested locks that the owner has on this flag. **/
    private int count = 0;

    /**
     * The calling thread will wait forever to become the owner of the flag.
     */
    public void acquire()
    {
        synchronized (this)
        {
            // While the flag is not available, wait for notification.
            while (tryLock() == false)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException ignore)
                {
                    // This is okay.
                }

            } // while (the flag is not available)
        } // synchronized
    }

    /**
     * The calling thread will make a single attempt to become the owner of the flag.
     * <ul>
     * <li>If there is no owner for the flag, the current thread becomes the owner, and the lock
     * count is set to one.  This method will return true.</li>
     * <li>If the calling thread is the owner of the flag, the lock count is increased and this
     * method will return true.</li>
     * <li>If the calling thread is not the owner of the flag, the method returns false.</li>
     * </ul>
     * @return boolean True if the calling thread is now the owner of the flag, false if not.
     */
    public boolean tryAcquire()
    {
        synchronized (this)
        {
            return tryLock();
        }
    }

    /**
     * Releases one lock on the flag, if the calling thread is the owner.  If there are no more
     * locks, the next waiter on getBusyFlag() will be allowed to obtain the lock.
     */
    public void release()
    {
        synchronized (this)
        {
            if (owner == Thread.currentThread())    // Calling thread is the owner?
            {
                count--;                            // Decrement the lock count.
                if (count == 0)                     // No more locks?
                {
                    owner = null;                   // The caller is no longer the owner.
                    notify();
                }
            }
        }
    }

    /**
     * Returns the thread that currently owns this flag.
     * @return Thread The owner of this flag, or null if it is not locked.
     */
    public Thread getOwnerThread()
    {
        synchronized (this)
        {
            return owner;
        }
    }

    /**
     * Returns the current number of locks on this flag.
     * @return int - The number of locks.
     */
    public int getLockCount()
    {
        synchronized (this)
        {
            return count;
        }
    }

    // --- Implementation methods ---

    /**
     * Attempts to lock the flag.  This should always be called from
     * within a synchronized block.
     * @return True if the lock was obtained, false if not.
     */
    private boolean tryLock()
    {
        if (owner == null)                      // No owner?
        {
            owner = Thread.currentThread();     // The owner is the calling thread.
            count = 1;                          // Lock count is one.
            return true;                        // The calling thread is the owner.
        }
        if (owner == Thread.currentThread())    // Calling thread is the owner?
        {
            count++;                            // Increment the lock count.
            return true;                        // The calling thread is the owner.
        }
        return false;                           // Otherwise, the calling thread is not the owner.
    }

} // class
