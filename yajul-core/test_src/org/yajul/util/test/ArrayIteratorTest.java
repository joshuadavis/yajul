/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2003
 * Time: 10:38:54 PM
 */
package org.yajul.util.test;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
            assertEquals(array[counter],n);
            counter++;
        }
        assertEquals(array.length,counter);

        NoSuchElementException nsee = null;
        try
        {
            iter.next();
        }
        catch (NoSuchElementException e)
        {
            nsee = e;
        }
        assertNotNull(nsee);

    }

    public void testSkipNull()
    {
        oneNullElement(new Object[] { "one", null, "three" });
        oneNullElement(new Object[] { null , "two", "three" });
        oneNullElement(new Object[] { "one", "two", null });

    }

    private void oneNullElement(Object[] array)
    {
        Iterator iter = new ArrayIterator(array,true);

        int counter = 0;
        Object n = null;
        while (iter.hasNext())
        {
            n = iter.next();
//            System.out.println("counter = " + counter + " n = " + n)     ;
            assertNotNull(n);
            counter++;
        }
        assertEquals(array.length - 1,counter);

        NoSuchElementException nsee = null;
        try
        {
            iter.next();
        }
        catch (NoSuchElementException e)
        {
            nsee = e;
        }
        assertNotNull(nsee);
    }

}