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
package org.yajul.io;

import org.yajul.util.StringUtil;
import org.yajul.util.Bytes;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An output stream that prints lines of hexadecimal output containing the byte
 * offset (in hex), the hex representation of the types, and the ASCII representation
 * of the bytes.
 * User: josh
 * Date: Jan 11, 2004
 * Time: 9:23:13 PM
 */
public class HexDumpOutputStream extends FilterOutputStream
{
    /** The width of each line (# of bytes). **/
    private int width;
    /** The nubmer of bytes on the current line. **/
    private int count;
    /** The total number of bytes. **/
    private int total;
    /** A buffer of the current bytes. **/
    private byte[] buf;
    /** Another buffer, used in converting the stream position into hex. **/
    private byte[] intBytes;
    /** Hex chars buffer. **/
    private char[] hex;
    /** Hex byte buffer. **/
    private byte[] hexBytes;

    private final byte[] SEPARATOR = " | ".getBytes();
    private static final char NON_PRINTABLE = '.';
    private static final char NEWLINE = '\n';

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param   out   the underlying output stream to be assigned to
     *                the field <tt>this.out</tt> for later use, or
     *                <code>null</code> if this instance is to be
     *                created without an underlying stream.
     * @param width The number of bytes to print in a line of output.
     */
    public HexDumpOutputStream(OutputStream out,int width)
    {
        super(out);
        this.width = width;
        this.count = 0;
        this.total = 0;
        this.buf = new byte[width]; // Create a buffer for the bytes.
        this.hex = new char[2];
        this.hexBytes = new byte[2];
        this.intBytes = new byte[4];
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
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException
    {
        // If the width has been reached, finish the current line and
        // start a new one.
        if (count == width)
        {
            writeASCII();
            out.write(NEWLINE);
            count = 0;
        }

        // If this is the beginning of a line, write the total bytes
        // as a hex string.
        if (count == 0)
        {
            Bytes.toBytes(total,intBytes);  // Convert the integer into bytes.
            // Re-use 'buf' to hold the hex representation.
            Bytes.hexBytes(Bytes.HEX_BYTES_LOWER,intBytes,buf,4);
            out.write(buf,0,8);
            out.write(SEPARATOR);
        }

        buf[count] = (byte)(0x000000FF & b);
        StringUtil.hexChars(StringUtil.HEX_CHARS_LOWER,b,hex);
        hexBytes[0] = (byte)hex[0];
        hexBytes[1] = (byte)hex[1];
        out.write(hexBytes);
        count++;
        total++;
    }

    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out to the stream.
     * <p>
     * The <code>flush</code> method of <code>FilterOutputStream</code>
     * calls the <code>flush</code> method of its underlying output stream.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterOutputStream#out
     */
    public void flush() throws IOException
    {
        // If there are bytes on the line.
        if (count > 0)
        {
            // Pad out to the first 'ascii' column.
            int pad  = 2 * (width - count);
            for (int i = 0; i < pad ; i++)
                out.write(' ');
            // Write the separator and the remaining bytes as ascii.
            writeASCII();
            out.write(NEWLINE);
        }
        super.flush();
    }

    private void writeASCII() throws IOException
    {
        out.write(SEPARATOR);
        for(int i = 0; i < count; i++)
        {
            if (buf[i] >= 32 && buf[i] < 127)
                out.write(buf[i]);
            else
                out.write(NON_PRINTABLE);
        }
    }
}
