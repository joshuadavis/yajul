package org.yajul.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * Calculates the differences between two sets.
 * <br>
 * User: josh
 * Date: 12/13/11
 * Time: 1:54 PM
 */
public class SetDifference<T>
{
    private Set<T> leftOnly;
    private Set<T> rightOnly;
    private Set<T> both;

    public SetDifference(Set<T> leftOnly, Set<T> rightOnly, Set<T> both)
    {
        this.leftOnly = leftOnly;
        this.rightOnly = rightOnly;
        this.both = both;
    }

    public Set<T> getBoth()
    {
        return both;
    }

    public Set<T> getLeftOnly()
    {
        return leftOnly;
    }

    public Set<T> getRightOnly()
    {
        return rightOnly;
    }

    public static <T> SetDifference<T> diff(Set<T> left, Set<T> right)
    {
        Set<T> leftOnly = new HashSet<T>(left.size());
        Set<T> rightOnly = new HashSet<T>(right);
        Set<T> both = new HashSet<T>(left.size());
        for (T x : left)
        {
            if (right.contains(x))
            {
                rightOnly.remove(x);
                both.add(x);
            }
            else
                leftOnly.add(x);
        }
        return new SetDifference<T>(leftOnly,rightOnly,both);

    }
}
