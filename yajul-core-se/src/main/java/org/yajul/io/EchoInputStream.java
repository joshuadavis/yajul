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

/*
 * EchoInputStream.java
 *
 * Created on March 7, 2001, 9:36 PM
 */

package org.yajul.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An input stream that echoes all data read from the input stream into
 * an output stream.  This can be useful for debugging.
 *
 * @author josh
 */
public class EchoInputStream extends FilterInputStream {
    private OutputStream out;

    /**
     * Creates new EchoInputStream that writes everything read from
     * 'in' into 'out'.
     *
     * @param in  The underlying input stream.
     * @param out The output stream to echo the input to.
     */
    public EchoInputStream(InputStream in, OutputStream out) {
        super(in);
        this.out = out;
    }

    /**
     * Reads the next byte in the input stream, and also writes it to
     * the echo output stream.
     *
     * @return int - The next byte, or -1 for end of file.
     * @throws IOException if there was a problem reading from the underlying
     *                     stream or writing to the echo stream.
     * @see java.io.FilterInputStream#read()
     */
    public int read() throws IOException {
        int c = super.read();
        if (c >= 0)
            out.write(c);
        return c;
    }

    /**
     * Reads the specified number of bytes from the input stream and also
     * writes them to the echo output stream.
     *
     * @param bytes The array where the bytes read from the input will
     *              be placed.
     * @param off   Offset for the first byte.
     * @param len   The number of bytes to read.
     * @return int  The number of bytes actually read, or -1 for end of file.
     * @throws IOException if there was a problem reading from the underlying
     *                     stream or writing to the echo stream.
     * @see java.io.FilterInputStream#read(byte[],int,int)
     */
    public int read(byte[] bytes, int off, int len) throws IOException {
        int i = super.read(bytes, off, len);
        if (i > 0)
            out.write(bytes, off, i);
        return i;
    }
}
