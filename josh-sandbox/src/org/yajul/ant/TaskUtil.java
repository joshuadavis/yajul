/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 16, 2002
 * Time: 6:28:21 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.ant;

import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Task;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides a set of generic ANT functions.
 * @author Joshua Davis
 */
public class TaskUtil
{
    /**
     * Returns a list of file names using a directory scanner for each
     * element of the list, which is assumed to contain instances of
     * FileSet.
     * @param task      The task that is using this method (used to create directory scanner).
     * @param fileSets  A list of FileSet objects.
     * @return String[] The concatenated list of all files in all file sets in the list.
     * @see org.apache.tools.ant.types.FileSet
     * @see org.apache.tools.ant.FileScanner
     */
    public static final String[] getAllFileNames(Task task,List fileSets)
    {
//        task.log("getAllFileNames() : ENTER");
        String[] fileNames;
        Iterator i = fileSets.iterator();
        List list = new ArrayList();
        while (i.hasNext())
        {
            FileSet fileset = (FileSet) i.next();
            FileScanner scanner = fileset.getDirectoryScanner(task.getProject());
            String files[] = scanner.getIncludedFiles();
            list.addAll(Arrays.asList(files));
        }
        fileNames = (String[]) list.toArray(new String[list.size()]);
        return fileNames;
    }
}
