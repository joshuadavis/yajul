/**
 * TODO: Add file level comments.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Apr 12, 2003
 * Time: 10:42:20 AM
 */

package org.yajul.io.test;

import junit.framework.TestCase;
import org.yajul.io.DirectoryIterator;

import java.io.File;
import java.io.FileFilter;

/**
 * Unit test for the DirectoryIterator class.
 */
public class DirectoryIteratorTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public DirectoryIteratorTest(String name)
    {
        super(name);
    }

    /**
     * Simple smoke test for the directory iterator.
     */
    public void testDirectoryIterator() throws Exception
    {
        DirectoryIterator iter = new DirectoryIterator(new File("."));
        while (iter.hasNext())
        {
            File f = (File) iter.next();
            System.out.println(f.getCanonicalPath());
        }

    }

    /**
     * Simple smoke test for the directory iterator.
     */
    public void testFilter() throws Exception
    {
        DirectoryIterator iter = new DirectoryIterator(new File("."),
                new FileFilter() {
                    /**
                     * Tests whether or not the specified abstract pathname should be
                     * included in a pathname list.
                     *
                     * @param  pathname  The abstract pathname to be tested
                     * @return  <code>true</code> if and only if <code>pathname</code>
                     *          should be included
                     */
                    public boolean accept(File pathname)
                    {
//                        System.out.println("pathname.getName() = " + pathname.getName());
                        return pathname.getName().endsWith(".html");
                    }
                });

        while (iter.hasNext())
        {
            File f = (File) iter.next();
            System.out.println(f.getCanonicalPath());
        }

    }
}
