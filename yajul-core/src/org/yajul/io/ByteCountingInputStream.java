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

/*******************************************************************************
 * Old log...
 * 2     10/23/00 7:27p Joshuad
 * First cut.
 *******************************************************************************/

package org.yajul.io;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * InputStream class that counts the number of bytes read.
 */
public class ByteCountingInputStream extends FilterInputStream
{
    private int count;
    private long startTime;
    private long bytesSinceStartTime;

    public ByteCountingInputStream(InputStream is)
    {
        super(is);
        count = 0;
        bytesSinceStartTime = 0;
        startTime = 0;
    }

    /**
     *  Reads the next byte of data from the input stream.
     */
    public int read()
            throws java.io.IOException
    {
        int b = super.read();
        if (b != -1)     // If the value isn't the EOF signal...
            increment(1);
        return b;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p>
     * This method simply performs <code>in.read(b, off, len)</code>
     * and returns the result.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterInputStream#in
     */
    public int read(byte b[], int off, int len) throws IOException
    {
        int bytes = super.in.read(b, off, len);
        if (bytes != -1)        // If the value isn't the EOF signal...
            increment(bytes);   // increment the number of bytes read.
        return bytes;
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     */
    public long skip(long n)
            throws java.io.IOException
    {
        long b = super.in.skip(n);
        increment(b);
        return b;
    }

    /**
     * Returns the number of bytes read from the input stream.
     * @return the number of bytes read from the input stream.
     */
    public int getByteCount()
    {
        return count;
    }

    /**
     * Reset the throughput statistics.
     */
    public void resetStatistics()
    {
        startTime = System.currentTimeMillis();
        bytesSinceStartTime = 0;
    }

    /**
     * Returns the elapsed time in milliseconds since the first byte was read,
     * or since the resetStatistics() method was called.
     * @return the elapsed time in milliseconds since the first byte was read,
     * or since the resetStatistics() method was called.
     */
    public long getElapsedTime()
    {
        if (startTime == 0)
            return 0;
        else
            return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the average throughput in bytes per second.
     * @return the average throughput in bytes per second.
     */
    public double getAverageThroughput()
    {
        long elapsedTime = getElapsedTime();
        if (elapsedTime <= 0)
            return bytesSinceStartTime;
        else
            return ( ((double)bytesSinceStartTime) / ((double)elapsedTime) )
                    * 1000.0;
    }

    /**
     * Increment the counters.
     * @param i
     */
    private void increment(long i)
    {
        count += i;
        bytesSinceStartTime += i;
        if (startTime == 0)
            startTime = System.currentTimeMillis();
    }
}