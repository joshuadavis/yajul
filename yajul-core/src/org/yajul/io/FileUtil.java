// $Id$
package org.yajul.io;

import java.io.File;
import java.io.IOException;

/**
 * Provides utility methods for dealing with files (java.io.File).
 * 
 * @author josh May 4, 2004 7:08:23 AM
 */
public class FileUtil
{
    /**
     * Deletes files or directories recursively, returns the number of files deleted.
     * @param f The file or directory to delete.
     * @return The number of files deleted.
     * @throws IOException if something goes wrong.
     */
    public static int recursiveDelete(File f) throws IOException
    {
        if (f.isFile())
        {
            if (f.delete())
                return 1;
            else
                return 0;
        }
        else if (f.isDirectory())
        {
            File[] files = f.listFiles();
            int count = 0;
            for (int i = 0; i < files.length; i++)
                count += recursiveDelete(files[i]);
            if (f.delete())
                count++;
            return count;
        }
        else
            throw new IOException("File is not a file or a directory!");
    }
}
