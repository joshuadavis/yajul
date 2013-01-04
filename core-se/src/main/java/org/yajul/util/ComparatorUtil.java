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
    public static final Comparator<Comparable> COMPARABLE_COMPARATOR = new Comparator<Comparable>() {
        public int compare(Comparable o1, Comparable o2) {
            //noinspection unchecked
            return o1.compareTo(o2);
        }
    };

    public static <T> Comparator<T> comparableComparator() {
        //noinspection unchecked
        return (Comparator<T>) COMPARABLE_COMPARATOR;
    }

    public static final Comparator<Comparable> NULL_LOW_COMPARATOR =
            new NullComparator<Comparable>(COMPARABLE_COMPARATOR);

    public static <T> Comparator<T> nullLowComparableComparator() {
        //noinspection unchecked
        return (Comparator<T>) NULL_LOW_COMPARATOR;
    }

    public static final Comparator<String> STRING_COMPARATOR = comparableComparator();

    public static final Comparator<String> NULL_LOW_STRING_COMPARATOR = nullLowComparableComparator();

    public static final Comparator<String> NULL_EQUALS_EMPTY_STRING_COMPARATOR = new Comparator<String>() {
        public int compare(String a, String b) {
            return STRING_COMPARATOR.compare(
                    StringUtil.nullAsEmpty(a),
                    StringUtil.nullAsEmpty(b));
        }
    };

    public static int compareIntegers(int i1, int i2) {
        return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
    }

    public static boolean nullSafeEquals(Object a, Object b) {
        return (a == null && b == null) ||
                (a != null && b != null && a.equals(b));
    }

    public static int nullSafeCompare(Double a, Double b) {
        if (a == null && b == null)
            return 0;
        else if (a == null)
            return -1;
        else if (b == null)
            return 1;
        return Double.compare(a, b);
    }

}
