package org.yajul.io.archiver;

import java.io.OutputStream;

/**
 * Provides the filename and output stream for a given document.
 */
public class Sink
{
    private String filename;
    private OutputStream stream;

    /**
     * Creates a sink.
     *
     * @param filename The filename that the output stream is pointing to.
     * @param out      The output stream.
     */
    Sink(String filename, OutputStream out)
    {
        this.filename = filename;
        this.stream = out;
    }

    /**
     * Returns the name of the file that the output stream will write to.
     *
     * @return the name of the file that the output stream will write to.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Returns the output stream, which will write to a file.
     *
     * @return the output stream, which will write to a file.
     */
    public OutputStream getStream()
    {
        return stream;
    }

    protected void finalize() throws Throwable
    {
        super.finalize();
        if (stream != null)
            stream.close();
    }
} // class Sink
