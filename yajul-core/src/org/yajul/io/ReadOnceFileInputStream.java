package org.yajul.io;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A FileInputStream that erases the underlying file when the stream is closed.  Useful when the application
 * needs a temporary file that is erased after it is fully read.
 * <hr>
 * User: jdavis<br>
 * Date: May 17, 2004<br>
 * Time: 10:53:28 AM<br>
 * @author jdavis
 */
public class ReadOnceFileInputStream extends FileInputStream
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(ReadOnceFileInputStream.class.getName());

    private File file;

    /**
     * Creates a <code>ReadOnceFileInputStream</code> by
     * opening a connection to an actual file,
     * the file named by the path name <code>name</code>
     * in the file system.  A new <code>FileDescriptor</code>
     * object is created to represent this file
     * connection.
     * <p>
     * First, if there is a security
     * manager, its <code>checkRead</code> method
     * is called with the <code>name</code> argument
     * as its argument.
     * <p>
     * If the named file does not exist, is a directory rather than a regular
     * file, or for some other reason cannot be opened for reading then a
     * <code>FileNotFoundException</code> is thrown.
     *
     * @param      name   the system-dependent file name.
     * @exception  FileNotFoundException  if the file does not exist,
     *                   is a directory rather than a regular file,
     *                   or for some other reason cannot be opened for
     *                   reading.
     * @exception  SecurityException      if a security manager exists and its
     *               <code>checkRead</code> method denies read access
     *               to the file.
     * @see        SecurityManager#checkRead(String)
     */
    public ReadOnceFileInputStream(String name) throws FileNotFoundException
    {
        super(name);
        file = new File(name);
    }

    /**
     * Creates a <code>ReadOnceFileInputStream</code> by
     * opening a connection to an actual file,
     * the file named by the <code>File</code>
     * object <code>file</code> in the file system.
     * A new <code>FileDescriptor</code> object
     * is created to represent this file connection.
     * <p>
     * First, if there is a security manager,
     * its <code>checkRead</code> method  is called
     * with the path represented by the <code>file</code>
     * argument as its argument.
     * <p>
     * If the named file does not exist, is a directory rather than a regular
     * file, or for some other reason cannot be opened for reading then a
     * <code>FileNotFoundException</code> is thrown.
     *
     * @param      file   the file to be opened for reading.
     * @exception  FileNotFoundException  if the file does not exist,
     *                   is a directory rather than a regular file,
     *                   or for some other reason cannot be opened for
     *                   reading.
     * @exception  SecurityException      if a security manager exists and its
     *               <code>checkRead</code> method denies read access to the file.
     * @see        java.io.File#getPath()
     * @see        SecurityManager#checkRead(String)
     */
    public ReadOnceFileInputStream(File file) throws FileNotFoundException
    {
        super(file);
        this.file = file;
    }

    /**
     * Closes this file input stream and releases any system resources
     * associated with the stream. NOTE: This method is automatically closed
     * when an unclosed stream is finalized.
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException
    {
        super.close();
        if (file != null && file.exists())
        {
            file.delete();
            if (log.isDebugEnabled())
                log.debug("close() : " + file.getAbsolutePath() + " deleted.");
        }
        file = null;
    }
}
