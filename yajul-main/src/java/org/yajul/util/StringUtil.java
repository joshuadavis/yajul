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
    private static final String PADDING = "                    ";

    /**
     * Prints the class name of the object, followed by '@', followed by the
     * hash code of the object, just like java.lang.Object.toString().
     * @param o The object to print.
     * @return String - The object's default string representation.
     */
    public static final String defaultToString(Object o)
    {
        StringBuffer buf = new StringBuffer();
        appendDefaultToString(buf, o);
        return buf.toString();
    }

    /**
     * Appends the class name of the object, followed by '@', followed by the
     * hash code of the object, just like java.lang.Object.toString().
     * @param o The object to print.
     * @param buf The string buffer to append to.
     */
    public static final void appendDefaultToString(StringBuffer buf, Object o)
    {
        if (o == null)
            buf.append("null");
        else
        {
            buf.append(o.getClass().getName());
            buf.append("@");
            buf.append(Integer.toHexString(System.identityHashCode(o)));
        }
    }

    /**
     * Left-pads a string with spaces out to the specified length and returns
     * it.
     * @param s The string to pad.
     * @param length The desired length of the padded string.
     * @param truncate Set to true to truncate 's' to 'length' if
     * s.length() > length
     * @return String - The padded (truncated) string.
     */
    public static final String padLeft(String s, int length, boolean truncate)
    {
        return padLeft(s, length, truncate, PADDING);
    }

    /**
     * Right-pads a string with spaces out to the specified length and returns
     * it.
     * @param s The string to pad.
     * @param length The desired length of the padded string.
     * @param truncate Set to true to truncate 's' to 'length' if
     * s.length() > length
     * @return String - The padded (truncated) string.
     */
    public static final String padRight(String s, int length, boolean truncate)
    {
        return padRight(s, length, truncate, PADDING);
    }

    /**
     * Left-pads a string out to the specified length using the specified
     * padding string and returns it.
     * @param s         The string to pad.
     * @param length    The desired length of the padded string.
     * @param truncate  Set to true to truncate 's' to 'length' when s.length()
     * > length
     * @param padding   The string to use as padding.
     * @return String   - The padded (truncated) string.
     */
    public static String padLeft(String s, int length, boolean truncate,
                                 final String padding)
    {
        return pad(length, s, truncate, padding, false);
    }

    /**
     * Right-pads a string out to the specified length using the specified
     * padding string and returns it.
     * @param s         The string to pad.
     * @param length    The desired length of the padded string.
     * @param truncate  Set to true to truncate 's' to 'length' when s.length()
     * > length
     * @param padding   The string to use as padding.
     * @return String   - The padded (truncated) string.
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

    /**
     * Returns true if the string is null or zero length.
     * @param str The string to test.
     * @return boolean - True if the string is null or zero length.
     **/
    public static final boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    }

    /**
     * Splits a string into an array of strings using the given set of delimiter
     * characters.
     * @param str The string to split.
     * @param delims A string containing the delimiter characters.
     * @return String[] - An array of strings.
     * @see java.util.StringTokenizer
     */
    public static final String[] tokenize(String str, String delims)
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
     * Splits a string into an array of strings using the given delimiter string.  The returned array will
     * contiain zero length strings if the delimiter is found at the very beginning or end of the searched
     * string 'str'.
     * @param str The string to split.
     * @param delim A string delimiter.
     * @return String[] - An array of strings.
     */
    public static final String[] split(String str, String delim)
    {
        ArrayList list = new ArrayList();
        int start = 0;
        int index = str.indexOf(delim);

        // If the delimiter string was not found, return the string as a single
        // array element.
        if (index < 0)
            return new String[] { str };
        while (index >= 0)
        {
            list.add(str.substring(start,index));       // Add the substring to the list.
            start = index + delim.length();             // Start after the delimiter.
            index = str.indexOf(delim,start);           // Look for the next occurrence.
        } // while

        // If the delimiter was found at the very end of the string, add a zero-length
        // element to the list.
        if (start == str.length())
            list.add("");
        // If there is a trailing part of the string that did not contain the delimeter,
        // add it.
        else if (start < str.length())
            list.add(str.substring(start));

        return (String[]) list.toArray(new String[list.size()]);
    }
    /**
     * Joins an array of strings using the given delimiter.
     * @param array An array of strings.
     * @param delim The delimiter (typicaly a single character,
     * but anything can be used).
     * @return String - The joined strings, with the given delimeter in
     * between each string in the array.
     */
    public static final String join(String[] array, String delim)
    {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < array.length; i++)
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
     * Replaces the last occurence of x in the buffer with y
     * @param str The string that is the subject of the  operation
     * @param x The string that is to be replaced by y in the buffe
     * @param y The string that will replace x in the buffer
     * @return String the buffer with x replaced by y
     */
    public static final String replaceLast(String str, String x, String y)
    {
        return replaceSingle(true,str, x, y);
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
        for (int i = a.length - 1; i > 0; i--)
        {
            if (!equals(a[i], b[i]))
                return false;
        }
        return true;
    }

    /**
     * Returns a string containing the hexadecimal representation of the array
     * of bytes.
     * @param bytes The array of bytes to turn into hex.
     * @return String - The hex string.
     */
    public static final String hexString(byte[] bytes)
    {
        return hexString(bytes, null);
    }

    /**
     * Returns a string containing the hexadecimal representation of the
     * array of bytes, separated by an optional string.
     * @param bytes The array of bytes to turn into hex.
     * @param separator The separator string.  If null or zero length, no
     * separator will be used.
     * @return String - The hex string.
     */
    public static final String hexString(byte[] bytes, String separator)
    {
        StringBuffer buf = new StringBuffer();
        boolean separatorRequested =
                (separator != null && separator.length() > 0);
        int b = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            // Add the separator, if required.
            if (separatorRequested && (i > 0))
                buf.append(separator);

            // Get the int value 0-255.
            b = 0x000000FF & bytes[i];
            // Add a leading zero, to ensure all bytes are represented
            // by two hex characters.
            if (b < 16)
                buf.append("0");
            buf.append(Integer.toHexString(b));
        }
        return buf.toString();
    }

    /**
     * Parses a long value from a string.  If the string
     * begins with '0x', the string will be interpreted as a hexadecimal
     * number.
     * @param str String to parse.
     * @return Long value of the string, 0 if 'str' is empty.
     * @see #isEmpty(String)
     */
    public static long parseLong(String str)
    {
        if (isEmpty(str))
            return 0;
        if (str.startsWith("0x"))
            return Long.parseLong(str.substring(2), 16);
        else
            return Long.parseLong(str);
    }

    // --- Implementation private methods ---

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

    private static String replaceSingle(boolean last,String str, String x, String y)
    {
        // Get the last index of x in the string.
        int index = (last) ? str.lastIndexOf(str) : str.indexOf(str);
        // If x was not found, return the original string.
        if (index == -1)
            return str;

        StringBuffer buf = new StringBuffer();
        // Append the substring before the token to be replaced.
        buf.append(str.substring(0,index));
        // Append the replacement.
        buf.append(y);
        // Append the substring after the token to be replaced.
        buf.append(str.substring(index + x.length()));
        return buf.toString();
    }

} // class StringUtil
