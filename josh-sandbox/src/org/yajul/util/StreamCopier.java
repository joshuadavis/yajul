/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 1:21:37 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util;

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
