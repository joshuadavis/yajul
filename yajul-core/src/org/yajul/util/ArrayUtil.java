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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides utility methods for object arrays, and arrays of primitives.
 * User: jdavis
 * Date: Aug 1, 2003
 * Time: 4:26:20 PM
 * @author jdavis
 * @author agautam
 */
public class ArrayUtil
{
    /**
     * Converts an array of Integers into an array of ints.  Null values must
     * not be present in the array.
     * @param integerArray The array of Integers.
     * @return int[] - An array of ints.
     */
    public static final int[] toIntArray(Integer[] integerArray)
    {
        int[] rv = new int[integerArray.length];
        for (int i = 0; i < integerArray.length; i++)
            rv[i] =  integerArray[i].intValue();
        return rv;
    }

    /**
     * Converts a collection (Vector, ArrayList, or otherwise) of Integers
     * into an array of ints.
     * @param integerCollection The collection of Integers.
     * @return int[] - An array of ints.
     */
    public static final int[] toIntArray(Collection integerCollection)
    {
        int[] rv = new int[integerCollection.size()];
        Iterator iterator = integerCollection.iterator();
        for (int i = 0; iterator.hasNext(); i++)
            rv[i]  = ((Integer) iterator.next()).intValue();
        return rv;
    }

    /**
     * Adds Integer values from the array into the set.
     * @param values The array of values.
     * @param set The set to add the values to.
     * @return Set - The set.
     */
    public static final Set addToSet(int[] values,Set set)
    {
        for (int i = 0; i < values.length; i++)
            set.add(new Integer(values[i]));
        return set;
    }

    /**
     * Adds values from the array into the set.
     * @param values The array of values.
     * @param set The set to add the values to.
     * @return Set - The set.
     */
    public static final Set addToSet(Object[] values,Set set)
    {
        for (int i = 0; i < values.length; i++)
            set.add(values[i]);
        return set;
    }


    /**
     * Calculates the number of blocks that would be necessary for a given
     * number of elements and a given block size.
     * @param elements The total number of elements.
     * @param blockSize The number of elements in a block.
     * @return int - The number of blocks needed for 'elements' elements.
     */
    public static final int calculateBlockCount(int elements,int blockSize)
    {
        int blocks = elements / blockSize;
        if (elements % blockSize != 0)
            blocks++;
        return blocks;
    }

    /**
     * Sums an aray of hash codes into a single hash code using an algorithm
     * similar to that used by java.lang.String.
     * @param components An array of ints that will be the components of the
     * new hash code.
     * @return int - A new hash code, based on the components.
     */
    public static final int computeHashCode(int[] components)
    {
        // Sum all of the hash codes of the components, using an algorithm similar to that used by
        // java.lang.String.
        int rv = 0;
        int limit = components.length;
        for (int i = 0; i < limit; i++)
            rv += components[i] * (31 ^ (limit - i));
        return rv;
    }

    /**
     * Computes the hash code for an array of objects using an algorithm
     * similar to that used by java.lang.String.
     * @param components An array of objects that will supply the hash code
     * components.
     * @return int - The new, compound hash value.
     */
    public static final int computeHashCode(Object[] components)
    {
        // Sum all of the hash codes of the components, using an algorithm similar to that used by
        // java.lang.String.
        int rv = 0;
        int limit = components.length;
        for (int i = 0; i < limit; i++)
            rv += components[i].hashCode() * (31 ^ (limit - i));
        return rv;
    }

    /**
     * Converts a double[] to a Double[].
     */
    public static final Double[] toDoubleArray(double input[])
    {
        if (input == null)
            return null;

        Double[] output = new Double[input.length];
        for (int i = 0; i < output.length; i++)
        {
            output[i] = new Double(input[i]);
        }

        return output;
    }

    /**
     * Checks the contents of the two passed in arrays for equality.
     * @return true if the array contents are equal or both are null.
     */
    public static final boolean areContentsEqual(double[] array1, double[] array2)
    {
        if ((array1 == null) && (array2 == null))
            return true;
        if ((array1 == null) || (array2 == null))
            return false;
        if (array1.length != array2.length)
            return false;
        for (int i = 0; i < array2.length; i++)
        {
            if (array1[i] != array2[i])
                return false;
        }
        return true;
    }

    /**
     * Add the contents of the addFrom array to the result array. The results will be stored in the result array.
     *
     * A new array will be allocated if the addFrom array is longer than result[]
     *
     * @return the result array if a new one in not allocated, else the new array is returned.
     */
    public static final double[] appendToArray(double[] result, double[] addFrom)
    {
        if ((result == null) && (addFrom == null)) return null;
        if (result == null) return addFrom;
        if (addFrom == null) return result;

        double[] output;
        if (addFrom.length > result.length)
        {
            output = new double[addFrom.length];
            System.arraycopy(result, 0, output, 0, result.length);
        }
        else
            output = result;

        for (int i = 0; i < output.length; i++)
        {
            if (i < addFrom.length)
                output[i] += addFrom[i];
        }

        return output;
    }

    /**
     * Adds the contents of 2 arrays.
     * The item count of the returned array would be the longer of the inputs.
     * Entries in the shorter array will be considered 0 for addition purposes, once its length has been exhausted.
     *
     * @return null if both inputs are null, if one is null, it will return the other.
     */
    public static final double[] addArrays(double[] array1, double[] array2)
    {
        if ((array1 == null) && (array2 == null)) return null;
        if (array1 == null) return array2;
        if (array2 == null) return array1;

        double[] output = new double[array1.length > array2.length ? array1.length : array2.length];
        for (int i = 0; i < output.length; i++)
        {
            output[i] = (i < array1.length ? array1[i] : 0) + (i < array2.length ? array2[i] : 0);
        }
        return output;
    }

}
