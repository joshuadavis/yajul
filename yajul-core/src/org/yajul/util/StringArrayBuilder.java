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

/**
 * Provides a simple means for concatenating string arrays.
 * User: jdavis
 * Date: Oct 9, 2003
 * Time: 12:08:39 PM
 * @author jdavis
 */
public class StringArrayBuilder
{
    private String[] array;
    private int index;

    /**
     * Creates a string array with the specified capacity.
     * @param size The capacity of the string array (length).
     */
    public StringArrayBuilder(int size)
    {
        array = new String[size];
    }

    /**
     * Appends the specified strings to the current position in the array.
     * @param stringArray The array of strings to append.
     */
    public void append(String[] stringArray)
    {
        System.arraycopy(stringArray,0,array,index,stringArray.length);
        index += stringArray.length;
    }


    /**
     * Appends the specified string to the array.
     * @param string The string.
     */
    public void append(String string)
    {
        array[index] = string;
        index ++;
    }

    /**
     * Returns the current index of the appender.
     * @return The index.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Returns the current string array.
     * @return The string array.
     */
    public String[] toStringArray()
    {
        return array;
    }
}
