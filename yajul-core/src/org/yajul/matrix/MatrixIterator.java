// $Id$
package org.yajul.matrix;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates any Matrix implementation.
 * @author josh Sep 7, 2004 8:18:34 AM
 */
public class MatrixIterator implements Iterator
{
    private int[] sizes;
    private int[] coords;
    private int[] nextCoords;
    private boolean hasNext;
    private Matrix matrix;

    public MatrixIterator(Matrix m)
    {
        matrix = m;
        sizes = m.getSizes();
        coords = new int[m.getDimensions()];
        nextCoords = new int[m.getDimensions()];
        hasNext = m.size() > 0;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("MatrixIterator does not support remove()!");
    }

    public boolean hasNext()
    {
        return hasNext;
    }

    /**
     * Returns the current coordinates.
     * @return The current coordinates.
     */
    public int[] getCoords()
    {
        return coords;
    }

    public Object next()
    {
        if (!hasNext)
            throw new NoSuchElementException("Iterator completed, there are no more elements.");
        System.arraycopy(nextCoords,0,coords,0,coords.length);
        Object o = matrix.get(coords);
        hasNext = MatrixUtil.increment(nextCoords,sizes);
        return o;
    }

    /**
     * Increments the coordinates without retrieving an object.
     * @return true if there are more elements, false if not.
     */
    public boolean increment()
    {
        boolean rv = hasNext;
        System.arraycopy(nextCoords,0,coords,0,coords.length);
        hasNext = MatrixUtil.increment(nextCoords,sizes);
        return rv;
    }
}
