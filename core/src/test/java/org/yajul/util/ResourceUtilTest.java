// $Id$

package org.yajul.util;

import java.util.Properties;

import junit.framework.TestCase;

import org.yajul.util.ResourceUtil;

/**
 * Tests ResourceUtil
 */
public class ResourceUtilTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public ResourceUtilTest(String name)
    {
        super(name);
    }

    public void testLoadProperties() throws Exception
    {
        Properties props = ResourceUtil.loadProperties("org/yajul/util/test-properties.properties");
        assertEquals("one",props.getProperty("this"));
        assertEquals("two",props.getProperty("that"));
        assertEquals(2,props.size());

        props = ResourceUtil.loadProperties("this-does-not-exist.properties");
        assertNull(props);

        props = ResourceUtil.loadProperties("test-package-properties.properties",null,this.getClass());
        assertNotNull(props);

        Properties defaults = ResourceUtil.loadProperties("org/yajul/util/test-properties.properties");
        props = ResourceUtil.loadProperties("test-package-properties.properties",defaults,this.getClass());
        assertEquals("one",props.getProperty("this"));
        assertEquals("overridden",props.getProperty("that"));
        assertEquals("bar",props.getProperty("foo"));
        // Note: Even though there are three properties total, one is a default so only two
        // are considered to be defined in 'props'.
        assertEquals(2,props.size());

    }

    public void testExists() throws Exception
    {
        assertTrue(ResourceUtil.exists("org/yajul/util/test-properties.properties"));
        assertFalse(ResourceUtil.exists("this-does-not-exist-either.props"));
    }

    public void testGetResource() throws Exception
    {
        byte[] bytes = ResourceUtil.resourceAsBytes("org/yajul/util/test-properties.properties");
        assertNotNull(bytes);
        bytes = ResourceUtil.resourceAsBytes("foo-nothing-here.properties");
        assertNull(bytes);
    }
}
