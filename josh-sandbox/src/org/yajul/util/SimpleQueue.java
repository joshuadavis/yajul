/*********************************************************************************
 *   $Header$
 *********************************************************************************/
/*
 * SimpleQueue.java
 *
 * Created on January 6, 2001, 10:27 AM
 */

package org.yajul.util;

import java.util.LinkedList;

/**
 * A simple FIFO queue with multi-threading support.  Readers that call
 * the 'get()' method are blocked until a writer adds an object to the queue
 * with the 'add()' method.
 * @author  pgmjsd@yahoo.com
 * @version 1.0
 */
public class SimpleQueue extends Object
{

    private LinkedList  list;
    
    // Since LinkedList is not synchronized, all operations must be synchronized.
    
    /** Creates new SimpleQueue */
    public SimpleQueue()
    {
        list = new LinkedList();
    }
    
    /** Get the first object in the queue.  Blocks the thread that calls it until
     * there is an object in the queue.
     * @throws InterruptedException This is thrown when the waiter is interrupted.
     * @return The first object in the queue.
     */
    public synchronized Object get() throws InterruptedException
    {
        return get(0);              // Get and wait forever.
    }

    /** Get the first object in the queue.  Blocks the thread that calls it until
     * there is an object in the queue.
     * @param waitTime - The maximum amount of time to wait.
     * @throws InterruptedException This is thrown when the waiter is interrupted.
     * @return The first object in the queue.
     * @see java.lang.Object#wait(long)
     */
    public synchronized Object get(long waitTime) throws InterruptedException
    {
        if (list.size() == 0)       // If the queue is empty, wait.
            wait(waitTime);
        return list.removeFirst();
    }

    /** Add a new object to the end of the queue.  If there are any theads waiting on
     * the 'get()' method, this call will unblock the first one.
     * @param o The object to add to the queue.
     */
    public synchronized void add(Object o)
    {        
        list.addLast(o);
        notify();                   // Unblock the first waiter!
    }
    
    /** Returns the number of objects currently in the queue.
     * @return The number of objects currently in the queue.
     */
    public synchronized int size()  { return list.size(); }
}