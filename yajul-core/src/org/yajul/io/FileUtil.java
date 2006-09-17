// $Id$
package org.yajul.io;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides utility methods for dealing with files (java.io.File).
 *
 * @author josh May 4, 2004 7:08:23 AM
 */
public class FileUtil
{
    private static final int ONE_K = 1024;
    private static final int MINIMUM_TAIL_BUFFER = 3 * ONE_K;
    private static final int BYTES_PER_LINE = 80;

    /**
     * Deletes files or directories recursively, returns the number of files deleted.
     *
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

    /**
     * Like UN*X tail - writes the last 'n' lines of a file to the writer.
     *
     * @param numberOfLines the number of tail lines
     * @param file          the file to tail
     * @param writer        where to send the output
     * @return long - The position of the last byte read.
     */
    public static long tail(int numberOfLines, File file, Writer writer) throws IOException
    {
        // Calculate the number of bytes to skip.  Assume that each line has about 80 chars.
        long size = file.length();
        long tailbuffer = numberOfLines * BYTES_PER_LINE + ONE_K;
        long skipBytes = Math.max(size - tailbuffer, MINIMUM_TAIL_BUFFER);
        // Note: LineNumberReader extends buffered reader, so we don't need to
        // make a buffer.
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        reader.skip(skipBytes);
        // Read in lines, only keep the last 'n' lines.
        String line;
        List list = new LinkedList();
        while ((line = reader.readLine()) != null)
        {
            list.add(line);
            if (list.size() > numberOfLines)
                list.remove(0);
        }
        // Write out all the lines to the writer.
        PrintWriter pw = new PrintWriter(writer);
        for (Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            line = (String) iterator.next();
            pw.println(line);
        }
        return size;
    }
}
