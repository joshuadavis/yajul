// $Id$
package org.yajul.matrix;

/**
 * Provides utility methods for working with Matrix implementations.
 * @author josh Sep 6, 2004 10:59:31 AM
 */
public class MatrixUtil
{
    public static final int totalSize(int[] sizes)
    {
        int total = sizes[0];
        for (int i = 1; i < sizes.length; i++)
            total = sizes[i] * total;
        return total;
    }

    public static final void checkCoords(int[] coords,int[] sizes)
    {
        if (coords.length > sizes.length)
            throw new ArrayIndexOutOfBoundsException("Invalid dimension: " + sizes.length);
        for (int i = 0; i < coords.length ; i ++)
        {
            int coord = coords[i];
            if (sizes[i] <= coord || coord < 0)
            {
                throw new ArrayIndexOutOfBoundsException("Invalid coordinates: " + coordsToString(coords) +
                        ", dimension " + i + " is not between zero and " + sizes[i]);
            }
        }
    }

    public static final String coordsToString(int[] coords)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int i = 0; i < coords.length ; i++)
        {
            if (i > 0)
                buf.append(",");
            buf.append(Integer.toString(coords[i]));
        }
        buf.append("]");
        return buf.toString();
    }

    public static final boolean increment(int[] coords,int[] sizes)
    {
        int i;
        for (i = coords.length - 1; ; i--)
        {
            if (coords[i] + 1 >= sizes[i])
            {
                if (i == 0)
                    return false;
                coords[i] = 0;
            }
            else
            {
                coords[i]++;
                return true;
            }
        }
    }

    public static final int coordsToAddress(int[] coords,int[] factors)
    {
        int address = coords[0];
        for (int i = 1; i < coords.length; i++)
            address = address + coords[i] * factors[i];
        return address;
    }

    public static final int[] addressToCoords(int address,int[] factors)
    {
        int[] coords = new int[factors.length - 1];
        addressToCoords(address, factors, coords);
        return coords;
    }

    public static final void addressToCoords(int address, int[] factors, int[] coords)
    {
        int dimensions = factors.length - 1;
        for (int i = dimensions-1 ; i >= 0 ; i--)
        {
            int factor = factors[i];
            coords[i] = address / factor;
            address = address % factor;
        }
    }

    /**
     * Calculates the array addressing factors for a matrix with the given sizes.
     * @param sizes The sizes in each dimension.
     * @return An array of address factors corresponding to each dimension, plus an extra int indicating the total size
     * at the end of the array.
     */
    public static final int[] calculateFactors(int[] sizes)
    {
        int dimensions = sizes.length;
        int[] factors = new int[dimensions + 1];
        factors[0] = 1;
        for (int i = 1; i < dimensions; i++)
            factors[i] = sizes[i - 1] * factors[i - 1];
        factors[dimensions] = sizes[dimensions - 1] * factors[dimensions - 1];
        return factors;
    }
}
