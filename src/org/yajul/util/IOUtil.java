package org.yajul.util;

// JDK

import org.yajul.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.CharArrayReader;
import java.io.Writer;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

/**
 * Provides static methods that implement commonly used IO functions.
 * @author Joshua Davis
 * @version 1.0
 */
public class IOUtil
{
    /** The default buffer size for the copy() method. **/
    public static final int DEFAULT_BUFFER_SIZE = 256;

    /** The file separator string ("/" on UNIX) */
    public static final String FILESEP = System.getProperty("file.separator");

    /** A logger for this class. */
    private static Logger log = Logger.getLogger(IOUtil.class.getName());

    /**
     * Copies the input stream into the output stream in a thread safe and efficient manner.
     * @param in The input stream.
     * @param out The output stream.
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
            synchronized (out)
            {
                byte[] buf = new byte[bufsz];
                int bytesRead = 0;
                int total = 0;
                while (true)
                {
                    bytesRead = in.read(buf);
                    if (bytesRead == -1)
                        break;
                    total += bytesRead;
                    out.write(buf, 0, bytesRead);
                } // while
                return total;
            } // synchronized (out)
        } // synchronized (in)
    }

    /**
     * Copies the input reader into the output writer in a thread safe and efficient manner.
     * @param in The input reader.
     * @param out The output writer.
     * @param bufsz The size of the buffer to use.
     * @return int The number of bytes copied.
     * @throws IOException  When the stream could not be copied.
     **/
    public static final int copy(Reader in, Writer out, int bufsz)
            throws IOException
    {
        // From Java I/O, page 43
        // Do not allow other threads to read from the input or write to the
        // output while the copying is taking place.
        synchronized (in)
        {
            synchronized (out)
            {
                char[] buf = new char[bufsz];
                int bytesRead = 0;
                int total = 0;
                while (true)
                {
                    bytesRead = in.read(buf);
                    if (bytesRead == -1)
                        break;
                    total += bytesRead;
                    out.write(buf, 0, bytesRead);
                } // while
                return total;
            } // synchronized (out)
        } // synchronized (in)
    }

    /**
     * Copies the input stream into the output stream in a thread safe and efficient manner.
     * @param in The input stream.
     * @param out The output stream.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static final int copy(InputStream in, OutputStream out)
            throws IOException
    {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies the input reader into the output writer in a thread safe and efficient manner.
     * @param in The input reader.
     * @param out The output writer.
     * @return int The number of bytes copied.
     * @throws IOException When the stream could not be copied.
     **/
    public static final int copy(Reader in, Writer out)
            throws IOException
    {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Reads the input stream into an array of bytes.
     * @param in The input stream.
     * @return byte[] The array of bytes copied from the stream.
     * @throws IOException When the stream could not be copied.
     */
    public static final byte[] toByteArray(InputStream in) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(in, baos);
        return baos.toByteArray();
    }

    /**
     * Copies the input byte array into the output (writer).  Returns the number of
     * bytes.
     * @param in The input byte array.
     * @param out The output writer.
     * @return The number of bytes copied.
     */
    public static int copy(byte[] in,Writer out)
        throws IOException
    {
        return copy(new InputStreamReader(new ByteArrayInputStream(in)),out);
    }

    /**
     * Reads the entire input stream into a char array.
     * @param in The input reader
     * @return char[] The array of characters.
     */
    public static char[] readCharArray(Reader in)
        throws IOException
    {
        CharArrayWriter caw = new CharArrayWriter();
        copy(in,caw);
        return caw.toCharArray();
    }

    /**
     * Reads the entire input stream into a byte array.
     * @param in The input reader
     * @return byte[] The array of bytes.
     */
    public static byte[] readByteArray(Reader in)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(in,new OutputStreamWriter(baos));
        return baos.toByteArray();
    }

    /**
     * Copies the stream into the output and returns a new stream.
     */
    public static Reader duplicate(Reader in,Writer out)
        throws IOException
    {
        char [] buf = readCharArray(in);
        out.write(buf);
        out.flush();
        return new CharArrayReader(buf);
    }

    /**
     * Copies the stream into both output streams.
     * and returns a new stream.
     */
    public static Reader duplicate(Reader in,Writer out,Writer out2)
        throws IOException
    {
        char [] buf = readCharArray(in);
        out.write(buf);
        out.flush();
        out2.write(buf);
        out2.flush();
        return new CharArrayReader(buf);
    }

    /**
     * Make directory, making sure that the whole path is created
     * @param path The path to be created
     */
    public static final void makeDir(File path)
    {
        StringTokenizer tok = new StringTokenizer(path.getAbsolutePath(), FILESEP);
        String dir = FILESEP;

        while (tok.hasMoreTokens())
        {
            dir = dir + tok.nextToken() + FILESEP;
            File f = new File(dir);
            if (!f.exists())
                f.mkdir();
        }
    }

    /**
     * Deletes only the files (not the subdirectories) from a directory,
     * or creates it if it does not exist.  Be very <b>CAREFUL</b> when using this
     * method, since it does not ask for any confirmation before deleting
     * all the files in the given directory.
     * @param path the path to the directory
     */
    public static final void clearDir(File dir)
    {
        if (dir.exists())
        {
            // empty it out
            if (log.isDebugEnabled())
                log.debug("Deleting files from directory " + dir);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++)
                files[i].delete();
        }
        else
        {
            //make it
            makeDir(dir);
        }
    }

    /** Returns an array of strings representing all the subdirectories in the directory
     * @param d the directory
     * @return the list of the subdirectories
     */
    public static final String[] listSubDirectories(File dir)
    {
        if (!dir.isDirectory())
        {
            log.error(dir + " is not a directory");
            return new String[0];
        }
        // return the filtered entries
        return dir.list(
                new FilenameFilter()
                {
                    public boolean accept(File directory, String name)
                    {
                        return (new File(directory, name)).isDirectory();
                    }
                } // anonymous inner class
        );
    }

    /**
     * Returns just the file name part of a file path.  For example:<br>
     * <pre>
     * "/home/jdavis/stuff/SomeFile.txt" => "SomeFile.txt"
     * </pre>
     * @param file - The file to get the name of.
     * @return String - The file name.
     */
    public static final String getFileName(File file)
    {
        try
        {
            if (file.isDirectory())
                return null;
            if (file.getParentFile() == null)
                return null;
            return file.getCanonicalPath().substring(
                    file.getCanonicalFile().getParentFile().getCanonicalPath().length() + 1);

        }
        catch (IOException ioe)
        {
            // Well, this isn't supposed to happen, so return null.
            return null;
        }
    }
}

