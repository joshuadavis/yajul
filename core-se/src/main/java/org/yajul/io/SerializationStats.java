package org.yajul.io;

import org.yajul.collections.CollectionUtil;
import org.yajul.comparators.ComparatorChain;
import org.yajul.comparators.ComparatorUtil;
import org.yajul.serialization.SerializableWrapper;
import org.yajul.serialization.SerializationHelper;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serialization statistics helper.
 * <br>User: Joshua Davis
 * Date: Nov 23, 2007
 * Time: 6:08:06 PM
 */
public class SerializationStats {
    private final static Logger log = Logger.getLogger(SerializationStats.class.getName());

    /**
     * Returns the size of the object if it was serialize all by itself.
     *
     * @param object the serializable object
     * @return the number of bytes
     * @throws IOException if something goes wrong
     */
    public static int sizeOf(Serializable object) throws IOException {
        ByteCountingOutputStream counter = new ByteCountingOutputStream(
                new NullOutputStream());
        SerializationHelper.serialize(object, counter);
        return counter.getByteCount();
    }

    /**
     * Counts the number of objects of each class inside the serializable object, and also
     * counts the number of bytes that the object would have in serialized form.
     *
     * @param obj the object
     * @return the statistics.
     */
    public static Stats getStats(Serializable obj) {
        try {
            ByteCountingOutputStream counter = new ByteCountingOutputStream(new NullOutputStream());
            CountingObjectOutputStream oos = new CountingObjectOutputStream(counter);
            oos.writeObject(obj);
            return new Stats(counter.getByteCount(), oos);
        } catch (IOException e) {
            log.log(Level.WARNING, "Unable to compute size of " + obj.getClass().getSimpleName() + " due to : " + e);
            return null;
        }
    }

    /**
     * The total size, and the number of instances of each class in an object.
     */
    public static class Stats {
        private int totalSize;
        private CountingObjectOutputStream oos;

        public Stats(int byteCount, CountingObjectOutputStream oos) {
            this.totalSize = byteCount;
            this.oos = oos;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public Collection<CountingObjectOutputStream.Counter> getCounters() {
            return oos.getCounters();
        }

        public CountingObjectOutputStream.Counter getCounter(String name) {
            return oos.getCounter(name);
        }
    }

    public static final ComparatorChain COUNTER_COMPARATOR = new ComparatorChain(
            new Comparator<CountingObjectOutputStream.Counter>() {
                public int compare(CountingObjectOutputStream.Counter o1, CountingObjectOutputStream.Counter o2) {
                    return ComparatorUtil.compareIntegers(o1.getCount(), o2.getCount());
                }
            },
            new Comparator<CountingObjectOutputStream.Counter>() {
                public int compare(CountingObjectOutputStream.Counter o1, CountingObjectOutputStream.Counter o2) {
                    return ComparatorUtil.NULL_LOW_STRING_COMPARATOR.compare(o1.getName(), o2.getName());
                }
            }
    );

    public static List<CountingObjectOutputStream.Counter> sortCounters(SerializationStats.Stats stats) {
        List<CountingObjectOutputStream.Counter> counters = CollectionUtil.newArrayList(stats.getCounters());
        Collections.sort(counters, COUNTER_COMPARATOR);
        return counters;
    }

    /**
     * Automatically unwrap the object if it implements SerializableWrapper.
     *
     * @param obj The object, or a SerializableWrapper around an object.
     * @return The wrapped object if the argument implements SerializableWrapper, or the argument
     *         object if it doesn't implement SerializableWrapper.
     * @throws java.io.IOException    if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    public static Serializable autoUnwrap(Serializable obj) throws IOException, ClassNotFoundException {
        if (obj instanceof SerializableWrapper) {
            SerializableWrapper wrapper = (SerializableWrapper) obj;
            return wrapper.unwrap();
        } else
            return obj;

    }
}
