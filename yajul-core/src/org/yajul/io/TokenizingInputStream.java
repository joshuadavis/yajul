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

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads from the underlying input stream until the delimiter is reached.  The
 * read methods will return -1 (end of stream) when the delimiter is reached,
 * or the end of the underlying stream has been reached.
 * User: josh
 * Date: Jan 18, 2004
 * Time: 10:00:01 AM
 */
public class TokenizingInputStream extends AbstractByteFilterInputStream
{
    private byte[] delimiter;
    /** True if the end of the underlying stream has been reached. **/
    private boolean eos = false;
    /** The index of the next char to match. **/
    private int matchIndex;
    /** The number of tokens parsed so far. **/
    private int tokenCount;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param   in   the underlying input stream, or <code>null</code> if
     *          this instance is to be created without an underlying stream.
     */
    public TokenizingInputStream(InputStream in,byte[] delimiter)
    {
        super(in);
        this.delimiter = delimiter;
        this.matchIndex = 0;
        this.tokenCount = 0;
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     * <p>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  java.io.IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read() throws IOException
    {
        // If the underlying stream has no more data, return -1 (EOS).
        if (eos)
            return -1;
        // If the delimiter was fully matched, return -1 (EOS)
        if (matchIndex >= delimiter.length)
            return -1;
        // Read the next byte from the underlying stream.
        int c = readByte();
        if (c == -1)
        {
            eos = true;
            return c;
        }

        // If the byte matches the current delimiter byte, then increment
        // the match index.
        if (c == delimiter[matchIndex])
        {
            matchIndex++;
            if (matchIndex >= delimiter.length)
                tokenCount++;
        }
        // Otherwise, start matching from the beginning.
        else
            matchIndex = 0;
        // Return the byte.
        return c;
    }

    /**
     * Returns true if the end of the underlying stream was encountered.
     * @return true if the end of the underlying stream was encountered.
     */
    public boolean endOfStream()
    {
        return eos;
    }

    /**
     * Resets the stream so a new token can be read.  Returns false if the end
     * of the underlying stream was encountered, true if more tokens could
     * be read.
     * @return false if the end of the underlying stream was encountered.
     */
    public boolean nextToken()
    {
        if (eos)
            return false;
        // Reset the match index.
        matchIndex = 0;
        return true;
    }

    /**
     * Returns the number of tokens matched so far.
     * @return the number of tokens matched so far.
     */
    public int getTokenCount()
    {
        return tokenCount;
    }
}
