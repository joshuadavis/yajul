// $Id$
package org.yajul.io;

import org.apache.log4j.Logger;
import org.yajul.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Javabean that can store objects in the filesystem.  The objects are stored in a hierarchy of directories
 * corresponding to the date value passed in.  Callers should persist the file name passed back from storeObject()
 * so that the objects can easily be retrieved without knowing the date, etc.
 * <br>
 * An example of a Spring initializer is listed here:
 * <pre>
 * &lt;bean id="documentArchiver" class="com.kiodex.persistence.filesystem.DocumentArchiver"
 *    init-method="init">
 *    &lt;property name="storeageDirectoryName">&lt;value>/archive/documents&lt;/value>&lt;/property>
 *    &lt;property name="retrieveDirectories">
 *        &lt;list>
 *            &lt;value>/archive/old/documents&lt;/value>
 *        &lt;/list>
 *    &lt;/property>
 *    &lt;property name="extension">&lt;value>.dat.gz&lt;/value>&lt;/property>
 * &lt;/bean>
 * </pre>
 * User: jdavis
 * Date: Mar 5, 2004
 * Time: 6:07:28 PM
 * @author josh May 6, 2004 11:41:01 PM
 */
public class DocumentArchiver
{
    private static Logger log = Logger.getLogger(DocumentArchiver.class.getName());

    public static final String DEFAULT_EXTENSION = ".dat.gz";

    private File storeageDirectory;
    private String extension = DEFAULT_EXTENSION;
    private List retrieveDirectories;
    private boolean gzip = true;

    /**
     * Returns the storage directory name.
     * @return the storage directory name.
     */
    public File getStoreageDirectory()
    {
        return storeageDirectory;
    }

    /**
     * Sets the directory where documents will be stored.
     * @param storeageDirectory The document storeage directory.
     */
    public void setStoreageDirectory(File storeageDirectory)
    {
        if (!storeageDirectory.isDirectory())
            throw new IllegalArgumentException(storeageDirectory.toString() + " is not a directory!");
        this.storeageDirectory = storeageDirectory;
    }

    /**
     * Returns the filename extension that will be used for the stored documents.
     * @return the filename extension that will be used for the stored documents.
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Sets the filename extension used for stored documents.
     * @param extension The filename extension, defaults to '.dat.gz'.
     */
    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    /**
     * Returns the list of retrieval directories.   Documents not found in the storeage directory will
     * be searched along this path of directories.
     * @return the list of retrieval directories
     */
    public List getRetrieveDirectories()
    {
        return retrieveDirectories;
    }


    /**
     * Sets the retrieval directories, which will be scanned in order if a document is not found in the
     * storeage directory.
     * @param retrieveDirectories A list of directory names.
     */
    public void setRetrieveDirectories(List retrieveDirectories)
    {
        this.retrieveDirectories = retrieveDirectories;
    }

    /**
     * Returns true if documents are being GZIP compressed.
     * @return true if documents are being GZIP compressed.
     */
    public boolean isGzip()
    {
        return gzip;
    }

    /**
     * Enbales/disables GZIP compression.
     * @param gzip True for gzip compression, false for uncompressed.
     */
    public void setGzip(boolean gzip)
    {
        this.gzip = gzip;
    }

    /**
     * Initializes the bean.
     * @throws IOException if something goes wrong.
     */
    public void init() throws IOException

    {
        log.info("init() : ENTER");
        try
        {
            if (storeageDirectory.exists())
            {
                if (!storeageDirectory.isDirectory())
                    throw new IOException("'" + storeageDirectory + "' is not a directory.");
            }
            else
            {
                storeageDirectory.mkdirs();
            }
        }
        catch (IOException e)
        {
            log.error(e, e);
            throw e;
        }
        finally
        {
            log.info("init() : LEAVE");
        }
    }


    /**
     * Stores an object, given the id, date and sub-directory.
     * @param subDirectory The sub-directory of the storeage directory where documents of this type are stored.
     * @param id The id object that will be used to generate the file name.
     * @param date The date, which will be used to generate the diretory name.
     * @param object The object that will be stored.
     * @return The name of the file that was used to store the object.
     * @throws IOException if something goes wrong.
     */
    public String storeObject(String subDirectory, Object id, Date date, Object object) throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("storeObject() : ENTER");
        try
        {
            String fileName = generateFileName(id, date);
            // If a storeage sub-directory was specified, use it.
            File dir = getSubDirectory(storeageDirectory, subDirectory);
            File path = new File(dir, fileName);
            String pathname = path.getAbsolutePath();
            File f = new File(pathname);
            if (f.exists())
            {
                int i = 1;
                File backup = new File(pathname + "." + i);
                while (backup.exists())
                {
                    i++;
                    backup = new File(pathname + "." + i);
                }
                log.info("storeObject() : Renaming existing file to " + backup.getAbsolutePath());
                f.renameTo(backup);
                f = new File(pathname);
            }
            log.info("storeObject() : " + f.getAbsolutePath());
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream((gzip) ? (OutputStream) new GZIPOutputStream(fos) : fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
            if (log.isDebugEnabled())
                log.debug("storeObject() : Object sucessfully stored.");
            return fileName;    // Return the relative file name.
        }
        catch (IOException e)
        {
            log.error(e, e);
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug("storeObject() : LEAVE");
        }
    }

    /**
     * Returns a sub directory of the base directory, if a sub-directory
     * was specified.
     * @param subDirectory The sub-directory (optional.
     * @return a sub directory of the base directory, if a sub-directory
     *         was specified.
     */
    private File getSubDirectory(File base, String subDirectory)
    {
        return (StringUtil.isEmpty(subDirectory)) ? base : new File(base, subDirectory);
    }

    /**
     * Generates a file name from an object id and a date.
     * @param id The object id, used to generate the unique file name.
     * @param date The date, used to create the directory path.
     * @return The filename.
     */
    public String generateFileName(Object id, Date date)
    {
        // Generate the file name.
        String fileName = id.toString();
        fileName = fileName.replace('/', '-');
        fileName = fileName.replace(' ', '_');
        fileName = fileName.replace('\t', '_');
        fileName = fileName.replace('\r', '_');
        fileName = fileName.replace('\n', '_');
        fileName = fileName + extension;
        // Get the month and year as a string, with the file separator in the middle.
        SimpleDateFormat df = new SimpleDateFormat(File.separator +
                "yyyy" +
                File.separator +
                "yyyy_MM" +
                File.separator +
                "yyyy_MM_dd" +
                File.separator);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        fileName = df.format(date) + fileName;
        return fileName;
    }

    /**
     * Retrieves an object given the sub-directory, the id, and the date.
     * @param subDirectory The sub-directory.
     * @param id The object id.
     * @param date The date.
     * @return The object.
     * @throws IOException if something goes wrong.
     */
    public Object retrieveObject(String subDirectory, Object id, Date date) throws IOException
    {
        String fileName = generateFileName(id, date);
        return retrieveObject(subDirectory, fileName);
    }

    /**
     * Retrieves an object given the sub-directory and the file name.
     * @param subDirectory The sub-directory.
     * @param fileName The name of the file, as returned by the storeObject() method.
     * @return The object.
     * @throws IOException if something goes wrong.
     */
    public Object retrieveObject(String subDirectory, String fileName) throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("retrieveObject() : ENTER");
        try
        {
            // If a storeage sub-directory was specified, use it.
            File dir = getSubDirectory(storeageDirectory, subDirectory);
            File f = new File(dir, fileName);

            // If the file doesn't exist in the primary storeage directory, then
            // check the other directories.
            if (!f.exists())
            {
                if (log.isDebugEnabled())
                    log.debug("retrieveObject() : " + f.getAbsolutePath() + " doesn't exist.");
                for (Iterator iterator = retrieveDirectories.iterator(); iterator.hasNext();)
                {
                    String baseString = (String) iterator.next();
                    File base = new File(baseString);
                    if (!base.exists())
                    {
                        if (log.isDebugEnabled())
                            log.debug("retrieveObject() : directory " + base + " does not exist, skipping.");
                        continue;
                    }
                    dir = getSubDirectory(base, subDirectory);
                    f = new File(dir, fileName);
                    if (f.exists())
                        return retrieveObject(f);   // Return the object.
                } // for
                throw new FileNotFoundException("Unable to find " + fileName + " in any archiver directory.");
            } // if !f.exists()
            else
                return retrieveObject(f);   // Return the object.
        } // try
        catch (FileNotFoundException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            log.error(e, e);
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            log.error(e, e);
            throw new IOException("Class not found! " + e.getMessage());
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug("retrieveObject() : LEAVE");
        }
    }

    private Object retrieveObject(File f) throws IOException, ClassNotFoundException
    {
        log.info("retrieveObject() : " + f.getAbsolutePath());
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream((gzip) ? (InputStream) new GZIPInputStream(fis) : fis);
        Object o = ois.readObject();
        if (log.isDebugEnabled())
            log.debug("retrieveObject() : Object sucessfully retrieved.");
        return o;
    }


}
