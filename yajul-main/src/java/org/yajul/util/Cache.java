/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/

package org.yajul.util;

import org.yajul.log.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Cache provides a mechanism for storing a map of objects, which are activated
 * when needed.  This is implemented as an in-memory hash lookup table, with a
 * size-limited LRU list.
 * <ul>Options:
 * <li>Parallel activation - This flag allows multiple thread accessing the same
 * cache key to invoke the activator simultaneously.  Only one of the activated
 * objects will be placed in the cache, however.</li>
 * <li>Timeout - Entries will be re-activated (passivated, then activated) when
 * the entry has not been activated since the specified timeout.  This is useful
 * when the contents of the activator (backing store) change, making the cache
 * 'stale'.</li>
 * <li>Statistics - The cache can keep a list of all keys that have been asked
 * for.</li>
 * </ul>
 * @author Joshua Davis
 * @author Kent Vogel
 */
public class Cache
{
    /**
     * Activator provides a mechanism for an instance of Cache to activate
     * objects.
     * @see Cache
     */
    public interface Activator
    {
        /** The object is being passivated because it is the least recently
         * used. */
        public static final int PASSIVATE_LRU = 0;
        /** The object is being passivated because of a timeout. */
        public static final int PASSIVATE_TIMEOUT = 1;
        /** The object is being passivated because the cache entry has been
         * finalized. */
        public static final int PASSIVATE_FINALIZED = 2;
        /** The object is being passivated because the cache has been
         * cleared. */
        public static final int PASSIVATE_CLEAR = 3;

        /**
         * Activates the object associated with the specified information (for
         * example, read the object from persistent storage).
         * @param key   The key object that has enough information for the
         * activator to create a new object.
         * @return The newly activated object.
         * @throws Exception Something went wrong during the activation.
         */
        Object activate(Object key) throws Exception;

        /**
         * Passivates the object, indicating that it is no longer in the cache.
         * The object may want to release resources at this point.
         * @param key   The key object that has enough information for the
         * activator to create a new object.
         * @param obj   The object that is being removed from the cache.
         * @param reason    The reason the object is being passivated
         * (PASSIVATE_xxx values).
         * @throws Exception Something went wrong during the passivation.
         */
        void passivate(Object key, Object obj, int reason) throws Exception;
    }

    /** A logger for this class. */
    private static Logger log = Logger.getLogger(Cache.class.getName());

    /** Inner class that is the intermediate object that points to the actual
     * cached objects. **/
    private static class Entry
    {
        /** A logger for this class. */
        private static Logger log = Logger.getLogger(Entry.class.getName());
        /** The key for this entry. */
        private Object key;
        /** The cached object. */
        private Object object;
        /** The system time of the last hit on this entry. */
        private long lastHit;
        /** The system time of the last activation of this entry. */
        private long lastActivation;
        /** The cache that owns this entry. */
        private Cache cache;
        /** True if the entry has been activated. **/
        private boolean active;

        /**
         * Creates a cache entry
         * @param c The cache
         * @param key The key
         */
        Entry(Cache c, Object key)
        {
            this.cache = c;
            this.key = key;
            active = false;
        }

        boolean isStale()
        {
            if (cache == null)
                return true;

            long timeout = cache.getTimeout();
            // Timeout of <= 0 means no timeout.
            if (timeout <= 0)
                return false;   // Always fresh!

            return (System.currentTimeMillis() - lastActivation) > timeout;
        }

        void activate() throws Exception
        {
           Activator activator = null;

            synchronized (this)
            {
                if (cache == null)
                    throw new IllegalStateException("No cache backpointer!");
                if (active)             // Already active,
                    return;             // so don't activate.
                // Log the activation time.
                lastActivation = System.currentTimeMillis();
                activator = cache.getActivator();
                if (activator == null)
                    throw new IllegalStateException("No activator!");

                if (!cache.allowsParallelActivation())
                {
                    object = activator.activate(key);
                    active = true;
                    return;
                }

            }

            // The active flag should be false here, which will prevent
            // other threads from passivating while the activation takes
            // place.

            // Parallel activation...
            Object obj = activator.activate(key);

            // Serialize thread access again to set the fields.
            synchronized(this)
            {
                object = obj;
                active = true;      // Allows passivation.
            }
        }

        void passivate(int reason) throws Exception
        {
            Activator activator = null;

            // First, check if the entry is already passive.
            synchronized (this)
            {
                if (cache == null)
                    throw new IllegalStateException("No cache backpointer!");
                if (!active)            // Already passive,
                    return;             // so don't passivate.
                activator = cache.getActivator();
                if (activator == null)
                    throw new IllegalStateException("No activator!");
                Object obj = object;
                object = null;
                active = false;     // Other threads won't passivate this now.

                // Passivate in the synchronized block to avoid duplicates.
                activator.passivate(key,obj,reason);
            }
        }

        /**
         * Finalizer: Notifies the cache that this entry is being passivated
         * by the garbage collector (useful when the cache set is using
         * weak references).
         */
        protected void finalize() throws Throwable
        {
            passivate(Activator.PASSIVATE_FINALIZED);
            key = null;
            object = null;
            cache = null;
        }
    }

    /**
     * A list of keys, in order of use.  The most recently used is first.
     */
    private MRUSet keys;

    /**
     * A map of the cached object entries (Entry) by key.
     */
    private Map entryMap;

    /**
     * The maximum number of objects this cache will have in it before
     * it removes objects.  This is the maximum size of the LRU list.
     */
    private int maxSize;

    /**
     * The cache timeout, in millilseconds.  If zero, there will be no timeout.
     */
    private long timeout;
    /**
     * An interface to something that provides acitvation of objects.  This is
     * a 'factory', that produces objects based on a key.
     * @see Activator
     */
    private Activator activator;

    /** If true, parallel activation is allowed.  Otherwise,
     * object activation is serialized. */
    private boolean parallelActivation;

    /** If true, statistics are kept. */
    private boolean keepStats;

    //statistics
    private int activations;
    private int requests;
    private int timeouts;
    private Set allRequests;  // A set of all keys requested so far.

    /**
     * Creates a new cache with the specified size.  Object are activated by
     * the given activator interface.
     *
     * @param activator The interface responsible for activating objects on
     * cache misses.
     * @param maxSize The maximum number of objects this cache will have in
     * it before it removes objects. maxSize=0 is allowed.
     */
    public Cache(Activator activator, int maxSize)
    {
        this(activator, maxSize,
                0, // No timeout.
                true, // Allow parallel activation.
                false, // Don't keep statistics.
                new HashMap(maxSize)); // Default map.
    }

    /**
     * Creates a new cache with the specified size.  Object are activated by
     * the given activator interface.
     *
     * @param activator The interface responsible for activating objects on
     * cache misses.
     * @param maxSize The maximum number of objects this cache will have in it
     * before it removes objects. maxSize=0 is allowed.
     * @param timeout The maximum age of an object in the cache.  Objects
     * older than this will be passivated (when requested).
     * @param parallelActivation Iff false, threads will wait for an object
     * to be activated if it is already being activated when get() is called.
     * For any non-trival activation, it will always be faster to set this to
     * false.
     * @param keepStats If true, cache hit rate statistics will be kept.
     * @param map The map that will be used to keep all of the entries.
     */
    public Cache(Activator activator, int maxSize, long timeout,
                 boolean parallelActivation, boolean keepStats, Map map)
    {
        this.activator = activator;
        this.maxSize = maxSize;
        this.parallelActivation = parallelActivation;
        this.keepStats = keepStats;
        this.timeout = timeout;
        // Clear the stats.
        doClearStats();

        log.debug("maxSize = " + maxSize + " timeout = " + timeout);

        entryMap = map;
        keys = new MRUSet();
        allRequests = new HashSet(0);
    }
    //statistics methods

    /** Gets the maximum size the cache can grow to
     * @return int The maximum cache size. */
    public int getMaxSize()
    {
        return maxSize;
    }

    /** Gets the current size of the cache.
     * @return int The current cache size. */
    public int getCurrentSize()
    {
        return entryMap.size();
    }

    /** Gets the number of times an activation had to be perfomed (a cache
     * miss).
     * @return int The number of activations. */
    public int getActivations()
    {
        return activations;
    }

    /** Gets the number request made of the cache.
     * @return int The number of requests made. */
    public int getRequests()
    {
        return requests;
    }

    /** Gets the number unique keys asked for, this will allways be zero if
     * 'keepStats' is false.
     * @return int The number of unique requests. */
    public int getUniqueRequests()
    {
        return allRequests.size();
    }

    /** Gets the number of 'timeout' passivations (zero, unless a timeout was
     * specified).
     * @return int The number of passivations due to a timeout condition. **/
    public int getTimeouts()
    {
        return timeouts;
    }

    /** Gets the rate at which requested object were available in the cache
     * [0..1]
     * @return double The hit rate of the cache (requests - activations) /
     * requests **/
    public double getHitRate()
    {
        double dreq = (double) requests;
        double dact = (double) activations;
        return ((dreq - dact)) / dreq;
    }

    /** Gets the rate at which requested objects timed out [0..1]
     * @return double The ration of requests to timeouts (requests - timeouts)
     * / requests **/
    public double getTimeoutRate()
    {
        double dreq = (double) requests;
        double dto = (double) timeouts;
        return ((dreq - dto)) / dreq;
    }

    /**
     * Returns the activator being used by this cache.
     * @return Activator - The activator for this cache.
     */
    public Activator getActivator()
    {
        return activator;
    }

    /**
     * True if this cache allows simultaneous, redundant activation of the same
     * key by multiple threads.
     * @return boolean - True if parallel activation is allowed.
     */
    public boolean allowsParallelActivation()
    {
        return parallelActivation;
    }

    /**
     * Returns the timeout of active elements in milliseconds.
     * @return long - Milliseconds an element is allowed to be active.
     */
    public long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    /**
     * If true, statistics info is kept.
     * @param keepStats True if statistics should be kept, false if not.
     */
    public void setKeepStats(boolean keepStats)
    {
        synchronized (this)
        {
            this.keepStats = keepStats;
        }
    }

    /**
     * If true, statistics info is kept.
     * @return boolean True if statistics are being kept, false if not.
     */
    public boolean getKeepStats()
    {
        synchronized (this)
        {
            return keepStats;
        }
    }

    /**
     * Clears the statistics
     */
    public synchronized void clearStats()
    {
        allRequests.clear();
        doClearStats();
    }

    private void doClearStats()
    {
        activations = 0;
        requests = 0;
        timeouts = 0;
    }

    /**
     * Gets the object associated to the given key.  Activates a new one if
     * necessary.
     * @param key   The key
     * @return Object   The cached object.
     * @throws Exception If there was an activation / passivation error.
     */
    public Object get(Object key) throws Exception
    {

        Entry entry = null;     // The entry that will point to the value.
        Entry lru = null;       // Least recently used entry.
        boolean found = true;
        boolean stale = false;

        // Find the entry.
        synchronized (this)
        {
            requests++;

            if (keepStats)                  // Keep track of unique keys,
                allRequests.add(key);       // if required.

            entry = (Entry)entryMap.get(key);
            if (entry != null)              // Entry found?
            {
                keys.touch(key);            // Make the key the MRU.

                stale = entry.isStale();

                if (entry.active && !stale) // Entry active & not stale?
                    return entry.object;    // HIT! Return the entry.

                if (stale)                  // Keep track of timeouts.
                    timeouts++;
            }
            else                            // Entry not found?
            {
                found = false;
                entry = new Entry(this,key);        // Make a new one.
                if (entryMap.size() >= maxSize)     // Overflow?
                {
                    // Remove the LRU key.
                    Object lruKey = keys.removeLRU();
                    // Remove the LRU entry from the map.
                    lru = (Entry)entryMap.remove(lruKey);
                }
            }

            // The entry will be activated, so keep track of it here.
            activations++;
        } // synchronized

        // Threads are unserialized now.  The methods in entry will re-serialize
        // them when necessary around the entry itself so that multiple threads
        // may process multiple keys in parallel.

        // The entry is either 1) passive, 2) stale, 3) new with no overflow
        // or 4) new, with overflow.

        if (found && stale)
        {
            // Case 2: The entry existed, and was stale.
            // Passivate, then activate.
            entry.passivate(Activator.PASSIVATE_TIMEOUT);
        }
        else if ((!found) && (lru != null))
        {
            // Case 4: This is a new entry, and there is an overflow.
            // Passivate the LRU entry.
            lru.passivate(Activator.PASSIVATE_LRU);
        }

        // Activate the entry.
        entry.activate();

        // Add the entry to the map, if it wasn't found.
        if (!found)
        {
            synchronized (this)
            {
                keys.add(key);                      // Add as the MRU!
                entryMap.put(key,entry);
            }
        }

        return entry.object;
    }

    /**
     * Clears the cache contents and resets the stats.
     * @throws Exception If there was an error during passivation.
     */
    public void clear() throws Exception
    {
        synchronized (this)
        {
            entryMap.clear();  // Entries will be finalized.
            keys.clear();
            clearStats();
        }
    }
}
