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
 * @author  Joshua Davis
 * @version 1.0
 */
public class SimpleQueue
{
    /** The representation of the queue. **/
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
    public Object get() throws InterruptedException
    {
        synchronized(this)
        {
            if (list.size() == 0)               // If the queue is empty, wait.
                wait();
            return list.removeFirst();          // Return the first element.
        }
    }

    /** Get the first object in the queue.  Blocks the thread that calls it until
     * there is an object in the queue.
     * @param waitTime - The maximum number of milliseconds to wait.  If the wait time is zero
     * the method will return instantly with null, if there are no objects in the queue.
     * @throws InterruptedException This is thrown when the waiter is interrupted.
     * @return The first object in the queue.
     */
    public Object get(long waitTime) throws InterruptedException
    {
        synchronized(this)
        {
            if (waitTime > 0)                   // If waiting is required, check the queue.
            {
                if (list.size() == 0)           // If the queue is empty, wait.
                    wait(waitTime);
            }
            if (list.size() == 0)               // If the queue is *still* empty, then return null!
                return null;
            else
                return list.removeFirst();      // Otherwise, return the first object in the queue
        }
    }
    
    /** Add a new object to the end of the queue.  If there are any theads waiting on
     * the 'get()' method, this call will unblock the first one.
     * @param o The object to add to the queue.
     */
    public void add(Object o)
    {     
        synchronized(this)
        {
            list.addLast(o);
            notify();                           // Unblock the first waiter!
        }
    }
    
    /** Returns the number of objects currently in the queue.
     * @return The number of objects currently in the queue.
     */
    public int size()
    {
        synchronized(this)
        {
            return list.size();
        }
    }
}









