package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.ArrayUtil;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * Tests ArrayUtil
 * <hr>
 * User: jdavis<br>
 * Date: May 28, 2004<br>
 * Time: 2:35:33 PM<br>
 * @author jdavis
 */
public class ArrayUtilTest extends TestCase
{
    public ArrayUtilTest(String name)
    {
        super(name);
    }

    /**
     * Test primitive array -> object array functions.
     */
    public void testArraysAndSets() throws Exception
    {
        int[] ints = new int[] { 1, 3 , 77, 8 };
        Integer[] integers = new Integer[] { new Integer(1), new Integer(3), new Integer(77), new Integer(8) };
        int[] resultInts = ArrayUtil.toIntArray(integers);
        assertTrue(Arrays.equals(ints,resultInts));
        Set intSet = new HashSet();
        ArrayUtil.addToSet(ints,intSet);
        Set integerSet = new HashSet();
        ArrayUtil.addToSet(integers,integerSet);
        assertTrue(intSet.containsAll(integerSet));
        assertTrue(integerSet.containsAll(intSet));
        int[] intsFromSet = ArrayUtil.toIntArray(intSet);
        for (int i = 0; i < intsFromSet.length; i++)
        {
            assertTrue(intSet.contains(new Integer(intsFromSet[i])));
        }
    }

    public void testBlockSize() throws Exception
    {
        assertEquals(0,ArrayUtil.calculateBlockCount(0,100));
        assertEquals(1,ArrayUtil.calculateBlockCount(33,100));
        assertEquals(2,ArrayUtil.calculateBlockCount(101,100));
        assertEquals(2,ArrayUtil.calculateBlockCount(200,100));
        assertEquals(3,ArrayUtil.calculateBlockCount(201,100));
    }

    public void testHashCode() throws Exception
    {
        int hc1 = ArrayUtil.computeHashCode(new int[] { 1, 2, 3 });
        int hc2 = ArrayUtil.computeHashCode(new int[] { 3, 2, 1 });
        assertTrue(hc1 != hc2);
    }
}
