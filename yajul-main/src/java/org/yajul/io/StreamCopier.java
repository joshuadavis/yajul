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

import org.yajul.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides stream copying capability in a Runnable class.  This can be used to
 * redirect streams from a spawned JVM, or to 'pump' a one side of PipedInputStream /
 * PipedOutputStream pair.
 * @author Joshua Davis
 */
public class StreamCopier implements Runnable
{
    private static Logger log = Logger.getLogger(StreamCopier.class);

    /** The default buffer size. **/
    public static final int DEFAULT_BUFFER_SIZE = 256;
    private InputStream in;
    private OutputStream out;
    private int bufsz = DEFAULT_BUFFER_SIZE;

    /**
     * Creates a new stream copier, that will copy the input stream into the output
     * stream when the run() method is caled.
     * @param    in     The input stream to read from.
     * @param    out    The output stream to write to.
     */
    public StreamCopier(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
    }

    /**
     * This method will copy the input into the output until there is no more input.
     *
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public void run()
    {
        byte[] buf = new byte[bufsz];
        int bytesRead = 0;
        try
        {
            while (true)
            {
                bytesRead = in.read(buf);
                if (bytesRead == -1)
                    break;
                out.write(buf, 0, bytesRead);
            } // while
            out.flush();
        }
        catch (IOException e)
        {
            log.unexpected(e);
        }
    }
}
