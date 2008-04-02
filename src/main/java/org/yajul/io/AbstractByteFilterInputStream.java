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

package org.yajul.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Implements the array based read method to simplify writing 'byte at a time'
 * FilterInputStream classes.  Sub-classes need only implement the read()
 * method.
 * <br>
 * User: josh
 * Date: Nov 27, 2002
 * Time: 8:38:44 AM
 *
 * @author josh
 */
public abstract class AbstractByteFilterInputStream extends FilterInputStream {
    private PushbackInputStream pushback;

    /**
     * Creates a new AbstractByteFilterInputStream with a built in 'pushback buffer' which
     * enables the 'unread' methods.
     *
     * @param in                 The underlying input stream being filtered.
     * @param pushbackBufferSize The size of the pushback buffer.
     */
    public AbstractByteFilterInputStream(InputStream in, int pushbackBufferSize) {
        super((in instanceof PushbackInputStream) ? in : new PushbackInputStream(in, pushbackBufferSize));
        pushback = (PushbackInputStream) this.in;
    }

    /**
     * Creates a new AbstractByteFilterInputStream.
     *
     * @param in The underlying input stream being filtered.
     */
    public AbstractByteFilterInputStream(InputStream in) {
        super(in);
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     * @throws IOException if an I/O error occurs.
     * @see FilterInputStream#in
     */
    protected final int readByte() throws IOException {
        return super.read();
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     * @throws IOException if an I/O error occurs.
     * @see FilterInputStream#in
     */
    public abstract int read() throws IOException;

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
    public final int read(byte b[], int off, int len) throws IOException {
        int ch;
        int i;
        for (i = 0; i < len; i++) {
            ch = read();
            if (ch == -1) {
                if (i > 0)
                    return i;
                else
                    return -1;
            }
            b[off + i] = (byte) ch;
        }
        return i;
    }

    /**
     * Pushes all of the bytes after the first one into the pushback buffer and returns the first byte, effectively
     * inserting the bytes into the stream.
     *
     * @param bytes The bytes to insert into the stream.
     * @return the first byte
     * @throws IOException if something goes wrong.
     */
    protected int insert(byte[] bytes)
            throws IOException {
        pushback.unread(bytes, 1, bytes.length - 1);
        return bytes[0];
    }

}
