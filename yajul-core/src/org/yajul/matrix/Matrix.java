// $Id$
package org.yajul.matrix;

import java.util.Collection;

/**
 * Describes the behavior of an orthogonal matrix of objects.
 * @author josh Sep 5, 2004 12:45:03 PM
 */
public interface Matrix extends Collection
{
    /**
     * Sets the sizes and number of dimensions in the matrix.  The length of the array is equal to the number of
     * dimensions.   If the size is set larger, null cells will be added.
     * @param sizes the size of each matrix dimension
     */
    void setSize(int[] sizes);

    /**
     * Allows the matrix to grow when put() methods are called.
     * @param canGrow true = matrix will grow when put() methods are called, false = put() methods will throw
     * ArrayIndexOutOfBoundsException.
     */
    void setCanGrow(boolean canGrow);

    /**
     * Returns true if the matrix can grow to fit.
     * @return true if the matrix can grow to fit.
     */
    boolean canGrow();

    /**
     * Puts an object into a one dimensional matrix (Vector).
     * @param x The coordinate to put the object in.
     * @param o The object to put in the matrix.
     */
    void put(int x, Object o);

    /**
     * Puts an object into a two dimensional matrix.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param o The object to put in the matrix.
     */
    void put(int x, int y,Object o);

    /**
     * Puts an object into a three dimensional matrix.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @param o The object to put in the matrix.
     */
    void put(int x, int y,int z,Object o);

    /**
     * Puts an object into the matrix.
     * @param coords The coordinates as an array of integers, one for each dimension.
     * @param o The object to put in the matrix.
     */
    void put(int[] coords,Object o);

    /**
     * Retrieves an object from a one dimensional matrix.
     * @param x The x coordinate.
     * @return The object at that coordinate (IFF the matrix is one dimensional).
     */
    Object get(int x);

    /**
     * Retrieves an object from a two dimensional matrix.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The object at that coordinate (IFF the matrix is two dimensional).
     */
    Object get(int x,int y);

    /**
     * Retrieves an object from a three dimensional matrix.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The object at that coordinate (IFF the matrix is three dimensional).
     */
    Object get(int x,int y,int z);

    /**
     * Retrieves an object from a matrix.
     * @param coords The coordinates as an array of integers, one for each dimension.
     * @return The object at that coordinate.
     */
    Object get(int[] coords);

    /**
     * Returns the number of dimensions.
     * @return the number of dimensions.
     */
    int getDimensions();


    /**
     * Returns the size of the matrix in the specified dimension.
     * @return the size of the matrix in the specified dimension.
     */
    int getSize(int dimension);

    /**
     * Returns the sizes of each dimension in an array of ints.  A new array is returned every time so
     * that the internal size array cannot be corrupted.
     * @return the sizes of each dimension in an array of ints.
     */
    int[] getSizes();
}
