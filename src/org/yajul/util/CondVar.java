

package org.yajul.util;

/**
 * Provides a POSIX-like condition variable, for the purposes of synchronizing threads.  This allows
 * a more flexible relationship between mutex locks and notification than the standard wait() and notify() methods
 * providied by Java.
 * <b>IMPORTANT NOTE:</b><br>
 * DO NOT USE THE wait() METHOD ON INSTANCES OF CondVar!!!!<br>
 * Use the cvWait(), or timedWait() methods!
 * <br>
 * Created by IntelliJ IDEA on Aug 28, 2002 1:14:40 PM
 * @author jdavis
 */
public class CondVar
{
    private BusyFlag syncFlag;

    /**
     * Creates a new CondVar, given a synchronization mutex.
     * @param syncFlag The object to use for locking.
     */
    public CondVar(BusyFlag syncFlag)
    {
        // --- Preconditions ---
        if (syncFlag == null)
            throw new IllegalArgumentException("A synchronization flag is required!");
        // --- End of preconditions ---
        this.syncFlag = syncFlag;
    }

    /**
     * Waits for the signal on this object.
     * <ol>
     * <li>Releases the underlying mutex, completely.</li>
     * <li>Waits for some other thread to signal.</li>
     * <li>Restores all of the locks on the mutex.</li>
     * </ol>
     * @param flag The flag that must be released before the signal, and re-acquired after the signal.
     * @param millis The number of milliseconds to wait.  Waits forever if this is zero.
     * @throws InterruptedException If the calling thread was interrupted while waiting.
     */
    public void timedWait(BusyFlag flag, long millis) throws InterruptedException
    {
        // --- Preconditions ---
        if (syncFlag == null)
            throw new IllegalArgumentException("A synchronization flag is required!");
        // --- End of preconditions ---

        int numberOfLocks = 0;
        InterruptedException interruptedException = null;

        synchronized (this)
        {
            ensureCurrentThreadOwnsFlag(flag);

            // While the owner thread is this thread, release all of the locks.
            while (flag.getOwnerThread() == Thread.currentThread())
            {
                numberOfLocks++;                // Remember how many locks were released.
                flag.release();
            }

            // The flag is now available.

            // Now, wait for the signal.
            try
            {
                if (millis == 0)                // Wait forever if required.
                {
                    wait();
                }
                else                            // Or, wait for the specified # of milliseconds.
                {
                    wait(millis);
                }
            }
            catch (InterruptedException e)
            {
                // Remember the exception.
                interruptedException = e;
            }
        } // End synchronized block,  waiting is over.

        // Either the signal happened, or the thread was interrupted.

        // Re-acquire all of the locks that were released.
        for (; numberOfLocks > 0; numberOfLocks--)
        {
            flag.acquire();
        }

        // If the thread was interrupted, throw the interrupted exception.
        if (interruptedException != null)
            throw interruptedException;
    }

    /**
     * Waits for the signal on this object using the flag establised in the constructor.
     * @see timedWait(BusyFlag,long)
     */
    public void timedWait(long millis) throws InterruptedException
    {
        timedWait(syncFlag, millis);
    }

    /**
     * Waits for the signal forever.
     * @see timedWait(BusyFlag,long)
     */
    public void cvWait() throws InterruptedException
    {
        timedWait(0);
    }

    /**
     * Throws an exception if the current thread does not own the flag.
     * @param flag The flag to check.
     */
    private void ensureCurrentThreadOwnsFlag(BusyFlag flag)
    {
        // Only the owner of the flag can wait for the cond var.
        if (flag.getOwnerThread() != Thread.currentThread())
            throw new IllegalMonitorStateException(
                    "Current thread '" + Thread.currentThread().getName() +
                    "' is not the owner of the lock!");
    }

    /**
     * Signals the waiter using the flag specified in the constructor.
     * @see signal(BusyFlag)
     */
    public void signal()
    {
        signal(this.syncFlag);
    }

    /**
     * Signals one and only one waiter.  The calling thread must have acquired a lock on the flag
     * before calling this.
     * @param flag The locked flag.
     */
    public void signal(BusyFlag flag)
    {
        doNotify(flag, false);
    }

    /**
     * Notifies any waiters.
     */
    private void doNotify(BusyFlag flag, boolean all)
    {
        // --- Preconditions ---
        if (flag == null)
            throw new IllegalArgumentException("A synchronization flag is required!");
        // --- End of preconditions ---

        synchronized (this)
        {
            ensureCurrentThreadOwnsFlag(flag);
            if (all)
                notifyAll();    // Wake up all waiters.
            else
                notify();       // Wake up one waiter.
        }
    }

    /**
     * Signals all waiters.
     * @param flag The locked flag.
     */
    public void broadcast(BusyFlag flag)
    {
        doNotify(flag, true);
    }

    /**
     * Signals all waiters using the flag from the constructor.
     */
    public void broadcast()
    {
        broadcast(syncFlag);
    }
}
