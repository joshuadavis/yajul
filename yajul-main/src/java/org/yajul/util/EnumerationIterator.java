package org.yajul.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Enumeration;

/**
 * Adapter that allows an Enumeration to behave lilke an Iterator.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Apr 12, 2003
 * Time: 9:30:19 AM
 */
public class EnumerationIterator implements Iterator
{
    private Enumeration enumeration;

    /**
     * Creates a new EnumerationIterator adapter for the specified enumeration.
     * @param enumeration The inumeration to create an iterator for.
     */
    public EnumerationIterator(Enumeration enumeration)
    {
        this.enumeration = enumeration;
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
        return enumeration.hasMoreElements();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next()
    {
        return enumeration.nextElement();
    }

    /**
     * This implementation always throws Unsupported operation exception.
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Enumerations do not support 'remove()'");
    }
}

