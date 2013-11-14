package org.yajul.comparators;

import java.util.Comparator;

/**
 * Comparator that accounts for nulls.
 * <br>
 * User: josh
 * Date: Jun 3, 2010
 * Time: 12:05:25 PM
 */
public class NullComparator<T> implements Comparator<T>
{
    private boolean nullsAreHigh;
    private Comparator<T> nonNullComparator;

    /**
     * Comparator  for null-able values.
     *  @param nullsAreHigh True means <code>null</code> objects are greater than
     *  non-<code>null</code> objects.  False means non-null objects are greater than null.
     * @param nonNullComparator Comparator to use if neither object is null.  Cannot be null.
     */
    public NullComparator(boolean nullsAreHigh, Comparator<T> nonNullComparator)
    {
        if (nonNullComparator == null)
            throw new IllegalArgumentException("nonNullComparator cannot be null!");
        this.nullsAreHigh = nullsAreHigh;
        this.nonNullComparator = nonNullComparator;
    }

    /**
     * Comparator  for null-able values.  Nulls are low.
     * @param nonNullComparator comparator to use if neither object is null.  Cannot be null.
     */
    public NullComparator(Comparator<T> nonNullComparator)
    {
        this(false,nonNullComparator);
    }

    /**
     * If both objects are <code>null</code>, a <code>0</code> value is returned.
     * If one object is <code>null</code> and the other is not, the result depends on the
     * value of <code>nullsAreHigh</code>.
     * If neither object is <code>null</code>, the result comes from the non-null comparator.
     *
     * @param o1  the first object to compare
     * @param o2  the object to compare it to.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     */
    public int compare(T o1, T o2)
    {
        if(o1 == o2) { return 0; }
        if(o1 == null) { return (nullsAreHigh ? 1 : -1); }
        if(o2 == null) { return (nullsAreHigh ? -1 : 1); }
        return nonNullComparator.compare(o1, o2);
    }
}
