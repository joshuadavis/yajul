package org.yajul.io;

import org.yajul.util.EnumerationIterator;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.NoSuchElementException;

/**
 * An iterator that returns all of the entry names in a zip file.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Apr 12, 2003
 * Time: 9:41:14 AM
 */
public class ZipFileNameIterator extends EnumerationIterator
{
    private boolean filesOnly;

    public ZipFileNameIterator(ZipFile zipfile,boolean filesOnly)
    {
        super(zipfile.entries());
        this.filesOnly = filesOnly;
    }

    /**
     * Returns the name of the next entry in the zip file.
     * @return the next element in the iteration.
     * @exception java.util.NoSuchElementException iteration has no more elements.
     */
    public Object next()
    {
        ZipEntry entry = (ZipEntry)super.next();
        // Skip directory entries, if needed.
        while (filesOnly
                && entry != null
                && entry.getName().endsWith("/")
                && super.hasNext())
            entry = (ZipEntry)super.next();     // Skip to the next entry.

        // Return null if the entry is null.
        if (entry == null)
            return null;
        else
            return entry.getName();
    }
}
