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

/******************************************************************************
 * Old log...
 *      Revision 1.3  2001/03/13 20:58:45  jdavis
 *      Add in optional stream debugging to HTTPConnection.
 *
 *      Revision 1.2  2000/12/01 23:55:33  cvsuser
 *      First cut.
 *      date	2000.10.23.23.27.00;	author joshuad;	state Exp;
 *
 * 2     10/23/00 7:27p Joshuad
 * First cut.
 ******************************************************************************/

package org.yajul.io;

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * Output stream filter that counts the number of bytes written to the
 * underlying stream.
 */
public class ByteCountingOutputStream extends FilterOutputStream
{
    private int count;

    /**
     * Creates a new output stream that counts then number of bytes written to
     * the output stream specified.
     */
    public ByteCountingOutputStream(OutputStream out)
    {
        super(out);
        count = 0;
    }

    /**
     * Writes b.length bytes from the specified byte array to this output stream.
     */
    public void write(byte[] b)
            throws IOException
    {
        super.out.write(b);
        count += b.length;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off to this output stream.
     */
    public void write(byte[] b, int off, int len)
            throws IOException
    {
        super.out.write(b, off, len);
        count += len;
    }

    /**
     * Writes the specified byte to this output stream.
     */
    public void write(int b)
            throws IOException
    {
        super.write(b);
        count++;
    }

    /**
     * Returns the number of bytes written.
     */
    public int getByteCount()
    {
        return count;
    }

}