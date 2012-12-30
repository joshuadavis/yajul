package org.yajul.util;

import java.util.Comparator;

/**
 * Reverses the order of another comparator.
 * <br>
 * User: josh
 * Date: Dec 24, 2009
 * Time: 8:29:01 AM
 */
public class ReverseComparator<T> implements Comparator<T> {
    private Comparator<T> comparator;

    public ReverseComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public int compare(T o1, T o2) {
        int rc = comparator.compare(o1,o2);
        return rc != 0 ? rc * -1 : 0;
    }
}
