// $Id$
package org.yajul.matrix;

/**
 * TODO: Add class javadoc
 *
 * @author josh Sep 5, 2004 12:45:03 PM
 */
public interface Matrix
{
    void setSize(int[] sizes);

    void setCanGrow(boolean canGrow);

    boolean canGrow();

    void put(int x, Object o);

    void put(int x, int y,Object o);

    void put(int x, int y,int z,Object o);

    void put(int[] coords,Object o);

    Object get(int x);

    Object get(int x,int y);

    Object get(int x,int y,int z);

    Object get(int[] coords);

    int getDimensions();

    int getSize(int dimension);

    /**
     * Returns the sizes of each dimension in an array of ints.  A new array is returned every time so
     * that the internal size array cannot be corrupted.
     * @return the sizes of each dimension in an array of ints.
     */
    int[] getSizes();

    /**
     * Returns the total number of cells in the matrix.
     * @return the total number of cells in the matrix.
     */
    int getTotalSize();
}
