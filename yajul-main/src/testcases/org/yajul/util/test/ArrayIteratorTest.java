/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2003
 * Time: 10:38:54 PM
 */
package org.yajul.util.test;

import junit.framework.TestCase;

import java.util.Iterator;

import org.yajul.util.ArrayIterator;

/**
 * JUnit test case for ArrayIterator
 * @author josh
 */
public class ArrayIteratorTest extends TestCase
{
    /**
     * Creates test case ArrayIteratorTest
     * @param name The name of the test (method).
     */
    public ArrayIteratorTest(String name)
    {
        super(name);
    }

    public void testArrayIterator()
    {
        Object[] array = new Object[] { "one", "two", "three" };
        Iterator iter = new ArrayIterator(array);
        int counter = 0;
        Object n = null;
        while (iter.hasNext())
        {
            n = iter.next();
//            System.out.println("n="+n);
            assertEquals(array[counter],n);
            counter++;
        }
        assertEquals(array.length,counter);
    }
}