package org.yajul.util;

import java.util.Comparator;

/**
 * Helper methods for comparators.
 * <br>
 * User: josh
 * Date: Dec 24, 2009
 * Time: 8:44:13 AM
 */
public class ComparatorUtil {
    public static final Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static int compareIntegers(int i1, int i2) {
        return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
    }
}
