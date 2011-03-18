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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;

/**
 * Provides commonly used string functions.
 * <hr>User: jdavis
 * <br>Date: Jul 15, 2003
 * <br>Time: 12:17:49 PM
 *
 * @author jdavis
 */
public class StringUtil {

    public static final String EMPTY = "";

    /**
     * A logger for this class.
     */
    private static Logger log = LoggerFactory.getLogger(StringUtil.class);

    /**
     * Returns the <tt>String</tt> encoded into an array of bytes using the
     * named charset.   This is identical to String.getBytes(charset), except that
     * it will use the default encoding if the charset is not available.
     *
     * @param s       the string
     * @param charset the name of a supported
     *                {@link java.nio.charset.Charset </code>charset<code>} , or null if the default
     *                charset is to be used.
     * @return The string, encoded in either the default charset or the specified charset.
     */
    public static byte[] getBytes(String s, String charset) {
        byte[] bytes;
        try {
            bytes = (isEmpty(charset)) ? s.getBytes() : s.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            log.warn("Encoding " + charset + " is not supported, using default encoding");
            bytes = s.getBytes();
        }
        return bytes;
    }

    /**
     * Prints the class name of the object, followed by '@', followed by the identity hash code
     * of the object, just like java.lang.Object.toString().
     *
     * @param o The object to print.
     * @return String - The object's default string representation.
     */
    public static String defaultToString(Object o) {
        return defaultToString(o,true);
    }

    /**
     * Prints the class name of the object, followed by '@', followed by the identity hash code
     * of the object, just like java.lang.Object.toString().
     *
     * @param o The object to print.
     * @param hex True for hex, false for decimal
     * @return String - The object's default string representation.
     */
    public static String defaultToString(Object o,boolean hex) {
        StringBuffer buf = new StringBuffer();
        if (o == null)
            buf.append("null");
        else {
            buf.append(o.getClass().getName());
            buf.append("@");
            int identity = System.identityHashCode(o);
            buf.append(hex ? Integer.toHexString(identity) : Integer.toString(identity));
        }
        return buf.toString();
    }

    /**
     * Returns true if the string is null or zero length.
     *
     * @param str - The string to test.
     * @return boolean - True if the string is null or zero length.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * @param s a string
     * @return Returns the string, or a zero length (empty) string if 's' is null.
     */
    public static String nullAsEmpty(String s) {
        return s == null ? EMPTY : s;
    }
    
    /**
     * Returns a string containing the hexadecimal representation of the array
     * of bytes.
     *
     * @param bytes The array of bytes to turn into hex.
     * @return String - The hex string.
     */
    public static String hexString(byte[] bytes) {
        return hexString(bytes, null);
    }

    /**
     * Returns a string containing the hexadecimal representation of the
     * array of bytes, separated by an optional string.
     *
     * @param bytes     The array of bytes to turn into hex.
     * @param separator The separator string.  If null or zero length, no
     *                  separator will be used.
     * @return String - The hex string.
     */
    public static String hexString(byte[] bytes, String separator) {
        StringBuffer buf = new StringBuffer();
        hexString(buf, bytes, separator, true);
        return buf.toString();
    }

    /**
     * Convert a string of hexadecimal characters to a byte array.
     *
     * @param hexString The hexadecimal string
     * @return The string of hexadecimal characters as a byte array.
     */
    public static byte[] parseHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            // Odd number of characters: put a zero at the beginning.
            hexString = "0" + hexString;
        }
        return Bytes.parseHex(hexString);
    }

    /**
     * Convert a hexadecimal character to a byte.
     *
     * @param n The hex character
     * @return the byte
     */
    public static byte parseHexChar(char n) {
        if (n <= '9')
            return (byte) (n - '0');
        if (n <= 'G')
            return (byte) (n - Bytes.VALUE_UPPERCASE_A);
        else
            return (byte) (n - Bytes.VALUE_LOWERCASE_A);
    }

    /**
     * Appends the hex representation of the bytes to the string buffer, separated
     * by an optional separator string.
     *
     * @param bytes     The bytes to convert to hex.
     * @param buf       The buffer to append to.
     * @param separator The separator string.  If null or zero length, no
     *                  separator will be used.
     * @param lowerCase True for lower case hex (e.g. 34f0), false for upper
     *                  case hex (e.g. 34F0).
     */
    public static void hexString(StringBuffer buf,
                                 byte[] bytes, String separator,
                                 boolean lowerCase) {
        char[] out = new char[2];
        final char[] chars = (lowerCase) ? Bytes.HEX_CHARS_LOWER : Bytes.HEX_CHARS_UPPER;
        final char[] sep = (separator != null && separator.length() > 0) ?
                separator.toCharArray() : null;
        for (int i = 0; i < bytes.length; i++) {
            if ((sep != null) && (i > 0))   // Add the separator, if required.
                buf.append(sep);
            hexChars(chars, bytes[i], out);
            buf.append(out);            // Append the two hex chars.
        }
    }

    /**
     * Converts the byte into a two element array of hex characters.
     *
     * @param chars  The hex character set to use (e.g. HEX_BYTES_LOWER / HEX_BYTES_UPPER).
     * @param inByte The input byte.
     * @param out    The output array of characters.  Length must be >= 2.
     */
    public static void hexChars(final char[] chars, int inByte, char[] out) {
        out[1] = chars[Bytes.MASK & inByte];   // Get the lower nybble and set the second char.
        inByte >>= 4;                    // Shift off the lower nybble.
        out[0] = chars[Bytes.MASK & inByte];   // Get the upper nybble and set the first char.
    }

    /**
     * Appends the string to the string buffer IFF it is not 'empty' (null or zero length).
     *
     * @param string The string to append.
     * @param buf    The string buffer.
     * @see #isEmpty(String)
     */
    public static void appendIfNotEmpty(String string, StringBuffer buf) {
        if (!isEmpty(string))
            buf.append(string);
    }

    /**
     * Returns the substring before the delimiter string, not including the delimiter string.
     *
     * @param string The string to look in.
     * @param delim  The delimiter string.
     * @return the substring before the delimiter string, not including the delimiter string.
     */
    public static String substringBefore(String string, String delim) {
        int pos = string.indexOf(delim);
        if (pos == 0)
            return null;
        else if (pos > 0)
            return string.substring(0, pos);
        else
            return string;
    }

    /**
     * Returns the substring after the delimiter string, not including the delimiter string.
     *
     * @param string The string to look in.
     * @param delim  The delimiter string.
     * @return the substring after the delimiter string, not including the delimiter string.
     */
    public static String substringAfter(String string, String delim) {
        int pos = string.indexOf(delim);
        if (pos == 0)
            return string;
        else if (pos > 0)
            return string.substring(pos + delim.length());
        else
            return null;
    }
}
