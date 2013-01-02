package org.yajul.util;

import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Low level ObjectProvider tests.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 12:00 PM
 */
public class ObjectProviderTest extends TestCase {
    public void testInstanceProvider() {
        ObjectProvider<String> p = new InstanceProvider<String>("this is it");
        assertEquals("this is it",p.getObject());
    }

    public void testCachedObjectProvider() {
        final AtomicInteger counter = new AtomicInteger();

        ObjectProvider<String> d = new ObjectProvider<String>() {
            public String getObject() {
                counter.incrementAndGet();
                return "hey";
            }
        };

        assertEquals(0,counter.get());

        ObjectProvider<String> p = new CachedObjectProvider<String>(d);
        final String a = p.getObject();
        assertEquals("hey", a);
        assertEquals(1,counter.get());
        final String b = p.getObject();
        assertSame(a,b);
        assertEquals(1,counter.get());
    }
}
