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
     * @param in - The input stream.
     * @param out - The output stream.
     * @param bufsz - The size of the buffer to use.
     * @throws IOException - When the stream could not be copied.
     **/
    public static final void copy(InputStream in, OutputStream out, int bufsz)
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
                while (true)
                {
                    bytesRead = in.read(buf);
                    if (bytesRead == -1)
                        break;
                    out.write(buf, 0, bytesRead);
                } // while
            } // synchronized (out)
        } // synchronized (in)
    }

    /**
     * Copies the input stream into the output stream in a thread safe and efficient manner.
     * @param in - The input stream.
     * @param out - The output stream.
     * @throws IOException - When the stream could not be copied.
     **/
    public static final void copy(InputStream in, OutputStream out)
            throws IOException
    {
        copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Reads the input stream into an array of bytes.
     * @param in - The input stream.
     * @return byte[] - The array of bytes copied from the stream.
     * @throws IOException - When the stream could not be copied.
     */
    public static final byte[] toByteArray(InputStream in) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(in, baos);
        return baos.toByteArray();
    }

    /**
     * Make directory, making sure that the whole path is created
     * @param path The path to be created
     */
    public static final void makeDir(String path)
    {
        StringTokenizer tok = new StringTokenizer(path, FILESEP);
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
    public static final void clearDir(String path)
    {
        File dir = new File(path);

        if (dir.exists())
        {
            // empty it out
            if (log.isDebugEnabled())
                log.debug("Deleting files from directory " + path);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++)
                files[i].delete();
        }
        else
        {
            //make it
            makeDir(path);
        }
    }

    /** Returns an array of strings representing all the subdirectories in the directory
     * @param d the directory
     * @return the list of the subdirectories
     */
    public static final String[] listSubDirectories(String d)
    {
        File dir = new File(d);
        if (!dir.isDirectory())
        {
            log.error(d + " is not a directory");
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
     * Perfom preliminary opeation for writing to a file
     * @param path Directory name.
     * @param name File name (without directory)
     * @param append if true, the file will be open for appending,
     *        else, if false, the existing file will be deleted
     * @return the PrintStream object ready for printing
     */
    public static final PrintStream getPrintStream(String path, String name, boolean append)
    {
        if (!path.endsWith(FILESEP))
            path = path + FILESEP;
        File fpath = new File(path);
        // create the path if it doesn't exist
        if (!fpath.exists())
            makeDir(path);
        return getPrintStream(path + name.trim(), append);
    }

    /**
     * Perfom preliminary opeation for writing to a file
     * @param fullName File name (including path)
     * @param append if true, the file will be open for appending,
     *        else, if false, the existing file will be deleted
     *
     * @return the PrintStream object ready for printing
     */
    public static final PrintStream getPrintStream(String fullName, boolean append)
    {
        File file = new File(fullName.trim());
        if (!append && file.exists())
        {
            log.warn("Erasing existing file: " + fullName);
            file.delete();
        }

        PrintStream output = null;
        try
        {
            output = new PrintStream(new FileOutputStream(fullName, append));
        }
        catch (IOException e1)
        {
            log.error("Problem writing to file: " + fullName, e1);
            return null;
        }
        return output;
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

