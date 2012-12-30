package org.yajul.util;

import java.util.*;

/**
 * Helper methods for collections.
 * <br>
 * User: josh
 * Date: 12/30/12
 * Time: 12:41 PM
 */
public class CollectionUtil {

    /**
     * Add all of the elements in the iterator to the collection.
     * @param iter the iterator
     * @param collection the collection to add the elements to
     * @param <E> the element type
     * @return the collection, with all the elements added
     */
    public static <E> Collection<E> addAll(Iterator<? extends E> iter, Collection<E> collection) {
        while (iter.hasNext()) {
            collection.add(iter.next());
        }
        return collection;
    }

    /**
     * Creates a new array list.
     * @param <E> the element type
     * @return a new array list
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * Creates a new array list with an initial capacity.
     * @param initialCapacity the initial capacity.
     * @param <E> the element type
     * @return a new array list
     */
    public static <E> ArrayList<E> newArrayList(int initialCapacity) {
        return new ArrayList<E>(initialCapacity);
    }

    /**
     * Create a new array list from an iterator
     * @param iter the iterator
     * @param <E> the element type
     * @return a new array list
     */
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> iter) {
        ArrayList<E> list = newArrayList();
        addAll(iter, list);
        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> iterable) {
        return newArrayList(iterable.iterator());
    }

    /**
     * Creates a new hash map.
     * @param <K> key class
     * @param <V> value class
     * @return a new hash map.
     */
    public static <K,V> HashMap<K,V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * Creates a new hash map with an initial capacity
     * @param initialCapacity initial capacity
     * @param <K> key class
     * @param <V> value class
     * @return a new hash map.
     */
    public static <K, V> HashMap<K,V> newHashMap(int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * Creates a new linked hash map.
     * @param <K> key class
     * @param <V> value class
     * @return a new linked hash map.
     */
    public static <K,V> LinkedHashMap<K,V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * Creates a new linked hash map with an initial capacity
     * @param initialCapacity initial capacity
     * @param <K> key class
     * @param <V> value class
     * @return a new linked hash map.
     */
    public static <K, V> LinkedHashMap<K,V> newLinkedHashMap(int initialCapacity) {
       return new LinkedHashMap<K, V>(initialCapacity);
    }

    /**
     * Creates a new hash set.
     * @param <E> element class
     * @return a new hash set
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    /**
     * Creates a new hash set with an initial capacity
     * @param initialCapacity initial capacity
     * @param <E> element class
     * @return a new hash set
     */
    public static <E> HashSet<E> newHashSet(int initialCapacity) {
        return new HashSet<E>(initialCapacity);
    }

    /**
     * Creates a new hash set with elements from the collection
     * @param collection the collection
     * @param <E> the element type
     * @return a new hash set with the elements from the collection
     */
    public static <E> HashSet<E> newHashSet(Collection<? extends E> collection) {
        HashSet<E> set = newHashSet(collection.size());
        addAll(collection.iterator(),set);
        return set;
    }

    /**
     * Creates a new linked hash set.
     * @param <E> element class
     * @return a new linked hash set
     */
    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<E>();
    }

}
