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
 * Yet another class that turns Java primatives into arrays of bytes, and back.
 * User: josh
 * Date: Jan 11, 2004
 * Time: 10:18:58 PM
 */
public class Bytes
{
    /***
     * Builds a 4-byte array from an int, MSB first.
     * @param n The number to convert.
     * @param b The output array.
     * @return The output array (b).
     */
    public static byte[] toBytes(int n,
                                 byte[] b)
    {
        // The LSB goes in the last byte.
        b[3] = (byte) (n);
        n >>>= 8;
        b[2] = (byte) (n);
        n >>>= 8;
        b[1] = (byte) (n);
        n >>>= 8;
        b[0] = (byte) (n);
        return b;
    }

    /***
     * Builds a 8-byte array from an int, MSB first.
     * @param n The number to convert.
     * @param b The output array.
     * @return The output array (b).
     */
    public static byte[] toBytes(long n, byte[] b)
    {
        // The LSB goes in the last byte.
        b[7] = (byte) (n);
        n >>>= 8;
        b[6] = (byte) (n);
        n >>>= 8;
        b[5] = (byte) (n);
        n >>>= 8;
        b[4] = (byte) (n);
        n >>>= 8;
        b[3] = (byte) (n);
        n >>>= 8;
        b[2] = (byte) (n);
        n >>>= 8;
        b[1] = (byte) (n);
        n >>>= 8;
        b[0] = (byte) (n);
        return b;
    }

    /** Lowercase hex characters. **/
    public static final byte[] HEX_BYTES_LOWER = "0123456789abcdef".getBytes();
    /** Uppercase hex characters. **/
    public static final byte[] HEX_BYTES_UPPER = "0123456789ABCDEF".getBytes();
    /** Lower nybble mask. **/
    private static final int MASK = 0x0000000F;

    /**
     * Converts the byte into a two element array of hex characters.
     * @param hexBytes The hex encoding set to use (e.g. HEX_BYTES_LOWER /
     *  HEX_BYTES_UPPER).
     * @param inBytes The input byte array.
     * @param length The number of bytes to convert to hex.
     * @param out The output array of hex bytes.  The length of this array
     * must be >= 2 * length.
     */
    public static void hexBytes(final byte[] hexBytes, byte[] inBytes, byte[] out, int length)
    {
        byte inByte;
        int j;
        for (int i = 0; i < length; i++)
        {
            inByte = inBytes[i];
            j = (i * 2);
            out[1 + j] = hexBytes[MASK & inByte];   // Get the lower nybble and set the second char.
            inByte >>= 4;                           // Shift off the lower nybble.
            out[0 + j] = hexBytes[MASK & inByte];   // Get the upper nybble and set the first char.
        }
    }

    /**
     * Converts the byte into a two element array of hex characters.
     * @param hexBytes The hex character set to use (e.g. HEX_BYTES_LOWER / HEX_BYTES_UPPER).
     * @param inByte The input byte.
     * @param out The output array of characters.  Length must be >= 2.
     */
    public static void hexBytes(final byte[] hexBytes, int inByte, byte[] out)
    {
        out[1] = hexBytes[MASK & inByte];   // Get the lower nybble and set the second char.
        inByte >>= 4;                    // Shift off the lower nybble.
        out[0] = hexBytes[MASK & inByte];   // Get the upper nybble and set the first char.
    }
}
