package org.yajul.io;

import org.yajul.util.ArrayIterator;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Iterates through the Files in a directory, recursively.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Apr 12, 2003
 * Time: 10:37:12 AM
 */
public class DirectoryIterator implements Iterator
{
    private File root;
    private File next;
    private LinkedList stack;
    private FileFilter filter;

    /**
     * Creates a new directory iterator for the specified directory.
     * @param root The root of the recursive directory iteration.
     */
    public DirectoryIterator(File root)
    {
        init(root,null);
    }

    /**
     * Creates a new directory iterator with a filter.
     * @param root The directory to iterate recursively.
     * @param filter The filter to use.
     */
    public DirectoryIterator(File root,FileFilter filter)
    {
        init(root,filter);
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
        if (next == null)
            throw new NoSuchElementException("The end of the iteration has already been reached!");

        Iterator i = peek();
        if (i == null)
        {
            next = null;
            throw new NoSuchElementException("The end of the iteration has already been reached!");
        }

        File rv = next;   // Return the current value of next.

        // Remove all empty iterators from the stack.
        while (!i.hasNext())
        {
            if (stack.size() > 1)
            {
                pop();
                i = peek();
            }
            else    // No more elements in the stack...
            {
                next = null;    // No more elements to iterate.
                return rv;      // Return the previous value of next.
            }
        } // while

        // Get the next element in the iterator.
        next = (File)i.next();

        // If the next file is a directory, then push it onto the stack as an iterator.
        if (next.isDirectory())
            push(next);

        return rv;
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
        throw new UnsupportedOperationException("DirectoryIterator does not support remove()!");
    }

    /**
     * Returns the root file of this directory iterator.
     * @return File - The root file of this directory iterator.
     */
    public File getRoot()
    {
        return root;
    }

    // --- Implementation methods ---

    private void init(File root,FileFilter filter)
    {

        if (!root.isDirectory())
            throw new IllegalArgumentException("Directory expected, " + root + " is not a directory.");

        this.root = root;

        // If a filter was specified, wrap it in a filter that *always* accepts directories
        // so the filter will not interfere with the recursion.
        if (filter != null)
            this.filter = new DirectoryAcceptFilter(filter);
        else
            this.filter = null;

        stack = new LinkedList();
        push(root);

        if (stack.size() > 0)
        {
            Iterator iter = peek();
            next = (iter.hasNext()) ? (File)iter.next() : null;
        }
        else
            next = null;
    }

    private void push(File file)
    {
        File[] files = (filter == null) ? file.listFiles() : file.listFiles(filter);
        if (!(files == null || files.length == 0))
        {
            // Push an iterator for the files in this directory onto the stack.
            ArrayIterator dirIterator = new ArrayIterator(files);
            stack.addFirst(dirIterator);
        }
    }

    private void pop()
    {
        stack.removeFirst();
    }

    private Iterator peek()
    {
        return (Iterator)stack.getFirst();
    }

    /**
     * A filter that always accepts directories, passing files on to a delegate for filtering.
     */
    private static class DirectoryAcceptFilter implements FileFilter
    {
        private FileFilter delegate;

        public DirectoryAcceptFilter(FileFilter delegate)
        {
            this.delegate = delegate;
        }

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
            if (pathname.isDirectory())
                return true;
            else
                return delegate.accept(pathname);
        }
    }
}
