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
 * Provides commonly used string functions.
 * User: jdavis
 * Date: Jul 15, 2003
 * Time: 12:17:49 PM
 * @author jdavis
 */
public class StringUtil
{

    /**
     * Prints the class name of the object, followed by '@', followed by the hash code
     * of the object, just like java.lang.Object.toString().
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
     * Returns true if the string is null or zero length.
     * @param str - The string to test.
     * @return boolean - True if the string is null or zero length.
     **/
    public static final boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
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
        hexString(buf, bytes, separator, true);
        return buf.toString();
    }

    /** Lowercase hex characters. **/
    public static final char[] HEX_CHARS_LOWER = "0123456789abcdef".toCharArray();
    /** Uppercase hex characters. **/
    public static final char[] HEX_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    /** Lower nybble mask. **/
    private static final int MASK = 0x0000000F;

    /**
     * Appends the hex representation of the bytes to the string buffer, separated
     * by an optional separator string.
     * @param bytes The bytes to convert to hex.
     * @param buf The buffer to append to.
     * @param separator The separator string.  If null or zero length, no
     * separator will be used.
     * @param lowerCase True for lower case hex (e.g. 34f0), false for upper
     * case hex (e.g. 34F0).
     */
    public static void hexString(StringBuffer buf,
                                 byte[] bytes, String separator,
                                 boolean lowerCase)
    {
        char[] out = new char[2];
        final char[] chars = (lowerCase) ? HEX_CHARS_LOWER : HEX_CHARS_UPPER;
        final char[] sep = (separator != null && separator.length() > 0) ?
                separator.toCharArray() : null;
        for (int i = 0; i < bytes.length; i++)
        {
            if ((sep != null) && (i > 0))   // Add the separator, if required.
                buf.append(sep);
            hexChars(chars, bytes[i], out);
            buf.append(out);            // Append the two hex chars.
        }
    }

    /**
     * Converts the byte into a two element array of hex characters.
     * @param chars The hex character set to use (e.g. HEX_BYTES_LOWER / HEX_BYTES_UPPER).
     * @param inByte The input byte.
     * @param out The output array of characters.  Length must be >= 2.
     */
    public static void hexChars(final char[] chars, int inByte, char[] out)
    {
        out[1] = chars[MASK & inByte];   // Get the lower nybble and set the second char.
        inByte >>= 4;                    // Shift off the lower nybble.
        out[0] = chars[MASK & inByte];   // Get the upper nybble and set the first char.
    }
    
    /**
     * Appends the string to the string buffer IFF it is not null and not empty.
     * @param string The string to append.
     * @param buf The string buffer.
     */
    public static final void appendIfNotEmpty(String string,StringBuffer buf)
    {
        if (!isEmpty(string))
            buf.append(string);
    }
    
}
