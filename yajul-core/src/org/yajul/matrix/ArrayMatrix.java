// $Id$
package org.yajul.matrix;

/**
 * A matrix stored as a single array.
 * @author josh Sep 5, 2004 12:46:36 PM
 */
public class ArrayMatrix extends AbstractMatrix implements Matrix
{
    Object[] array;
    int[] factors;

    public ArrayMatrix()
    {
        super();
        array = new Object[0];
        factors = new int[] { 1 };
    }

    public void setSize(int[] sizes)
    {
        int[] newfactors = MatrixUtil.calculateFactors(sizes);
        Object[] newarray = new Object[newfactors[sizes.length]];
        int[] oldcoords = new int[getDimensions()];
        int[] newcoords = new int[sizes.length];
        for (int i = 0; i < array.length ; i++)
        {
            Object o = array[i];
            MatrixUtil.addressToCoords(i,factors,oldcoords);
            for (int j = 0; j < newcoords.length ; j++)
                newcoords[j] = (j < oldcoords.length) ? oldcoords[j] : 0;
            int newaddress = MatrixUtil.coordsToAddress(newcoords,newfactors);
            newarray[newaddress] = o;
        }
        super.setSize(sizes);
        factors = newfactors;
        array = newarray;
    }

    public void put(int[] coords, Object o)
    {
        beforePut(coords);
        int address = MatrixUtil.coordsToAddress(coords,factors);
        array[address] = o;
    }

    public Object get(int[] coords)
    {
        int address = MatrixUtil.coordsToAddress(coords,factors);
        return array[address];
    }

}
