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
        /** The number of hits on this entry. */
        private int hits;
        /** The system time of the last hit on this entry. */
        private long lastHit;
        /** The system time of the last activation of this entry. */
        private long lastActivation;
        /** The cache that owns this entry. */
        private Cache cache;

        /**
         * Creates a cache entry
         * @param c The cache
         * @param k The key
         */
        Entry(Cache c, Object key)
        {
            this.cache = c;
            this.key = key;
        }

        /**
         * Finalizer: Notifies the cache that this entry is being passivated
         * by the garbage collector (useful when the cache set is using
         * weak references).
         */
        public void finalize()
        {
            // Notify the cache that this entry is being garbage collected.
            if (cache == null)
                log.debug("No cache backpointer.");
            else if (object == null)
                log.debug("No cached object.");
            else if (key == null)
                log.debug("No key.");
            else
                cache.entryFinalized(this);

            this.key = null;
            this.object = null;
            this.cache = null;
        }
    }

    /**
     * A list of keys, in order of use.  The most recently used is first.
     */
    private LinkedList usedKeys;

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
        usedKeys = new LinkedList();
        allRequests = new HashSet();
    }

    /**
     * Increments the request count
     */
    private synchronized void logRequest()
    {
        requests++;
    }

    /**
     * Logs the activation of the key for statistics
     */
    private void logActivation(Object key, Entry entry)
    {
        synchronized (this)
        {
            activations++;
            // If there is a timeout, log the last activation time.
            if (timeout > 0)
            {
                entry.lastActivation = System.currentTimeMillis();
            }

            // Keep track of the unique keys if 'keepStats' is true.
            if (keepStats)
                allRequests.add(key);
        }
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
        logRequest();

        Entry entry = null; // The entry that will point to the value.

        if (parallelActivation)
        {
            // Do a 'synchronized' lookup in the cache.
            synchronized (entryMap)
            {
                entry = find(key);
            }

            // Now, we are unsynchronized.
            // NOTE: Multiple threads may create an entry and invoke the
            // activator simultaneously!!!

            // If there's no entry, make one.
            if (entry == null)
                entry = new Entry(this, key);

            // If an activation is required, do it!
            if (entry.object == null)
                activate(key, entry);
            else
                usedKey(key);
        }
        else
        {
            // Serialized activation... serialize access to the map, so only
            // one thread will be allowed to create a 'blank' entry and add
            // it to the map.  Other threads will wait for the map, and find
            // the blank entry.
            synchronized (entryMap)
            {
                // Find the entry in the map.  Already syncrhonized, so
                // we can use the unsynchronized find method.
                entry = find(key);

                // If there is no entry yet, make one and add it to
                // the map.
                if (entry == null)
                {
                    entry = new Entry(this, key);

                    if (maxSize > 0)
                        addToCollection(key, entry);
                }
            }

            // Now, serialize the threads on the entry that was found / created
            // in the previous step.  Since this is synchronized on the entry,
            // the first thread in will activate the object, and all other
            // threads will wait to enter the block.
            synchronized (entry)
            {
                if (entry.object == null)
                {
                    // Perform the activation, but don't call activate() because
                    // the entry is already in the map.
                    logActivation(key, entry);
                    entry.object = activator.activate(key);    // Activate!
                }
                // Now, the object field is set.  Waiter threads will
                // not activate the object.
            }
        } // else

        return entry.object;
    }

    /**
     * Finds the object with the specified key.  Returns null if one doesn't
     * exits.
     */
    private Entry find(Object key)
    {
        Entry e = (Entry) entryMap.get(key);
        if (e != null)
        {
            e.hits++;
            e.lastHit = System.currentTimeMillis();
            // If there is a timeout, then see if the object needs
            // to be 're-activated'
            if (timeout > 0)
            {
                // If this is *not* the first time this entry
                // is being activated...
                if (e.lastActivation > 0)
                {
                    long age = e.lastHit - e.lastActivation;
                    if (age > timeout)      // If this entry is 'stale'.
                    {
                        timeouts++;
                        // log.debug("Element is " + age +
                        //      "ms old, (stale) passivating...");
                        try
                        {
                            passivate(key, Activator.PASSIVATE_TIMEOUT);
                        }
                        catch (Exception ex)
                        {
                            // TODO: Throw a 'nested exception'.
                            throw new Error(
                                    "Unexpected exception during passivation: "
                                    + ex.getMessage());
                        }
                        return null;
                    } // if (age > timeout)
                } // if (lastActivation > 0)
            }
        }
        return e;
    }

    /**
     * Activates the object with the specified key and adds it to the collection
     * @param key   The key.
     * @param entry The cache entry.
     */
    private void activate(Object key, Entry entry) throws Exception
    {
        logActivation(key, entry);

        // perform the work of activation

        entry.object = activator.activate(key);

        //cache the value
        if (maxSize > 0)
            addToCollection(key, entry);
    }

    /**
     * Adds the specified key/value pair to the cache
     * @param key       The key.
     * @param entry     The cache entry.
     */
    private synchronized void addToCollection(Object key, Entry entry)
            throws Exception
    {
        // Check if the value is already added from a contending thread.
        if (entryMap.get(key) != null)
            return;

        // Shrink the cache if necessary.
        if (entryMap.size() >= maxSize)
        {
            passivate(usedKeys.removeLast(), Activator.PASSIVATE_LRU);
        }

        // Add the key/value to the cache and LRU list
        usedKeys.addFirst(key);
        entryMap.put(key, entry);
    }

    /**
     * Called when a key is used. Moves it to the front of the usedKeys list
     */
    private void usedKey(Object key)
    {
        synchronized (this)
        {
            if (maxSize > 0)
            {
                usedKeys.remove(key);
                usedKeys.addFirst(key);
            }
        }
    }

    /**
     * Clears the cache contents and resets the stats.
     * @throws Exception If there was an error during passivation.
     */
    public void clear() throws Exception
    {
        synchronized (this)
        {
            Iterator iter = entryMap.keySet().iterator();
            while (iter.hasNext())
            {
                Object key = iter.next();
                Entry e = (Entry) entryMap.get(key);
                // Clear the back pointer so that 'finalize' notification is
                // disabled.
                e.cache = null;
                activator.passivate(key, e.object, Activator.PASSIVATE_CLEAR);
            }
            entryMap.clear();
            usedKeys.clear();
            clearStats();
        }
    }

    /**
     * If true, statistics info is kept.
     * @param keepStats True if statistics should be kept, false if not.
     */
    public synchronized void setKeepStats(boolean keepStats)
    {
        keepStats = keepStats;
    }

    /**
     * If true, statistics info is kept.
     * @return boolean True if statistics are being kept, false if not.
     */
    public synchronized boolean getKeepStats()
    {
        return keepStats;
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
     * Entry has been finalized.
     */
    private void entryFinalized(Entry e)
    {
        try
        {
            if (activator == null)
            {
                if (log.isDebugEnabled())
                    log.debug("Entry finalized, but activator is null: " 
                            + "not passivating.");
                return;
            }
            passivate(e.key, Activator.PASSIVATE_FINALIZED);
        }
        catch (Exception ex)
        {
            log.unexpected(ex);
        }
    }

    private void internalPassivate(Entry e, int reason) throws Exception
    {
        if (e.key == null)
            return;
        synchronized (this)
        {
            usedKeys.remove(e.key);
        }
        if (e.object != null)
            activator.passivate(e.key, e.object, reason);
    }

    /**
     * Removes an object from the cache.
     * @param key       The key for the object in the cache.
     * @param reason    The reason for the passivation (Activator.PASSIVATE_xxx
     * values).
     * @throws Exception When there was an error during passivation.
     */
    public void passivate(Object key, int reason) throws Exception
    {
        Entry e = null;
        synchronized (entryMap)
        {
            e = (Entry) entryMap.remove(key);
        }
        if (e != null)
            internalPassivate(e, reason);
    }
}
