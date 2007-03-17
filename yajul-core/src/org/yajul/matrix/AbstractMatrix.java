// $Id$
package org.yajul.matrix;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides common getters and setters for matrix implementations.
 *
 * @author josh Sep 5, 2004 12:49:30 PM
 */
public abstract class AbstractMatrix implements Matrix
{
    protected int[] sizes;
    protected boolean canGrow;
    // Temporary arrays for convenience getters and setters.
    private int[] tempCoords1d;
    private int[] tempCoords2d;
    private int[] tempCoords3d;

    public AbstractMatrix()
    {
        sizes = new int[]{0};
        canGrow = true;
    }

    public void setSize(int[] sizes)
    {
        this.sizes = new int[sizes.length];         // Copy the sizes.
        System.arraycopy(sizes, 0, this.sizes, 0, sizes.length);
    }

    public void setCanGrow(boolean canGrow)
    {
        this.canGrow = canGrow;
    }

    public boolean canGrow()
    {
        return this.canGrow;
    }

    public void put(int x, Object o)
    {
        setTempCoords(x);
        put(tempCoords1d, o);
    }

    public void put(int x, int y, Object o)
    {
        setTempCoords(x, y);
        put(tempCoords2d, o);
    }

    public void put(int x, int y, int z, Object o)
    {
        setTempCoords(x, y, z);
        put(tempCoords3d, o);
    }

    public Object get(int x)
    {
        setTempCoords(x);
        return get(tempCoords1d);
    }

    public Object get(int x, int y)
    {
        setTempCoords(x, y);
        return get(tempCoords2d);
    }

    public Object get(int x, int y, int z)
    {
        setTempCoords(x, y, z);
        return get(tempCoords3d);
    }

    public int getDimensions()
    {
        return sizes.length;
    }

    public int getSize(int dimension)
    {
        if (dimension > sizes.length || dimension < 0)
            throw new IllegalArgumentException("Invalid dimension: " + dimension);
        return sizes[dimension];
    }

    public int[] getSizes()
    {
        int[] s = new int[sizes.length];
        System.arraycopy(sizes, 0, s, 0, s.length);
        return s;
    }

    public void clear()
    {
        // TODO: Implement this method.
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public Object[] toArray()
    {
        // TODO: Implement this method.
        return new Object[0];
    }

    public boolean add(Object o)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean contains(Object o)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean remove(Object o)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean addAll(Collection c)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean containsAll(Collection c)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean removeAll(Collection c)
    {
        // TODO: Implement this method.
        return false;
    }

    public boolean retainAll(Collection c)
    {
        // TODO: Implement this method.
        return false;
    }

    public Iterator iterator()
    {
        return new MatrixIterator(this);
    }

    public Object[] toArray(Object a[])
    {
        // TODO: Implement this method.
        return new Object[0];
    }

    public int size()
    {
        return MatrixUtil.totalSize(sizes);
    }

    protected void checkCoords(int[] coords)
    {
        MatrixUtil.checkCoords(coords, sizes);
    }

    private void setTempCoords(int x)
    {
        if (tempCoords1d == null)
            tempCoords1d = new int[1];
        tempCoords1d[0] = x;
    }

    private void setTempCoords(int x, int y)
    {
        if (tempCoords2d == null)
            tempCoords2d = new int[2];
        tempCoords2d[0] = x;
        tempCoords2d[1] = y;
    }

    private void setTempCoords(int x, int y, int z)
    {
        if (tempCoords3d == null)
            tempCoords3d = new int[3];
        tempCoords3d[0] = x;
        tempCoords3d[1] = y;
        tempCoords3d[2] = z;
    }

    protected void beforePut(int[] coords)
    {
        if (coords.length < sizes.length)
            throw new ArrayIndexOutOfBoundsException("Coordinates must have at least " + sizes.length + " dimensions!");

        if (canGrow)
        {
            if (coords.length > sizes.length)
                growToFit(coords);
            else
            {
                for (int i = 0; i < coords.length; i++)
                {
                    int coord = coords[i];
                    if (coord >= sizes[i])
                    {
                        growToFit(coords);
                        break;
                    }
                }
            }
        }
    }

    private void growToFit(int[] coords)
    {
        int[] newsizes = new int[coords.length];
        for (int i = 0; i < sizes.length; i++)
            newsizes[i] = coords[i] >= sizes[i] ? coords[i] + 1 : sizes[i];
        for (int i = sizes.length; i < coords.length; i++)
            newsizes[i] = coords[i] + 1;
        setSize(newsizes);
    }

}
