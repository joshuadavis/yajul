/*********************************************************************************
 *   $Header$
 *********************************************************************************/

package org.yajul.util;

import org.yajul.log.Logger;

import java.util.*;

/**
 * Provides a simple object pooling facility.
 * @author Joshua Davis
 */
public class Pool
{
    public static interface Factory
    {
        /**
         * Creates a new object for the pool.
         * @return Object - A new object for the pool.
         */
        public Object create() throws Exception;

        /**
         * The pool notifies the factory that the object is now
         * 'free' using this method.
         * @param o - The object that is now free.
         */
        public void free(Object o) throws Exception;

        /**
         * Recycles (resets the state of) an object that is being returned
         * to the free pool.
         * @param o - The object that will be recycled.
         */
        public void recycle(Object o) throws Exception;
    }

    /** A logger for this class. */
    private static Logger log = Logger.getLogger(Pool.class.getName());

    /** The list of objects are free. */
    private List freePool;
    
    /** The maximum number of objects in the pool. */
    private int max;
    
    /** The number of reserved instances. */
    private int reserved;

    /** The factory for the pool. */
    private Factory factory;

    // -- Statistics --

    /** The number of calls to 'allocate'. **/
    private int allocateCount;

    /** The number of calls to 'free'. **/
    private int freeCount;

    /** The number of calls to the factory.create() method. **/
    private int createCount;

    /**
     * Creates a new pool.
     */
    public Pool(Factory factory,int max)
    {
        this.factory = factory;
        this.max = max;
        this.reserved = 0;
        this.allocateCount = 0;
        this.freeCount = 0;
        freePool = new ArrayList(max);
    }

    /**
     * Reserves an object from the pool.
     * @return Object - A new object from the pool
     * throws InterruptedException - If the thread wait was interrupted.
     */ 
    public Object allocate() throws Exception
    {
        // Get the first object in the set of free objects.
        synchronized(freePool)
        {
            allocateCount++;
            // If the free pool is empty, wait on it.
            if (freePool.size() == 0)
            {
                // If the maximum number of reserved instances has been reached,
                // wait on the free pool.
                if (reserved >= max) 
                    freePool.wait();
                else    // Otherwise, create a new instance!
                {
                    Object o = factory.create();
                    createCount++;
                    freePool.add(o);
                }
            }
            // Remove the next object from the free pool
            Iterator i = freePool.iterator();
            Object o = i.next();
            i.remove();
            // Notify the factory that this object is being recycled.
            factory.recycle(o);
            reserved++;
            return o;
        } // synchronized
    }

    /**
     * Resets the pool statistics.
     */
    public void clearStats()
    {
        synchronized(freePool)
        {
            freeCount = 0;
            allocateCount = 0;
        }
    }

    /**
     * Returns an object into the pool.
     */
    public void free(Object o)
    {
        // Put the object in the free pool.
        synchronized(freePool)
        {
            freeCount++;
            if (freePool.size() > max)
                throw new Error("Maximum pool size exceeded: " + max);
            freePool.add(o);
            if (reserved > 0)       // If there are reserved instances, 
                reserved--;         // decrement the count.
            freePool.notify();      // Unblock the first waiter.
        }
    }
    
    /**
     * Returns the number of objects that are currently reserved.
     */
    public int getReserved()
    {
        synchronized(freePool)
        {
            return reserved;
        }
    }
    
    /**
     * Returns the number of free objects in the pool.
     * @return int - The number of free objects.
     */
    public int getFree()
    {
        synchronized(freePool)
        {
            return freePool.size();
        }
    }
    
    /**
     * Returns the maximum number of objects in the pool.
     * @return int - The maximum number of objects in the pool.
     */
    public int getMax()
    {
        return max;
    }

    /**
     * Statistics : Returns the number of times the 'allocate' method was called.
     * @return int - The number of calls to 'allocate'.
     */
    public int getAllocateCount()
    {
        return allocateCount;
    }

    /**
     * Statistics : Returns the number of calls to 'free'.
     * @return int - The number of calls to 'free'.
     */
    public int getFreeCount()
    {
        return freeCount;
    }

    /**
     * Statistics : Returns the number of calls to the factory 'create' method.
     * @return int - The number of calls to the factory create method.
     */
    public int getCreateCount()
    {
        return createCount;
    }
}

