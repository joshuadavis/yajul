package org.yajul.util.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.yajul.util.ArrayUtil;

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

    public void testTodoubleArray()
    {
        assertNull(ArrayUtil.todoubleArray(null));
        Double[] input = new Double[] {new Double(10), new Double(14.98), new Double(98.76), new Double(0), new Double(-8.5)};
        double[] expected = new double[] {10, 14.98, 98.76, 0, -8.5};
        double[] actual = ArrayUtil.todoubleArray(input);
        assertArraysEqual(expected, actual);
    }

    public void testToDoubleArray()
    {
        assertNull(ArrayUtil.toDoubleArray(null));
        double[] input = new double[] {10, 30, 12, 12.4, 14};
        Double[] expected = new Double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            expected[i] = new Double(input[i]);
        }
        Double[] actual = ArrayUtil.toDoubleArray(input);
        assertArraysEqual(expected, actual);
    }

    private void assertArraysEqual(Double[] expected, Double[] actual)
    {
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.length == actual.length);
        for (int i = 0; i < actual.length; i++)
        {
            assertEquals("i=" + i, expected[i].doubleValue(), actual[i].doubleValue(), 0.0000001);
        }
    }

    public void testAreContentsEqual()
    {
        assertTrue(ArrayUtil.areContentsEqual(null, null));

        double[] a1 = new double[] {10, 30, 12, 12.4, 14}; // input 1
        double[] a2 = new double[] {14, 32, 54.5, -19, 0.5}; // input 2
        assertFalse(ArrayUtil.areContentsEqual(a1, a2));

        a2 = new double[] {10, 30, 12, 12.4, 14};
        assertTrue(ArrayUtil.areContentsEqual(a1, a2));
        assertFalse(ArrayUtil.areContentsEqual(a1, null));
        assertFalse(ArrayUtil.areContentsEqual(null, a2));

        a1 = new double[] {10, 30, 12, 12.4, 14};
        a2 = new double[] {10, 30, 12};
        assertFalse(ArrayUtil.areContentsEqual(a1, a2));

        a1 = new double[] {10, 30, 12};
        a2 = new double[] {10, 30, 12, 12.4, 14};
        assertFalse(ArrayUtil.areContentsEqual(a1, a2));
    }

    public void testAppendToArray2()
    {
        assertNull(ArrayUtil.appendToArrayD(null, null));
        Double[] a1 = new Double[] {new Double(10), new Double(14.98), new Double(98.76), new Double(0), new Double(-8.5)};
        double[] a2 = new double[] {14, 32, 54.5, -19, 0.5}; // input 2
        double[] re = new double[] {24, 46.98, (54.5+98.76), -19, -8}; // result expected
        assertArraysEqual(re, ArrayUtil.appendToArrayD(a2, a1));
        assertArraysEqual(re, a2);

        // fail on null!
        a1 = new Double[] {new Double(10), new Double(14.98), null, new Double(0), new Double(-8.5)};
        a2 = new double[] {14, 32, 54.5, -19, 0.5}; // input 2
        re = new double[] {24, 46.98, 54.5, -19, -8}; // result expected
        assertArraysEqual(re, ArrayUtil.appendToArrayD(a2, a1));
        assertArraysEqual(re, a2);
    }

    public void testAppendToArray()
    {
        assertNull(ArrayUtil.appendToArray(null, null));

        double[] a1 = new double[] {10, 30, 12, 12.4, 14}; // input 1
        double[] a2 = new double[] {14, 32, 54.5, -19, 0.5}; // input 2
        double[] re = new double[] {24, 62, 66.5, -6.6, 14.5}; // result expected
        assertArraysEqual(re, ArrayUtil.appendToArray(a1, a2));
        assertArraysEqual(re, a1);
        assertArraysEqual(a1, ArrayUtil.appendToArray(a1, null));
        assertArraysEqual(a2, ArrayUtil.appendToArray(null, a2));

        a1 = new double[] {10, 30, 11}; // input 1
        a2 = new double[] {14, 32, 51.5, -19, 0.5}; // input 2
        re = new double[] {24, 62, 62.5, -19, 0.5}; // result expected
        assertArraysEqual(re, ArrayUtil.appendToArray(a1, a2));

        a1 = new double[] {10, 30, 11, 12.4, 14}; // input 1
        a2 = new double[] {14, 32, 51.5}; // input 2
        re = new double[] {24, 62, 62.5, 12.4, 14}; // result expected
        assertArraysEqual(re, ArrayUtil.appendToArray(a1, a2));
        assertArraysEqual(re, a1);
    }

    public void testAddArrays()
    {
        assertNull(ArrayUtil.addArrays(null, null));

        double[] a1 = new double[] {10, 30, 12, 12.4, 14}; // input 1
        double[] a2 = new double[] {14, 32, 54.5, -19, 0.5}; // input 2
        double[] re = new double[] {24, 62, 66.5, -6.6, 14.5}; // result expected
        assertArraysEqual(re, ArrayUtil.addArrays(a1, a2));
        assertArraysEqual(a1, ArrayUtil.addArrays(a1, null));
        assertArraysEqual(a2, ArrayUtil.addArrays(null, a2));

        a1 = new double[] {10, 30, 11}; // input 1
        a2 = new double[] {14, 32, 51.5, -19, 0.5}; // input 2
        re = new double[] {24, 62, 62.5, -19, 0.5}; // result expected
        assertArraysEqual(re, ArrayUtil.addArrays(a1, a2));

        a1 = new double[] {10, 30, 11, 12.4, 14}; // input 1
        a2 = new double[] {14, 32, 51.5}; // input 2
        re = new double[] {24, 62, 62.5, 12.4, 14}; // result expected
        assertArraysEqual(re, ArrayUtil.addArrays(a1, a2));
    }

    private void assertArraysEqual(double[] expected, double[] actual)
    {
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.length == actual.length);
        for (int i = 0; i < actual.length; i++)
        {
            assertEquals("i=" + i, expected[i], actual[i], 0.0000001);
        }
    }
}
