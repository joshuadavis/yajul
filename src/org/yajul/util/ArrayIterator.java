/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Aug 31, 2002
 * Time: 1:09:41 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for primitive arrays.
 */
public class ArrayIterator implements Iterator
{
    private int index;
    private Object[] array;

    /**
     * Creates a new iterator for the specified array.
     * @param array     The array to iterate.
     */
    public ArrayIterator(Object[] array)
    {
        this.array = array;
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
        return (index < array.length);
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
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
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("ArrayIterator does not support remove()");
    }
}
