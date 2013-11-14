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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A stream that will return an end of stream code (-1) when the maximum
 * length is reached.
 * <br>
 * User: josh
 * Date: Jan 17, 2004
 * Time: 12:56:05 PM
 */
public class LimitedInputStream extends FilterInputStream {
    /*
    Length limited input stream.  This class is borrowed from Apache JServ and
    refactored to use FilteredInputStream.
    */

    private int remaining = -1;

    /**
     * Creates an filter intput stream that will read at most 'maxLength' bytes
     * from the underlying stream.  The read methods will return -1 if
     * the maximum length is reached.
     *
     * @param in        The underlying input stream.
     * @param maxLength The maximum number of bytes to read.
     */
    public LimitedInputStream(InputStream in, int maxLength) {
        super(in);
        this.remaining = maxLength;
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     * <p/>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     * @throws IOException if an I/O error occurs.
     * @see FilterInputStream#in
     */
    public int read() throws IOException {
        if (remaining > 0) {
            remaining--;
            return in.read();
        } else if (remaining == -1)
            return in.read();
        return -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p/>
     * This method simply performs <code>in.read(b, off, len)</code>
     * and returns the result.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of
     *         the stream has been reached.
     * @throws java.io.IOException if an I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (remaining > 0) {
            // Limit the read to the remaining bytes, if needed.
            if (len > remaining)
                len = remaining;

            int read = in.read(b, off, len);
            if (read != -1)
                remaining -= read;
            else
                remaining = -1;
            return read;
        } else if (remaining == -1)
            return in.read(b, off, len);
        return -1;
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the
     * input stream. The <code>skip</code> method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes,
     * possibly <code>0</code>. The actual number of bytes skipped is
     * returned.
     * <p/>
     * This method
     * simply performs <code>in.skip(n)</code>.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    public long skip(long n) throws IOException {
        long skip = in.skip(n);
        if (remaining > 0)
            remaining -= skip;
        return skip;
    }
}
