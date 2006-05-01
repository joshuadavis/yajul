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
import org.yajul.util.Copier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Provides stream copying capability in a Runnable class.  This can be used to
 * redirect streams from a spawned JVM, or to 'pump' a one side of
 * PipedInputStream / PipedOutputStream pair.<br>
 * Also provides a static method that copies an entire input stream into
 * an output stream.
 * @author Joshua Davis
 */
public class StreamCopier implements Runnable
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(StreamCopier.class);

    /** The default buffer size. **/
    public static final int DEFAULT_BUFFER_SIZE = 256;
    /** The input stream. **/
    private InputStream in;
    /** The output stream. **/
    private OutputStream out;
    /** The buffer size to use while copying. **/
    private int bufsz = DEFAULT_BUFFER_SIZE;
    /** If an exception was thrown in the run() method, this will be set. **/
    private IOException exception;
    /** True, if the copying is complete. **/
    private boolean complete = false;
    private static final int DEFAULT_BYTE_ARRAY_BUFSZ = 128;
    private static final int UNLIMITED = -1;

    /**
     * Copies the input stream into the output stream in a thread safe and
     * efficient manner.
     * @param in The input stream.
     * @param out The output stream.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static final int copy(InputStream in, OutputStream out, int bufsz)
            throws IOException
    {
        // From Java I/O, page 43
        // Do not allow other threads to read from the input or write to the
        // output while the copying is taking place.
        synchronized (in)
        {
            if (out != null)
            {
                synchronized (out)
                {
                    return unsyncCopy(in, out, bufsz);
                } // synchronized (out)
            }
            else
            {
                return unsyncCopy(in, out, bufsz);
            }
        } // synchronized (in)
    }

    /**
     * Copies the input stream into the output stream in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     * @param in The input stream.
     * @param out The output stream.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public final static int unsyncCopy(InputStream in, OutputStream out,
                                       int bufsz) throws IOException
    {
        return unsyncCopy(in, out, bufsz, UNLIMITED);
    }

    /**
     * Copies the input stream into the output stream in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     * @param in The input stream.
     * @param out The output stream.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     * until the end of the input stream.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static int unsyncCopy(InputStream in, OutputStream out, int bufsz, int limit) throws IOException
    {
        return Copier.copy(in,out,bufsz,limit);
    }

    /**
     * Copies the input stream (reader) into the output stream (writer) in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     * @param in The input reader
     * @param out The output writer.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     * until the end of the input stream.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static int unsyncCopy(Reader in, Writer out, int bufsz, int limit) throws IOException
    {
        return Copier.copy(in,out,bufsz,limit);
    }

    /**
     * Copies the input stream into the output stream in a thread safe and
     * efficient manner.
     * @param in The input stream.
     * @param out The output stream.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static final int copy(InputStream in, OutputStream out)
            throws IOException
    {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }


    /**
     * Reads the entire input stream into an array list of byte arrays, each
     * byte array being a maximum of 'blocksz' bytes long.
     * @param blocksz The block size.  Byte arrays in the list will not
     * be longer than this.
     * @param in The input stream.
     * @return ArrayList - An array list of byte arrays.
     * @throws IOException When something happens while reading the stream.
     */
    public static final ArrayList readBlocks(InputStream in, int blocksz)
            throws IOException
    {
        ArrayList list = new ArrayList();
        byte[] chunk = null;
        byte[] buf = new byte[blocksz];
        int bytesRead = 0;
        while (true)
        {
            bytesRead = in.read(buf);
            if (bytesRead == -1)
                break;
            // Add a new chunk to the list.
            chunk = new byte[bytesRead];
            System.arraycopy(buf,0,chunk,0,bytesRead);
            list.add(chunk);
        } // while
        return list;
    }

    /**
     * Reads the entire input stream into a byte array.
     * @param in The input stream.
     * @throws IOException When something happens while reading the stream.
     */
    public static final byte[] readByteArray(InputStream in)
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        unsyncCopy(in,baos,DEFAULT_BYTE_ARRAY_BUFSZ);
        return baos.toByteArray();
    }

    /** Reads the entire input stream into a byte array with a limit.
     * @param in The input reader
     * @param limit The number of bytes to read.
     * @exception IOException Thrown if there was an error while copying.
     * @return An array of bytes read from the input.
     */
    public static byte[] readByteArray(InputStream in,int limit)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        unsyncCopy(in,baos,DEFAULT_BUFFER_SIZE,limit);
        return baos.toByteArray();
    }

    /** Reads the entire input stream into a byte array with a limit.
     * @param in The input reader
     * @param limit The number of bytes to read.
     * @exception IOException Thrown if there was an error while copying.
     * @return An array of bytes read from the input.
     */
    public static byte[] readByteArray(Reader in,int limit)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        unsyncCopy(in,new OutputStreamWriter(baos),DEFAULT_BUFFER_SIZE,limit);
        return baos.toByteArray();
    }

    /**
     * Reads the entire input stream into a byte array.
     * @param in The input reader
     * @exception IOException Thrown if there was an error while copying.
     * @return An array of bytes read from the input.
     */
    public static byte[] readByteArray(Reader in)
        throws IOException
    {
        return readByteArray(in,-1);
    }

    /**
     * Reads the specified file into a byte array.
     * @param file The file to read.
     * @throws IOException When something happens while reading the stream.
     */
    public static final byte[] readByteArray(File file)
            throws IOException
    {
        return readByteArray(new BufferedInputStream(
                new FileInputStream(file),DEFAULT_BUFFER_SIZE));
    }

    /**
     * Reads the specified file into a byte array.
     * @param fileName The file name to read.
     * @throws IOException When something happens while reading the stream.
     */
    public static final byte[] readFileIntoByteArray(String fileName)
            throws IOException
    {
        return readByteArray(new File(fileName));
    }

    /**
     * Serializes the object into an array of bytes.
     * @param o The object to serialize.
     * @return An array of bytes that contiains the serialized object.
     */
    public static final byte[] serializeObject(Object o) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * Reads a serialized object from the array of bytes.
     * @param bytes The array of bytes.
     * @return The unserialized object.
     * @throws IOException if there was a problem reading the input.
     * @throws ClassNotFoundException if the class of the object in the input was not found.
     */
    public static final Object unserializeObject(byte[] bytes) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    /**
     * Creates a new stream copier, that will copy the input stream into the
     * output stream when the run() method is caled.
     * @param    in     The input stream to read from.
     * @param out The output stream.  If this is null, the input will be
     * discarded, similar to piping to /dev/null on UN*X.
     */
    public StreamCopier(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
    }

    /**
     * This method will copy the input into the output until there is no more
     * input.  Since this method is typically run by a thread, exceptions
     * are not thrown from it.  Instead, the exception can be read using
     * the getException() method.
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
        try
        {
            // Copy, using the a buffer.
            unsyncCopy(in, out, bufsz);
            // Flush the output.
            if (out != null)
                out.flush();
            // Completed state.
            synchronized (this)
            {
                complete = true;
            }
        }
        catch (IOException e)
        {
            // Log the exception!
            log.error("Unexpected: " + e.getMessage(), e);
            // Remember the exception, just in case anyone cares.
            synchronized (this)
            {
                exception = e;
            }
        }
    }

    /**
     * Returns the exception thrown in the run() method, if any.
     * @return IOException  - The exception thrown during the run() method,
     * or null if there were no errors.
     */
    public IOException getException()
    {
        synchronized (this)
        {
            return exception;
        }
    }

    /**
     * Returns true if the copying is complete.
     * @return boolean - true if the copying is complete.  Returns false if
     * the copying is in progress, not started, or encountered an error.
     */
    public boolean isComplete()
    {
        synchronized (this)
        {
            return complete;
        }
    }
}
