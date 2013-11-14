package org.yajul.io.archiver;

import java.io.InputStream;

/**
 * Provides the filename and input stream for a given document.
 */
public class Source
{
    private String filename;
    private InputStream stream;

    /**
     * Creates a source.
     *
     * @param filename The filename that the input stream is pointing to.
     * @param in       The input stream.
     */
    Source(String filename, InputStream in)
    {
        this.filename = filename;
        this.stream = new InputWrapper(this,in);
    }

    /**
     * Returns the name of the file that the input stream will read from.
     *
     * @return the name of the file that the input stream will read from.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Returns the input stream, which will read from a file.
     *
     * @return the input stream, which will read from a file.
     */
    public InputStream getStream()
    {
        return stream;
    }

    protected void finalize() throws Throwable
    {
        super.finalize();
        if (stream != null)
            stream.close();
    }

    public void afterStreamClosed()
    {
    }
} // class Source
