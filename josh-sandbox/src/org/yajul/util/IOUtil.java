
package org.yajul.util;

// JDK
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Set;

import org.yajul.log.Logger;

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
    public static final void copy(InputStream in,OutputStream out,int bufsz)
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
                    out.write(buf,0,bytesRead);
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
    public static final void copy(InputStream in,OutputStream out)
        throws IOException
    {
        copy(in,out,DEFAULT_BUFFER_SIZE);
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
        copy(in,baos);
        return baos.toByteArray();
    }
    
    /**
     * make directory, making sure that the whole path is created
     * @param path The path to be created
     */
    public static synchronized void makeDir(String path)
    {
        StringTokenizer tok = new StringTokenizer(path, FILESEP);
        String dir = FILESEP;

        while(tok.hasMoreTokens())
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
    public static synchronized void clearDir(String path)
    {
        File dir = new File(path);

        if (dir.exists())
        {
            //empty it out
            if (log.isDebugEnabled())
                log.debug("Deleting files from directory " + path);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i ++)
                files[i].delete();
        }
        else
        {
            //make it
            makeDir (path);
        }
    }    

    /** Returns an array of strings representing all the subdirectories in the directory
     * @param d the directory
     * @return the list of the subdirectories
     */
    public static synchronized String[] listSubDirectories(String d)
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
    public static synchronized PrintStream getPrintStream(String path, String name, boolean append)
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
    public static synchronized PrintStream getPrintStream(String fullName, boolean append)
    {
        File file = new File(fullName.trim());
        if (!append && file.exists())
        {
            log.warn("Erasing existing file: " +  fullName);
            file.delete();
        }

        PrintStream output = null;
        try
        {
            output = new PrintStream(new FileOutputStream(fullName, append));
        }
        catch (IOException e1)
        {
            log.error("Problem writing to file: " + fullName,e1);
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
            return  file.getCanonicalPath().substring(
                file.getCanonicalFile().getParentFile().getCanonicalPath().length() + 1);
                
        }
        catch(IOException ioe)
        {
            // Well, this isn't supposed to happen, so return null.
            return null;
        }
    }        


    /** Return an array of strings representing all the files with a given extension in a directory
     *  (ex: ".xml", ".doc")
     *@param d the directory
     *@param extension usually the 3-letter extension of a file (ex: "xml" or "doc") - should not include the dot "."
     *@return the list of the names of files ending in the given extension (ex: ".xml", ".doc")
     */
    public static synchronized String[] getFilesWithExtension(String d, final String extension)
    {
        File dir = new File(d);

        if (!dir.isDirectory())
        {
            log.warn(d + " is not a directory");
            return new String[0];
        }

        if (extension == null)
        {
            log.warn("extension is null");
            return new String[0];
        }

        // return the filtered entries
        return dir.list(new FilenameFilter() 
            {
                public boolean accept(File f, String name) 
                {
                    return name.endsWith("." + extension);}
            }
                        );

    }

    /** Reads in a file which contains instrument names (one per line) 
     * and returns them as an ArrayList of strings
     * @param file name of the file that contains instrument names
     * @return ArrayList constaining instrument names  as Strings
     */
    public static ArrayList readNamesFromFile(String file)
    {
        return readNamesFromFile(file, null, null);
    }

    /** Reads in a file which contains (instrument) names with priorities and returns them as an array of strings.  The file
     * can contain comments (ex: lines starting with # for now) and each instrument must have a priority
     * number associated with the individual instrument if the priority is not null.  The priority is the last
     * character on the line, a number from 1 to 9 separated by a space from the name.
     * @param file name of the file that contains instrument names
     * @param priority the priority used to determine if a instruments is to be selected or not.  null
     *        means don't use (and don't expect) priority.  1 is lowest priority and it would include all
     *        names.  a priority of 5 would return only the names that have a priority written as 5 or lower.
     * @param commentChars a set of characters that denote the beginning of a line with comments (ex: "#" -
     *        for perl-style comments)
     * @return ArrayList constaining instrument names  as Strings
     */
    public static synchronized ArrayList readNamesFromFile (String file, Integer priority, Set commentChars)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
        }
        catch(FileNotFoundException e)
        {
            log.error("Cannot find file:" + e.getMessage());
            return null;
        }
        ArrayList list = new ArrayList();
        try
        {
            for (String input = br.readLine(); input != null; input = br.readLine())
            {
                //get rid of spaces at both ends
                String line = input.trim();
                //skip empty lines and comments. comments must be on lines that start with 
                //known comment chars, passed in the commentsChar set (ex: "#")
                if ( (line.length() == 0) || 
                     ((commentChars != null) && (commentChars.contains(line.substring(0,0)))))
                    continue;

                //got a line that has a valid entry. 
                //if priority is null, just add it; else, check the priority
                if (priority == null)
                {
                    list.add(line);
                }
                else
                {
                    //the priority is the last
                    //character on the line, a number from 1 to 9
                    //separated by a space from the name 
                    int readPriority = line.charAt(line.length() - 1) - '0';
                    if ((readPriority < 1) || (readPriority > 9))
                    {
                        log.error("Incorrect priority on line: " + line);
                        return null;
                    }
                    else if (readPriority >= priority.intValue())
                    {
                        //got correct entry, adding it, without the last 2 chars
                        list.add(line.substring(0, line.length() - 2));
                    }
                }
            }
        }
        catch (IOException e)
        {
            log.error("Error while reading " + file);
            return null;
        }
        return list;
    }

}

