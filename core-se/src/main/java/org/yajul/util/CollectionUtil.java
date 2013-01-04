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
     * Null-safe check if the specified collection is empty.
     * <p/>
     * Null returns true.
     *
     * @param coll the collection to check, may be null
     * @return true if empty or null
     */
    public static boolean isEmpty(Collection coll)
    {
        return (coll == null || coll.isEmpty());
    }

    /**
     * Null-safe check if the specified map is empty.
     * <p/>
     * Null returns true.
     *
     * @param m the map to check, may be null
     * @return true if empty or null
     */
    public static boolean isEmpty(Map m)
    {
        return (m == null || m.isEmpty());
    }

    /**
     * Null safe size.
     *
     * @param collection the collection
     * @return the size, or zero if the collection is null
     */
    public static int nullSafeSize(Collection collection)
    {
        return (collection == null) ? 0 : collection.size();
    }

    /**
     * Null safe size.
     *
     * @param map the map
     * @return the size, or zero if the map is null
     */
    public static int nullSafeSize(Map map)
    {
        return (map == null) ? 0 : map.size();
    }

    /**
     * A serializable read-only copy of the key set for the map.
     *
     * @param map the map
     * @return an unmodifiable key set, serializable :)
     */
    public static <K, V> Set<K> unmodifiableKeySet(Map<K, V> map)
    {
        // HashMap.keySet() returns an unserializable object, so you need to make a copy.
        Set<K> keySet = map.keySet();
        keySet = newHashSet(keySet);
        return Collections.unmodifiableSet(keySet);
    }

    /**
     * @param list the list
     * @return an unmodifiable ArrayList copy.
     */
    public static <T> List<T> unmodifiableListCopy(Collection<T> list)
    {
        return Collections.unmodifiableList(newArrayList(list));
    }

    /**
     * @param list      the list
     * @param fromIndex the starting index
     * @param toIndex   the ending index
     * @return an ArrayList copy of the sub list, because list.subList() returns an unserializable object.
     */
    public static <T> List<T> copySubList(List<T> list, int fromIndex, int toIndex)
    {
        // list.subList() returns an unserializable object, so you need to make a copy.
        return newArrayList(list.subList(fromIndex, toIndex));
    }

    /**
     * The union of two lists as an ArrayList.   Doesn't like nulls.
     *
     * @param one the first list
     * @param two the second list
     * @return a new list that contains the elements of the first list followed by the elements of the
     *         second list
     */
    public static <T> List<T> union(List<? extends T> one, List<? extends T> two)
    {
        List<T> list = newArrayList(one);
        list.addAll(two);
        return list;
    }

    /**
     * The union of two sets as an HashSet.   Doesn't like nulls.
     *
     * @param one the first set
     * @param two the second set
     * @return a new set that contains the elements of the first set followed by the elements of the
     *         second set
     */
    public static <T> Set<T> union(Set<? extends T> one, Set<? extends T> two)
    {
        Set<T> set = newHashSet(one);
        set.addAll(two);
        return set;
    }

    /**
     * The union of two maps, as a HashMap.  Does not support null arguments.
     *
     * @param one the first map
     * @param two the second map
     * @return a map containing the union of all keys in both maps.  Values in the second map will replace
     *         values in the first map.
     */
    public static <K, V> HashMap<K, V> union(Map<? extends K, ? extends V> one,
                                             Map<? extends K, ? extends V> two)
    {
        //noinspection unchecked
        return mapUnion(false, one, two);
    }

    /**
     * The union of two maps, as a HashMap.  Null arguments are ignored.
     *
     * @param one the first map
     * @param two the second map
     * @return a map containing the union of all keys in both maps.  Values in the second map will replace
     *         values in the first map.
     */
    public static <K, V> HashMap<K, V> nullSafeUnion(
            Map<? extends K, ? extends V> one,
            Map<? extends K, ? extends V> two)
    {
        //noinspection unchecked
        return mapUnion(true, one, two);
    }

    private static <K, V> HashMap<K, V> mapUnion(boolean nullsOkay, Map<? extends K, ? extends V>... maps)
    {
        HashMap<K, V> map = newHashMap();
        for (Map<? extends K, ? extends V> m : maps)
        {
            if (m == null)
            {
                if (nullsOkay)
                    m = Collections.emptyMap();
                else
                    throw new IllegalArgumentException("arguments cannot be null!");
            }
            map.putAll(m);
        }
        return map;
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
