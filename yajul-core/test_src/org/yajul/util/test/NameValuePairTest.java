// $Id$

package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.NameValuePair;

import java.util.Map;
import java.util.HashMap;

/**
 * Tests NameValuePair
 */
public class NameValuePairTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * 
     * @param name The name of the test case.
     */
    public NameValuePairTest(String name)
    {
        super(name);
    }

    /**
     * TODO: Describe this test.
     */
    public void testSomething() throws Exception
    {
        NameValuePair p = new NameValuePair();
        p.setName("foo");
        p.setValue("bar");
        p.toString();   // Just exercise the method, don't validate.
        assertEquals("foo",p.getName());
        assertEquals("bar",p.getValue());
        p = new NameValuePair("bugs","daffy");
        assertEquals("bugs",p.getName());
        assertEquals("daffy",p.getValue());
        Map m = new HashMap();
        m.put("fred","quimby");
        p = new NameValuePair((Map.Entry) m.entrySet().iterator().next());
        assertEquals("fred",p.getName());
        assertEquals("quimby",p.getValue());
        NameValuePair q = (NameValuePair) p.clone();
        assertNotSame(p,q);
        assertEquals(p.getName(),q.getName());
        assertEquals(p.getValue(),q.getValue());
    }
}
