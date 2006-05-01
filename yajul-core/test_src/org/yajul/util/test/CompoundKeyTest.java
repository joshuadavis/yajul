package org.yajul.util.test;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.yajul.util.CompoundKey;

/**
 * Tests CompoundKey.
 * <hr>
 * User: jdavis<br>
 * Date: May 27, 2004<br>
 * Time: 8:18:48 PM<br>
 * @author jdavis
 */
public class CompoundKeyTest extends TestCase
{
    public CompoundKeyTest(String name)
    {
        super(name);
    }

    /**
     * Performs any set up that is required by the test,
     * such as initializing instance variables of the test
     * case class, etc. Invoked before every test method.
     */
    protected void setUp()
    {
    }

    /**
     * Cleans up any state that needs to be undone after
     * the test has completed.
     */
    protected void tearDown()
    {
    }

    public void testEquality()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = new CompoundKey("foo",new Integer(33),new Date(0));

        assertEquals("foo",             a.getComponent(0));
        assertEquals(new Integer(33),   a.getComponent(1));
        assertEquals(new Date(0),       a.getComponent(2));

        assertEquals("foo",             b.getComponent(0));
        assertEquals(new Integer(33),   b.getComponent(1));
        assertEquals(new Date(0),       b.getComponent(2));

        assertEquals(3,a.size());
        assertEquals(3,b.size());

        String s = a.toString();
        assertNotNull(s);
        assertTrue(s.length() > 0);

        assertCompoundKeyEquals(a, b);
        assertEquals(a.hashCode(),b.hashCode());

        a.setComponents(new Object[] { "bar",new Integer(55),new Date(1) });
        b.setComponents(new Object[] { "bar",new Integer(55),new Date(1) });
        assertEquals(a.hashCode(),b.hashCode());
        assertEquals(a.hashCode(),b.hashCode());
        assertCompoundKeyEquals(a, b);
    }

    public void testEquality2()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0),new Double(0.0));
        CompoundKey b = new CompoundKey("foo",new Integer(33),new Date(0),new Double(0.0));

        assertEquals("foo",             a.getComponent(0));
        assertEquals(new Integer(33),   a.getComponent(1));
        assertEquals(new Date(0),       a.getComponent(2));

        assertEquals("foo",             b.getComponent(0));
        assertEquals(new Integer(33),   b.getComponent(1));
        assertEquals(new Date(0),       b.getComponent(2));

        assertEquals(4,a.size());
        assertEquals(4,b.size());

        assertCompoundKeyEquals(a, b);
    }

    private void assertCompoundKeyEquals(CompoundKey a, CompoundKey b)
    {
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(),b.hashCode());
        assertEquals(0,a.compareTo(b));
        assertEquals(0,b.compareTo(a));
    }

    public void testInequality()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = new CompoundKey("foob",new Integer(33),new Date(0));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    public void testInequality2()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = new CompoundKey("foo",new Integer(34),new Date(0));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    public void testInequality3()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = new CompoundKey("foo",new Integer(33),new Date(12));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    public void testDifferingLengths()
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = new CompoundKey("foo",new Integer(33));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }


    public void testClone() throws Exception
    {
        CompoundKey a = new CompoundKey("foo",new Integer(33),new Date(0));
        CompoundKey b = (CompoundKey)a.clone();
        assertCompoundKeyEquals(a,b);
        assertTrue(a != b); // These should be different objects!
    }

    public void testExceptions() throws Exception
    {
        IllegalArgumentException iae = null;
        try
        {
            new CompoundKey(new Object[] { "foo",new Integer(33),new Date(0) , null });
        }
        catch (IllegalArgumentException e)
        {
            iae = e;
        }
        assertNotNull(iae);
    }

    /**
     * Constructs a test suite for this test case, providing any required
     * Setup wrappers, or decorators as well.
     * @return Test - The test suite.
     */
    public static Test suite()
    {
        // Return the default test suite: No setup, all public methods with
        // no return value, no parameters, and names that begin with 'test'
        // are added to the suite.
        return new TestSuite(CompoundKeyTest.class);
    }
}
