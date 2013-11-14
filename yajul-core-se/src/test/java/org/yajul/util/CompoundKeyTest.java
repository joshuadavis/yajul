package org.yajul.util;

import org.junit.Test;
import org.yajul.serialization.SerializationHelper;

import static org.junit.Assert.*;


import java.util.Date;

/**
 * Tests CompoundKey.
 * <hr>
 * User: jdavis<br>
 * Date: May 27, 2004<br>
 * Time: 8:18:48 PM<br>
 *
 * @author jdavis
 */
public class CompoundKeyTest {

    @Test
    public void testEquality() {
        CompoundKey a = new CompoundKey("foo", 33, new Date(0));
        CompoundKey b = new CompoundKey("foo", 33, new Date(0));
        checkEquality(a, b);
    }

    @Test
    public void testArrayConstructor() {
        Object[] array = new Object[] {"foo", 33, new Date(0) };
        CompoundKey a = new CompoundKey(array);
        CompoundKey b = new CompoundKey(array);
        checkEquality(a,b);
    }

    @Test
    public void testSerialization() throws Exception {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = SerializationHelper.serialClone(a);
        checkEquality(a,b);
    }

    private void checkEquality(CompoundKey a, CompoundKey b) {
        assertNotSame(a,b);
        assertEquals("foo", a.getComponent(0));
        assertEquals(33, a.getComponent(1));
        assertEquals(new Date(0), a.getComponent(2));

        assertEquals("foo", b.getComponent(0));
        assertEquals(b.getComponent(1), 33);
        assertEquals(new Date(0), b.getComponent(2));

        assertEquals(3, a.size());
        assertEquals(3, b.size());

        String s = a.toString();
        assertNotNull(s);
        assertTrue(s.length() > 0);

        assertCompoundKeyEquals(a, b);
    }

    @Test
    public void testEquality2() {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0), new Double(0.0));
        CompoundKey b = new CompoundKey("foo", new Integer(33), new Date(0), new Double(0.0));

        assertEquals("foo", a.getComponent(0));
        assertEquals(new Integer(33), a.getComponent(1));
        assertEquals(new Date(0), a.getComponent(2));

        assertEquals("foo", b.getComponent(0));
        assertEquals(new Integer(33), b.getComponent(1));
        assertEquals(new Date(0), b.getComponent(2));

        assertEquals(4, a.size());
        assertEquals(4, b.size());

        assertCompoundKeyEquals(a, b);
    }

    private void assertCompoundKeyEquals(CompoundKey a, CompoundKey b) {
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(0, a.compareTo(b));
        assertEquals(0, b.compareTo(a));
    }

    @Test
    public void testInequality() {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = new CompoundKey("foob", new Integer(33), new Date(0));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    @Test
    public void testInequality2() {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = new CompoundKey("foo", new Integer(34), new Date(0));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    @Test
    public void testInequality3() {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = new CompoundKey("foo", new Integer(33), new Date(12));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }

    @Test
    public void testDifferingLengths() {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = new CompoundKey("foo", new Integer(33));
        assertTrue(!a.equals(b));
        assertTrue(!b.equals(a));
        assertTrue(a.compareTo(b) != 0);
        assertTrue(b.compareTo(a) != 0);
    }


    @Test
    public void testClone() throws Exception {
        CompoundKey a = new CompoundKey("foo", new Integer(33), new Date(0));
        CompoundKey b = (CompoundKey) a.clone();
        assertCompoundKeyEquals(a, b);
        assertTrue(a != b); // These should be different objects!
    }

    @Test
    public void testExceptions() throws Exception {
        IllegalArgumentException iae = null;
        try {
            new CompoundKey(new Object[]{"foo", new Integer(33), new Date(0), null});
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);

        iae = null;
        try {
            new CompoundKey(null);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);

        iae = null;
        try {
            new CompoundKey();
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);

        iae = null;
        try {
            new CompoundKey(new Object[0]);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        assertNotNull(iae);

    }
}
