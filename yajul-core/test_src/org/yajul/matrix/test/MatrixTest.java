// $Id$

package org.yajul.matrix.test;

import junit.framework.TestCase;
import org.yajul.matrix.ArrayListMatrix;
import org.yajul.matrix.ArrayMatrix;
import org.yajul.matrix.Matrix;
import org.yajul.matrix.MatrixUtil;

import java.util.Arrays;

/**
 * Tests the ArrayListMatrix class.
 */
public class MatrixTest extends TestCase
{
    private Class matrixClass = ArrayListMatrix.class;

    public MatrixTest(String name)
    {
        super(name);
    }

    public void testAddressCalculations()
    {
        int[] sizes = new int[]{3, 7, 31};
        int[] factors = MatrixUtil.calculateFactors(sizes);
        assertEquals(4, factors.length);
        int elements = 3 * 7 * 31;
        assertEquals(1, factors[0]);
        assertEquals(elements, factors[3]);
        assertEquals(elements - 1, MatrixUtil.coordsToAddress(new int[]{2, 6, 30}, factors));
        assertEquals(0, MatrixUtil.coordsToAddress(new int[]{0, 0, 0}, factors));
        assertEquals(1, MatrixUtil.coordsToAddress(new int[]{1, 0, 0}, factors));
        assertEquals(3, MatrixUtil.coordsToAddress(new int[]{0, 1, 0}, factors));
        assertEquals(21, MatrixUtil.coordsToAddress(new int[]{0, 0, 1}, factors));
        assertEquals(42, MatrixUtil.coordsToAddress(new int[]{0, 0, 2}, factors));

        int[] coords = MatrixUtil.addressToCoords(42, factors);
        assertTrue("Unexpected:" + MatrixUtil.coordsToString(coords), Arrays.equals(new int[]{0, 0, 2}, coords));
        coords = MatrixUtil.addressToCoords(21, factors);
        assertTrue("Unexpected:" + MatrixUtil.coordsToString(coords), Arrays.equals(new int[]{0, 0, 1}, coords));
        coords = MatrixUtil.addressToCoords(3, factors);
        assertTrue("Unexpected:" + MatrixUtil.coordsToString(coords), Arrays.equals(new int[]{0, 1, 0}, coords));
        coords = MatrixUtil.addressToCoords(1, factors);
        assertTrue("Unexpected:" + MatrixUtil.coordsToString(coords), Arrays.equals(new int[]{1, 0, 0}, coords));
    }

    public void testIncrement() throws Exception
    {
        matrixClass = ArrayMatrix.class;
        Matrix m = createMatrix();
        fill2d1(m);
        int iterations = countIterations(m);
        assertEquals(20 * 10, iterations);
        assertEquals(20 * 10, m.getTotalSize());
        m = createMatrix();
        fill1d(m);
        iterations = countIterations(m);
        assertEquals(20, iterations);
        assertEquals(20, m.getTotalSize());
    }

    private int countIterations(Matrix m)
    {
        int[] coord = new int[m.getDimensions()];
        int[] sizes = m.getSizes();
        int iterations = 0;
        do
        {
            iterations++;
        }
        while (MatrixUtil.increment(coord, sizes));
        return iterations;
    }

    /**
     * Test basic put/get.
     */
    public void test2dArrayListMatrix() throws Exception
    {
        matrixClass = ArrayListMatrix.class;
        doTest2d();
    }

    /**
     * Test basic put/get.
     */
    public void test2dArrayMatrix() throws Exception
    {
        matrixClass = ArrayMatrix.class;
        doTest2d();
    }

    private void doTest2d() throws IllegalAccessException, InstantiationException
    {
        Matrix m = createMatrix();
        int[] coords = new int[2];
        fill2d1(m);
        check2d(m, coords);
        m = new ArrayListMatrix();
        fill2d2(m);
        check2d(m, coords);
        m.setCanGrow(false);
        fill2d2(m);
        checkResize2d(m);
    }

    private Matrix createMatrix() throws IllegalAccessException, InstantiationException
    {
        return (Matrix) matrixClass.newInstance();
    }

    private void checkResize2d(Matrix m)
    {
        assertFalse(m.canGrow());
        Exception x = null;
        try
        {
            m.get(-1);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            x = e;
        }
        assertNotNull(x);

        x = null;
        try
        {
            m.get(245);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            x = e;
        }
        assertNotNull(x);

        x = null;
        try
        {
            m.get(100, 100);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            x = e;
        }
        assertNotNull(x);

        x = null;
        try
        {
            m.get(100, 100, 100);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            x = e;
        }
        assertNotNull(x);

        x = null;
        try
        {
            m.getSize(3);
        }
        catch (IllegalArgumentException e)
        {
            x = e;
        }
        assertNotNull(x);

        m.setSize(new int[]{200, 200, 200});
        assertNull(m.get(100, 100, 100));

        m.put(199, 199, 199, "hello");
        assertEquals("hello", m.get(199, 199, 199));
    }

    private void fill2d2(Matrix m)
    {
        for (int i = 19; i >= 0; i--)
        {
            for (int j = 9; j >= 0; j--)
            {
                m.put(i, j, new Integer(i * 10 + j));
            }
        }
    }

    private void fill2d1(Matrix m)
    {
        assertTrue(m.canGrow());
        int[] coords = new int[2];
        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                coords[0] = i;
                coords[1] = j;
                m.put(coords, new Integer(i * 10 + j));
            }
        }
    }

    public void test1dArrayListMatrix() throws Exception
    {
        matrixClass = ArrayListMatrix.class;
        doTest1d();
    }

    public void test1dArrayMatrix() throws Exception
    {
        matrixClass = ArrayMatrix.class;
        doTest1d();
    }

    private void doTest1d() throws IllegalAccessException, InstantiationException
    {
        // Note: this test covers the methods that test2d does not.
        Matrix m = createMatrix();
        assertTrue(m.canGrow());
        fill1d(m);
        for (int i = 0; i < 20; i++)
        {
            Integer v = (Integer) m.get(i);
            int val = i * 10;
            assertEquals(val, v.intValue());
        }

        // Mutate into 2d by putting in a new value.
        m.put(0, 3, "hello");
    }

    private void fill1d(Matrix m)
    {
        for (int i = 0; i < 20; i++)
        {
            m.put(i, new Integer(i * 10));
        }
    }

    public void test3dArrayListMatrix() throws Exception
    {
        matrixClass = ArrayListMatrix.class;
        doTest3d();
    }

    public void test3dArrayMatrix() throws Exception
    {
        matrixClass = ArrayListMatrix.class;
        doTest3d();
    }

    private void doTest3d() throws IllegalAccessException, InstantiationException
    {
        // Note: this test covers the methods that test2d does not.
        Matrix m = createMatrix();
        assertTrue(m.canGrow());
        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    int value = i * 50 + j * 5 + k;
                    m.put(i, j, k, new Integer(value));
                }
            }
        }

        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    Integer v = (Integer) m.get(i, j, k);
                    int value = i * 50 + j * 5 + k;
                    assertEquals(value, v.intValue());
                }
            }
        }
    }

    private void check2d(Matrix m, int[] coords)
    {
        assertEquals(2, m.getDimensions());
        assertEquals(20, m.getSize(0));
        assertEquals(10, m.getSize(1));
        for (int i = 0; i < 20; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                coords[0] = i;
                coords[1] = j;
                Integer v = (Integer) m.get(coords);
                int val = i * 10 + j;
                assertEquals(val, v.intValue());
                v = (Integer) m.get(i, j);
                assertEquals(val, v.intValue());
            }
        }
    }
}
