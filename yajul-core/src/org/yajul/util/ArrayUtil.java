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
    public static Set addToSet(int[] values,Set set)
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
    public static Set addToSet(Object[] values,Set set)
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
    public static int calculateBlockCount(int elements,int blockSize)
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
    public static int computeHashCode(int[] components)
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

}
