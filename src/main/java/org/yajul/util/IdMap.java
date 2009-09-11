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

    public IdMap() {
        this.map = new HashMap<K, V>();
    }

    public IdMap(Collection<V> items) {
        this();
        putAll(items);
    }

    /**
     * Creates a map as a subset of another map.
     *
     * @param reference the superset map
     * @param ids       the subset of ids for this map
     */
    public IdMap(IdMap<K, V> reference, K[] ids) {
        addSubset(reference, ids);
    }

    /**
     * Adds a subset of the reference map.
     *
     * @param reference the reference map (superset)
     * @param ids       the ids in the subset
     */
    public void addSubset(IdMap<K, V> reference, K[] ids) {
        if (ids.length > 0) {
            this.map = new HashMap<K, V>(ids.length);
            for (K id : ids)
                put(reference.get(id));
        } else
            this.map = Collections.emptyMap();
    }

    public void put(V thing) {
        if (thing == null)
            return;
        K id = thing.getId();
        if (id == null)
            return;
        map.put(id, thing);
    }

    public void aggregate(Collection<V> objects) {
        if (notEmpty(objects)) {
            for (V thing : objects) {
                put(thing);
            }
        }
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

    public Collection<V> getCollection() {
        return values();
    }

    public boolean containsId(K id) {
        return containsKey(id);
    }

    public V getOne() {
        assert map.size() == 1;
        return map.values().iterator().next();
    }

    static boolean notEmpty(Collection summaryCollection) {
        return summaryCollection != null && summaryCollection.size() > 0;
    }

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
        // out.writeObject(map);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map.clear();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings({"unchecked"})
            V v = (V) in.readObject();
            put(v); // Get the key from the object.
        }
        // map = (Map<K, V>) in.readObject();
    }

    public static <K, E extends EntityWithId<K>> Set<K> idSet(Collection<E> things) {
        HashSet<K> set = new HashSet<K>();
        for (E thing : things)
            set.add(thing.getId());

        return set;
    }
}
