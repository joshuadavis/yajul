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

/**
 * Created by IntelliJ IDEA.
 * User: jdavis
 * Date: Nov 15, 2002
 * Time: 8:46:32 AM
 */
package org.yajul.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * A set of key objects that keeps track of the most recently used and
 * least recently used keys.  Provides specialized methods for moving any
 * element to the top of the MRU list, as well as accessing the most recent and
 * the least recently used elements.
 * <ul>
 * <li>The Set.add() method will add objects as 'most recent'.</li>
 * <li>Collection.addAll() will not guarantee any order of the insertion.  If
 * you need to insert the elements of another set in order, use an iterator and
 * add the elements using the add() method.</li>
 * </ul>
 * TODO: Decide if this should implent List or SortedSet
 * @author Joshua Davis
 */
public class MRUSet extends AbstractSet implements Set
{
    /** A sorted map of the key for each sequence number, sorted by
     * 'sequence number' (sequence number, key). **/
    private TreeMap keys;
    /** A map of the sequence number for each key (key, sequence number). **/
    private HashMap sequenceNumbers;
    /** A cached reference to the LRU sequence number. **/
    private Object lru;
    /** A cached reference to the MRU sequence number. **/
    private Object mru;
    /** The sequence counter. **/
    private int sequence;

    /**
     * Creates a new MRUSet.
     */
    public MRUSet()
    {
        keys = new TreeMap();
        sequenceNumbers = new HashMap();
        mru = lru = null;
        sequence = 0;
    }

    /**
     * Returns the most recently used key.
     * @return Object - The most recently used.
     */
    public Object getMRU()
    {
        // Get the key with the highest sequence number.
        return keys.get(mru);
    }

    /**
     * Returns the least recently used key.
     * @return Object - The least recently used.
     */
    public Object getLRU()
    {
        return keys.get(lru);
    }

    /**
     * Moves the key to the front of the MRU list.
     * @param key The key object.
     * @return Object - Null if 'key' was not in the set, non-null if the key
     * has been made MRU.
     */
    public Object touch(Object key)
    {
        return update(key, UPDATE);
    }

    /**
     * Removes the least recently used entry.
     * @return Object - The least recently used entry, or null if there are
     * no entries.
     */
    public Object removeLRU()
    {
        if (keys.isEmpty())
            return null;
        // Get the sequence number and the LRU key
        Object lruseq = keys.firstKey();
        // Remove the seq # from the (seq->key) sorted map.
        Object lrukey = keys.remove(lruseq);
        // Remove the key from the (key->seq) map.
        sequenceNumbers.remove(lrukey);

        // The mru seq shouldn't have changed unless there are no more keys.
        if (keys.size() == 0)
        {
            mru = null;
            lru = null;
        }
        else
        {
            // The lru seq is the new first key.
            lru = keys.firstKey();
        }
        return lrukey;
    }

    /**
     * Adds a new object (key) as the most recently used.  If the object
     * already exists, then it is promoted to most recently used status.
     * @param key The new key object.
     * @return boolean - True if the key was added, false if it already existed.
     */
    public boolean add(Object key)
    {
        return update(key, INSERT) != null;
    }

    /**
     * Removes the key from the set.
     * @param key The key object.
     * @return boolean - True if the key was removed, false if it didn't exist
     */
    public boolean remove(Object key)
    {
        return update(key, DELETE) != null;
    }

    /**
     * Returns the size of the set.
     * @return int - The size of the set.
     */
    public int size()
    {
        return keys.size();
    }

    /**
     * Removes all entries, releases resources allocated to the implementation.
     */
    public void clear()
    {
        keys.clear();
        sequenceNumbers.clear();
        // Don't bother re-setting sequence.
    }

    /**
     * Returns an iterator that will return the objects in the set, in
     * MRU-first order.
     * @return Iterator - Iterates over the values in the set.
     */
    public Iterator iterator()
    {
        return keys.values().iterator();
    }

    /**
     * Finalize this object.
     * @throws Throwable
     */
    protected void finalize() throws Throwable
    {
        clear();
        super.finalize();
    }

    // --- Implementation ---

    private static final int INSERT = 0;
    private static final int UPDATE = 1;
    private static final int DELETE = 2;

    private Object update(Object key, int operation)
    {
        boolean exists;

        // Find the sequence number for the key, and remove the mapping if
        // it was found.
        Object seq = sequenceNumbers.remove(key);
        // If the key is not in the seq # map,
        if (seq == null)
        {
            exists = false;
            // If not inserting new keys (update or delete),
            if (operation != INSERT)
                return null;            // return null.
        }
        else                            // The key was in the sequence # map.
        {
            exists = true;
            // Remove the existing seq # -> key entry.
            key = keys.remove(seq);
            if (operation == DELETE)        // Deleting?
            {
                // The LRU is the lowest sequence in the key set.
                lru = keys.firstKey();
                // The MRU is the hightest sequence in the key set.
                mru = keys.lastKey();
                return key;                 // Return the old key.
            }
        }

        // Add the key with a new sequence number.
        seq = new Integer(sequence++);      // Get a new sequence #.
        // Add the new key -> sequence # association.
        sequenceNumbers.put(key, seq);
        // Add the new sequence # -> key association.
        keys.put(seq, key);
        // The LRU is the lowest sequence in the key set.
        lru = keys.firstKey();
        // The MRU is (of course) the new key.
        mru = seq;

        if (operation == INSERT)            // If the operation was 'INSERT'
            return exists ? null : key;     // and the key existed, return null.
        else
            return key;
    }
}
