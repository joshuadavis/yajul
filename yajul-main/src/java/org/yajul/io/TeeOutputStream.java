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

import org.apache.log4j.Logger;
import org.yajul.log.LogUtil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream filter that writes to multiple underlying output streams.
 * @author josh
 */
public class TeeOutputStream extends FilterOutputStream
{
    private static Logger log = LogUtil.getLogger(TeeOutputStream.class);

    private OutputStream[] teeOut;    // Array of streams to write to.

    /**
     * Creates a new output stream that echoes output to both
     * of the specified streams.
     * @param out   The main underlying output stream.
     * @param out2  The 'tee' output stream.
     */
    public TeeOutputStream(OutputStream out, OutputStream out2)
    {
        super(out);
        teeOut = new OutputStream[1];
        teeOut[0] = out2;
    }

    /**
     * Creates a new output stream that writes output to all
     * of the streams in the array.
     * @param streams   An array of streams to write to.
     */
    public TeeOutputStream(OutputStream[] streams)
    {
        super(streams[0]);
        teeOut = new OutputStream[streams.length - 1];
        System.arraycopy(streams, 1, teeOut, 0, teeOut.length);
    }

    /**
     * Closes this output stream and releases any system resources
     * associated with the stream.
     * <p>
     * The <code>close</code> method of <code>FilterOutputStream</code>
     * calls its <code>flush</code> method, and then calls the
     * <code>close</code> method of its underlying output stream.
     * <p>
     * NOTE: This implementation also closes the 'tee' streams  as well.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterOutputStream#flush()
     * @see        FilterOutputStream#out
     */
    public void close() throws IOException
    {
        IOException e = null;

        super.close();  // Close the main underlying output stream.

        for (int i = 0; i < teeOut.length; i++)
        {
            try
            {
                teeOut[i].close();
            }
            catch (IOException ioe)
            {
                log.error("Unexpected I/O exception: " + ioe.getMessage(), ioe);
                e = ioe;
            }
        }
        teeOut = null;
        if (e != null)
            throw e;
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
        IOException e = null;

        super.flush();
        for (int i = 0; i < teeOut.length; i++)
        {
            try
            {
                teeOut[i].flush();
            }
            catch (IOException ioe)
            {
                log.error("Unexpected I/O exception: " + ioe.getMessage(), ioe);
                e = ioe;
            }
        }
        if (e != null)
            throw e;
    }

    /**
     * Writes <code>b.length</code> bytes to this output stream.
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code>
     * calls its <code>write</code> method of three arguments with the
     * arguments <code>b</code>, <code>0</code>, and
     * <code>b.length</code>.
     * <p>
     * Note that this method does not call the one-argument
     * <code>write</code> method of its underlying stream with the single
     * argument <code>b</code>.
     * <p>
     * NOTE: This implementation also writes to the 'tee' streams  as well.
     *
     * @param      b   the data to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterOutputStream#write(byte[], int, int)
     */
    public void write(byte b[]) throws IOException
    {
        IOException e = null;

        // NOTE: If super.write(b) is called, FilterOutputStream
        // will invoke write(b) in a loop.  So the output stream is being
        // invoked directly here.
        super.out.write(b);

        for (int i = 0; i < teeOut.length; i++)
        {
            try
            {
                teeOut[i].write(b);
            }
            catch (IOException ioe)
            {
                log.error("Unexpected I/O exception: " + ioe.getMessage(), ioe);
                e = ioe;
            }
        }
        if (e != null)
            throw e;
    }

    /**
     * Writes <code>len</code> bytes from the specified
     * <code>byte</code> array starting at offset <code>off</code> to
     * this output stream.
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code>
     * calls the <code>write</code> method of one argument on each
     * <code>byte</code> to output.
     * <p>
     * Note that this method does not call the <code>write</code> method
     * of its underlying input stream with the same arguments. Subclasses
     * of <code>FilterOutputStream</code> should provide a more efficient
     * implementation of this method.
     * <p>
     * NOTE: This implementation also writes to the 'tee' streams  as well.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterOutputStream#write(int)
     */
    public void write(byte b[], int off, int len) throws IOException
    {
        IOException e = null;

        // NOTE: If super.write(b,off,len) is called, FilterOutputStream
        // will invoke write(b) in a loop.  So the output stream is being
        // invoked directly here.

        super.out.write(b, off, len);

        for (int i = 0; i < teeOut.length; i++)
        {
            try
            {
                teeOut[i].write(b, off, len);
            }
            catch (IOException ioe)
            {
                log.error("Unexpected I/O exception: " + ioe.getMessage(), ioe);
                e = ioe;
            }
        }
        if (e != null)
            throw e;
    }

    /**
     * Writes the specified <code>byte</code> to this output stream.
     * <p>
     * The <code>write</code> method of <code>FilterOutputStream</code>
     * calls the <code>write</code> method of its underlying output stream,
     * that is, it performs <tt>out.write(b)</tt>.
     * <p>
     * Implements the abstract <tt>write</tt> method of <tt>OutputStream</tt>.
     * <p>
     * NOTE: This implementation also writes to the 'tee' streams  as well.
     *
     * @param      b   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException
    {
        IOException e = null;

        super.write(b);
        for (int i = 0; i < teeOut.length; i++)
        {
            try
            {
                teeOut[i].write(b);
            }
            catch (IOException ioe)
            {
                log.error("Unexpected I/O exception: " + ioe.getMessage(), ioe);
                e = ioe;
            }
        }
        if (e != null)
            throw e;
    }
}
