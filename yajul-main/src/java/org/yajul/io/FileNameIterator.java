package org.yajul.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;

/**
 * An iterator that returns the absolute or relative path names of files or files and directories
 * in a directory tree.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: May 11, 2003
 * Time: 3:34:25 PM
 */
public class FileNameIterator implements Iterator
{
    private DirectoryIterator iter;
    private boolean absolute;
    private boolean filesOnly;
    private File    next;
    private int     rootPathLength;

    public FileNameIterator(File root,boolean absolute,boolean filesOnly)
    {
        init(new DirectoryIterator(root),absolute, filesOnly);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext()
    {
        return next != null;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next()
    {
        File rv = next;

        // Advance to the next file or directory using the iterator.
        advance();

        // Return null, if the next value was null.
        if (rv == null)
            return null;
        // Return the appropriate string.
        if (absolute)
            return rv.getAbsolutePath();
        else
            return rv.getAbsolutePath().substring(rootPathLength);
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove()
    {
        iter.remove();
    }

    private void init(DirectoryIterator iter,boolean absolute, boolean filesOnly)
    {
        this.iter = iter;
        this.absolute = absolute;
        this.filesOnly = filesOnly;
        // Advance 'next' to the next file or directory.
        advance();
        // Set the root path length, for relative file name iteration.
        rootPathLength = iter.getRoot().getAbsolutePath().length() + 1;
    }

    private void advance()
    {
        next = null;
        while (iter.hasNext())
        {
            next = (File)iter.next();
            // Continue looping if we're in files only mode, and the current file is a directory.
            if (filesOnly && next.isDirectory())
                next = (File)iter.next();
            // Otherwise, stop now.
            else
                break;
        }
    }
}
