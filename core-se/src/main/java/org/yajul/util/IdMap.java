package org.yajul.util;

import java.io.*;
import java.util.*;

/**
 * Basic set of entities by their id.  Generally entities do not use
 * the surrogate key for equals and hashCode so using Set with natural equality test is
 * not always practical.
 * <br>User: Joshua Davis
 * Date: Nov 9, 2007
 * Time: 10:27:46 PM
 */
public class IdMap<K, V extends EntityWithId<K>> implements Externalizable, Map<K, V> {
    private Map<K, V> map;

    /**
     * Makes an empty IdMap with the default map implementation.
     */
    public IdMap() {
        this(CollectionUtil.<K,V>newLinkedHashMap(), null);
    }

    /**
     * Makes an IdMap using the map provided as the backing store, and adds all the 'items'
     * to it.
     *
     * @param map   the backing map
     * @param items the entities to add
     */
    public IdMap(Map<K, V> map, Collection<V> items) {
        this.map = map;
        if (items != null)
            putAll(items);
    }

    /**
     * Creates the IdMap with the specified backing map.
     *
     * @param map the map implementation
     */
    @SuppressWarnings("UnusedDeclaration")
    public IdMap(Map<K, V> map) {
        this(map, null);
    }

    /**
     * Makes an IdMap using the default backing store, and adds all the 'items'
     * to it.
     *
     * @param items the entities to add
     */
    @SuppressWarnings("UnusedDeclaration")
    public IdMap(Collection<V> items) {
        this(CollectionUtil.<K,V>newLinkedHashMap(items.size()), items);
    }

    /**
     * Creates a map as a subset of another map.
     *
     * @param superSet the superset map
     * @param ids      the subset of ids for this map
     */
    public IdMap(IdMap<K, V> superSet, Collection<K> ids) {
        addSubset(superSet, ids);
    }

    /**
     * Adds a subset of the reference map.
     *
     * @param reference the reference map (superset)
     * @param ids       the ids in the subset
     */
    public void addSubset(IdMap<K, V> reference, Collection<K> ids) {
        final int size = ids.size();
        if (size > 0) {
            this.map = CollectionUtil.newLinkedHashMap(size);
            for (K id : ids)
                put(reference.get(id));
        }
        else
            this.map = Collections.emptyMap();
    }

    /**
     * Adds the entity to the map by it's id.
     *
     * @param thing the entity to add
     * @see EntityWithId<K>.getId()
     */
    public void put(V thing) {
        if (thing == null)
            return;
        K id = thing.getId();
        if (id == null)
            return;
        map.put(id, thing);
    }

    /**
     * Objects are added if their ids don't exist, replaced if the id exists.
     *
     * @param objects the objects to add or replace
     */
    public void aggregate(Iterable<V> objects) {
        if (objects == null)
            return;
        for (V thing : objects)
            put(thing);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public V get(Object key) {
        return map.get(key);
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        map.putAll(t);
    }

    public void putAll(Collection<V> items) {
        for (V v : items)
            put(v.getId(), v);
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return o instanceof Map && map.equals(o);
    }

    public int hashCode() {
        return map.hashCode();
    }

    /**
     * @return the values in the id map, same as Map.values()
     * @see Map#values()
     */
    public Collection<V> getCollection() {
        return values();
    }

    /**
     * @param id the id to look for
     * @return true if the id exists in the map
     * @see Map#containsKey(Object)
     */
    public boolean containsId(K id) {
        return containsKey(id);
    }

    public V getOne() {
        assert map.size() == 1;
        return map.values().iterator().next();
    }

    /**
     * @return the unique ids, same as Map.keySet()
     * @see Map#keySet()
     */
    public Collection<K> getIds() {
        return map.keySet();
    }

    public String toString() {
        if (map.size() == 0)
            return "{}";
        else
            return getClass().getSimpleName() + "{" + map.values() + '}';
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // We don't need to store the keys, just the values.
        out.writeInt(map.size());
        for (V v : map.values()) {
            out.writeObject(v);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map.clear();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings({"unchecked"})
            V v = (V) in.readObject();
            put(v); // Get the key from the object.
        }
    }

    /**
     * Gets the set of unique ids from a bunch of entities.
     *
     * @param things the entities
     * @param <K>    the key type
     * @param <E>    the entity type
     * @return a set of unique ids
     */
    public static <K, E extends EntityWithId<K>> Set<K> idSet(Iterable<E> things) {
        LinkedHashSet<K> set = CollectionUtil.newLinkedHashSet();
        for (E thing : things)
            set.add(thing.getId());
        return set;
    }
}
