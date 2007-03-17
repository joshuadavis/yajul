package org.yajul.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares files by name.
 * <br>User: Joshua Davis
 * Date: Mar 17, 2007
 * Time: 9:10:44 AM
 */
public class FileComparator implements Comparator

{
    public static FileComparator INSTANCE = new FileComparator();

    public int compare(Object o1,
                       Object o2)
    {
        if (o1 == o2)
            return 0;

        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;

        File f1 = (File) o1;
        File f2 = (File) o2;

        if (f1.isDirectory() && f2.isFile())
            return -1;
        if (f1.isFile() && f2.isDirectory())
            return 1;
        return f1.getName().compareTo(f2.getName());
    }
}
