/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/

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
    private int minIndex;
    private int maxIndex;
    private Object[] array;
    private boolean skipNulls;

    /**
     * Creates a new iterator for the specified array.
     * @param array     The array to iterate.
     */
    public ArrayIterator(Object[] array)
    {
        this(array,0,array.length - 1,false);
    }

    /**
     * Creates a new iterator for the specified array that can skip null elements.
     * @param array     The array to iterate.
     * @param skipNulls True to skip null elements, false will include null elements.
     */
    public ArrayIterator(Object[] array,boolean skipNulls)
    {
        this(array,0,array.length - 1,skipNulls);
    }

    /**
     * Creates a new iterator for the specified array, which will iterate from the start to the end
     * index inclusive, skipping null elements if necessary.
     * @param array     The array to iterate.
     * @param startIndex The starting index (inclusive).
     * @param maxIndex  The ending index (inclusive).
     * @param skipNulls True to skip null elements, false will include null elements.
     */
    public ArrayIterator(Object[] array,int startIndex,int maxIndex,boolean skipNulls)
    {
        this.minIndex = startIndex;
        this.index = startIndex - 1;
        this.maxIndex = maxIndex;
        this.array = array;
        this.skipNulls = skipNulls;

        // If we are skipping nulls, advance 'index' to immediately precede the first non-null element.
        if (skipNulls)
            advance();
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
        if (index <= maxIndex)
        {
            if (skipNulls)
            {
                Object rv = array[index];   // Return the current element.
                advance();                  // Advance to just before the next non-null element.
                return rv;
            }
            else                            // Otherwise, just return the current element.
                return array[index];
        }
        else
            throw new NoSuchElementException("Array index "
                    + index + " out of bounds (min = "
                    + minIndex + ", max = " + maxIndex +")!");
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

    /** Advance 'index' to just before the first non-null element. **/
    private void advance()
    {
        for (int i = index + 1; i <= maxIndex ; i++)
        {
            if (array[i] != null)
            {
                index = i - 1;
                return;         // Found a non-null element, stop processing now!
            }
        } // for
        // Advanced past the end, stop the iteration by setting index to 'maxIndex'.
        index = maxIndex;
    }
}
