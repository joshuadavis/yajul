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

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Nov 27, 2002
 * Time: 8:43:37 AM
 */

// --- The orgiginal code is from: ---
// Base64Encoder.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.yajul.io;

//import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * Provides BASE64 encoding of binary data as an output stream filter.  Bytes
 * written to this output stream filter will be encoded using the BASE64
 * encoding rules, as defined in
 * <a href="http://ds.internic.net/rfc/rfc1521.txt">MIME specification</a>
 * and written to the underlying output stream.
 *
 * @author josh (Refactored from old 'Intira' code)
 */
public class Base64OutputStream extends FilterOutputStream
{
    /** A logger for this class. */
//    private static Logger log = Logger.getLogger(Base64OutputStream.class);

    /** The number of BASE64 encoded bytes to put in a 'block'. **/
    private static final int BLOCK_SIZE = 76;

    /** The encoding matrix.   The index is the 'input' binary value, and
     * the value is the BASE64 encoded representation. **/
    private static final byte ENCODED[] =
            {
                (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D',
                (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', // 0-7
                (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
                (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', // 8-15
                (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
                (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', // 16-23
                (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b',
                (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', // 24-31
                (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j',
                (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', // 32-39
                (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
                (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', // 40-47
                (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
                (byte) '0', (byte) '1', (byte) '2', (byte) '3', // 48-55
                (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                (byte) '8', (byte) '9', (byte) '+', (byte) '/', // 56-63
                (byte) '='						// 64
            };

    /** Keeps track of the BASE64 state (byte number) in the input stream. **/
    private int state;

    /** Keeps track of the number of bytes written so far, used to add a
     * newline every 76 encoded bytes. **/
    private int count;

    /** The previous encoded and parsed byte. **/
    private int previous;

    /**
     * Creates a new BASE64 encoding output stream.
     * @param out The underlying output stream.
     */
    public Base64OutputStream(OutputStream out)
    {
        super(out);
        state = 0;
        count = 0;
        previous = 0;
    }

    /**
     * Writes the specified <code>byte</code> to this output stream.
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code>
     * calls the <code>write</code> method of its underlying output stream,
     * that is, it performs <tt>out.write(b)</tt>.
     * <p>
     * Implements the abstract <tt>write</tt> method of <tt>OutputStream</tt>.
     *
     * @param      b   the <code>byte</code>.
     * @exception  java.io.IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException
    {
        state++;
        byte inbyte = (byte) b;
        byte prevByte = (byte) previous;
        switch (state)
        {
            case 1:
                writeByte(ENCODED[get1(inbyte)]);
                previous = b;
                break;
            case 2:
                writeByte(ENCODED[get2(prevByte, inbyte)]);
                previous = b;
                break;
            case 3:
                writeByte(ENCODED[get3(prevByte, inbyte)]);
                writeByte(ENCODED[get4(inbyte)]);
                state = 0;
                break;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }
    }

    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out to the stream.
     * <p>
     * The <code>flush</code> method of <code>FilterOutputStream</code>
     * calls the <code>flush</code> method of its underlying output stream.
     *
     * @exception  java.io.IOException  if an I/O error occurs.
     * @see        java.io.FilterOutputStream#out
     */
    public void flush() throws IOException
    {
        switch (state)
        {
/*
                   1          2          3
            in:  xx123456 xx781234 xx567812 xx345678
            out: 12345678 12345678 12345678 12345678
                 1        2        3        4
            Input byte 1 -> output bytes 1 & 2
            Input byte 2 -> output bytes 2 & 3
            Input byte 3 -> output bytes 3 & 4
*/
            case 1:
                // First output byte written, so write the second
                // output byte with the last two bits of the first input
                // byte in it.
                writeByte(ENCODED[get2((byte) previous, (byte) 0)]);
                super.write('=');
                super.write('=');
                break;
            case 2:
                // First and second bytes written, so write the third output
                // byte with bits 5-8 of the second input byte.
                writeByte(ENCODED[get3((byte) previous, (byte) 0)]);
                super.write('=');
                break;
            case 3: // Input length mod 3 is zero, so no padding.
                break;
        }
        super.flush();
    }

    /**
     * Writes the byte to the encoded output stream, inserting '\n' characters
     * to separate the blocks (as per the BASE64 spec).
     * @param b The encoded byte to write.
     * @throws IOException
     */
    private final void writeByte(int b) throws IOException
    {
        count++;
        if (count > BLOCK_SIZE)
        {
            super.write('\n');
            count = 0;
        }
        super.write(b);
    }

    private static final int get1(byte byte1)
    {
        return (byte1 & 0xfc) >> 2;
    }

    private static final int get2(byte byte1, byte byte2)
    {
        return ((byte1 & 0x3) << 4) | ((byte2 & 0xf0) >>> 4);
    }

    private static final int get3(byte byte2, byte byte3)
    {
        return ((byte2 & 0x0f) << 2) | ((byte3 & 0xc0) >>> 6);
    }

    private static final int get4(byte byte3)
    {
        return byte3 & 0x3f;
    }
}
