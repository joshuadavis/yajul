package org.yajul.io;

import org.apache.commons.collections.map.UnmodifiableMap;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Counts the number of unique objects, totals by class.
 * <br>
 * User: josh
 * Date: Sep 4, 2009
 * Time: 4:32:24 PM
 */
public class CountingObjectOutputStream extends ObjectOutputStream {
    private Map<String, Counter> counters = new HashMap<String, Counter>();

    public CountingObjectOutputStream(OutputStream out) throws IOException {
        super(out);
        enableReplaceObject(true);
    }

    public CountingObjectOutputStream() throws IOException {
        super();
    }

    @Override
    protected Object replaceObject(Object obj) throws IOException {
        countObject(obj);
        return super.replaceObject(obj);
    }

    public void countObject(Object obj) {
        String className = obj.getClass().getName();
        Counter counter = counters.get(className);
        if (counter == null) {
            counter = new Counter(className);
            counters.put(className, counter);
        }
        counter.increment();
    }

    public Collection<Counter> getCounters() {
        return counters.values();
    }

    public Counter getCounter(String className) {
        return counters.get(className);
    }

    public static class Counter implements Comparable<Counter> {
        private String name;
        private int count;

        public Counter(String className) {
            name = className;
        }

        @Override
        public String toString() {
            return "{" +
                    "class='" + name + '\'' +
                    ", count=" + count +
                    '}';
        }

        public void increment() {
            count++;
        }

        public int compareTo(Counter o) {
            if (count == o.count)
                return name.compareTo(o.name);
            else
                return count - o.count;
        }

        public int getCount() {
            return count;
        }

        public String getName() {
            return name;
        }
    }
}
