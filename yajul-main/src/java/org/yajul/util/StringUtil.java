/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Provides static methods: Commonly used string functions.
 * @author Joshua Davis
 * @version 1.0
 */
public class StringUtil
{
    /**
     * Prints the class name of the object, followed by '@', followed by the 
     * hash code of the object, just like java.lang.Object.toString().
     * @param o - The object to print.
     * @return String - The object's default string representation.
     */
    public static final String defaultToString(Object o)
    {
        StringBuffer buf = new StringBuffer();
        if (o == null)
            buf.append("null");
        else
        {
            buf.append(o.getClass().getName());
            buf.append("@");
            buf.append(Integer.toHexString(o.hashCode()));
        }
        return buf.toString();
    }

    /**
     * Left-pads a string with spaces out to the specified length and returns 
     * it.
     * @param s - The message to write to the log
     * @param length - The desired length of the padded string.
     * @param truncate - Set to true to truncate 's' to 'length' when 
     * s.length() > length
     * @return String - The padded (truncated) string.
     */
    public static final String padLeft(String s, int length, boolean truncate)
    {
        final String paddingChunk = "                    ";
        return padLeft(s, length, truncate, paddingChunk);
    }

    /**
     * Right-pads a string with spaces out to the specified length and returns
     * it.
     * @param s - The message to write to the log
     * @param length - The desired length of the padded string.
     * @param truncate - Set to true to truncate 's' to 'length' when
     * s.length() > length
     * @return String - The padded (truncated) string.
     */
    public static final String padRight(String s, int length, boolean truncate)
    {
        final String paddingChunk = "                    ";
        return padRight(s, length, truncate, paddingChunk);
    }

    /**
     * Left-pads a string out to the specified length using the specified
     * padding string and returns it.
     * @param s         The message to write to the log
     * @param length    The desired length of the padded string.
     * @param truncate  Set to true to truncate 's' to 'length' when s.length()
     * > length
     * @param padding   The string to use as padding.
     * @return String   The padded (truncated) string.
     */
    public static String padLeft(String s, int length, boolean truncate, 
        final String padding)
    {
        return pad(length, s, truncate, padding, false);
    }

    /**
     * Right-pads a string out to the specified length using the specified
     * padding string and returns it.
     * @param s         The message to write to the log
     * @param length    The desired length of the padded string.
     * @param truncate  Set to true to truncate 's' to 'length' when s.length()
     * > length
     * @param padding   The string to use as padding.
     * @return String   The padded (truncated) string.
     */
    public static String padRight(String s, int length, boolean truncate,
        final String padding)
    {
        return pad(length, s, truncate, padding, true);
    }

    private static String pad(int length, String s, boolean truncate,
                              final String padding, boolean right)
    {
        int spaceCount = length - s.length();

        // Is the string longer than the padded length?
        if (spaceCount <= 0)
        {
            if (truncate)
                return s.substring(0, length);
            else
                return s;
        }

        StringBuffer sb = null;

        if (right)
        {
            sb = new StringBuffer();
            // Add the padding.
            appendPad(padding, spaceCount, sb);
            // Add the string.
            sb.append(s);
        }
        else
        {
            // Add the string.
            sb = new StringBuffer(s);
            // Add the padding.
            appendPad(padding, spaceCount, sb);
        }

        return sb.toString();
    }


    private static final void appendPad(final String padding, int spaceCount,
                                        StringBuffer sb)
    {
        // Append whole chunks.
        int paddingChunkLength = padding.length();
        while (spaceCount >= paddingChunkLength)
        {
            sb.append(padding);
            spaceCount -= paddingChunkLength;
        }

        // Append the last partial chunk.
        if (spaceCount >= 0)
            sb.append(padding.substring(0, spaceCount));
    }

    /**
     * Returns true if the string is null or zero length.
     * @param str - The string to test.
     * @return boolean - True if the string is null or zero length.
     **/
    public static final boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    } 
    
    /**
     * Splits a string into an array of strings using the delimiters given.
     * @param str - The string to split.
     * @param delims - A string containing the delimiter characters.
     * @return String[] - An array of strings.
     * @see java.util.StringTokenizer
     */
    public static final String[] split(String str, String delims)
    {
        StringTokenizer st = new StringTokenizer(str, delims);
        ArrayList list = new ArrayList();
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        } // while
        return (String[]) list.toArray(new String[list.size()]);        
    }

    /**
     * Joins an array of strings using the given delimiter.
     * @param array - An array of strings.
     * @param delim - The delimiter (typicaly a single character, 
     * but anything can be used).
     * @return String - The joined strings, with the given delimeter in
     * between each string in the array.
     */
    public static final String join(String[] array, String delim)
    {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < array.length ; i++)
        {
            if (i > 0)           // If this is not the first element,
                b.append(delim); // add a delimiter before adding the element.
            b.append(array[i]);
        } // for
        return b.toString();
    }

    /**
     * Replaces all the occurences of x in the buffer with y
     * @param buffer The string that is the subject of the  operation
     * @param x The string that is to be replaced by y in the buffe
     * @param y The string that will replace x in the buffer
     * @return String the buffer with x replaced by y
     */
    public static final String replace(String buffer, String x, String y)
    {
        return join(split(buffer, x), y);
    }

    /**
     * Compares two strings; if contents of both are the same OR if both are \
     * null, returns true
     * @param a string #1
     * @param b string #2
     * @return <code>true</code> if the two match
     */
    public static final boolean equals(String a, String b)
    {
        return (a == null ? b == null : a.equals(b));
    }

    /**
     * Compares two arrays of strings.  If they are both the same length and
     * contain equal strings in the same order, this will return true.
     * @param   a   string array #1
     * @param   b   string array #2
     * @return <code>true</code> if the two match
     */
    public static final boolean arrayEquals(String[] a, String[] b)
    {
        if (a == null)
        {
            if (b == null)
                return true;
            else
                return false;
        }
        else
        {
            if (b == null)
                return false;
        }
        // Both a and be are non-null
        if (a.length != b.length)
            return false;
        for (int i = a.length - 1; i > 0 ; i--)
        {
            if (!equals(a[i], b[i]))
                return false;
        }
        return true;
    }
} // class StringUtil
