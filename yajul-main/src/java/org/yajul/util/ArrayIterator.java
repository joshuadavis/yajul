/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 31, 2002
 * Time: 1:09:41 PM
 */
package org.yajul.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for arrays of objects.  Useful in situations where an iterator
 * is required, but an array of objects is all that is available.
 * @author Joshua Davis
 */
public class ArrayIterator implements Iterator
{
    private int index;
    private int maxIndex;
    private Object[] array;

    /**
     * Creates a new iterator for the specified array.
     * @param array     The array to iterate.
     */
    public ArrayIterator(Object[] array)
    {
        this.array = array;
        maxIndex = array.length - 1;
        index = -1;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext()
    {
        return (index < maxIndex);
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception java.util.NoSuchElementException iteration
     * has no more elements.
     */
    public Object next()
    {
        index++;
        if (index < array.length)
            return array[index];
        else
            throw new NoSuchElementException("Array index out of bounds!");
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).
     * <p>Note: Since this class iterates an array, and the size of the array
     * cannot be changed, this method always throws an
     * UnsupportedOperationException.</p>
     * @exception java.lang.UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     */
    public void remove()
    {
        throw new UnsupportedOperationException(
                "ArrayIterator does not support remove()");
    }
}
