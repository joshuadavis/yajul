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

    /**
     * Test loading properties resources.
     */
    public void testLoadProperties() throws Exception
    {
        Properties props = ResourceUtil.loadProperties("test-properties.properties");
        assertEquals("one",props.getProperty("this"));
        assertEquals("two",props.getProperty("that"));
        assertEquals(2,props.size());

        props = ResourceUtil.loadProperties("this-does-not-exist.properties");
        assertNull(props);
    }

    public void testExists() throws Exception
    {
        assertTrue(ResourceUtil.exists("test-properties.properties"));
        assertFalse(ResourceUtil.exists("this-does-not-exist-either.props"));
    }

    public void testGetResource() throws Exception
    {
        byte[] bytes = ResourceUtil.resourceAsBytes("test-properties.properties");
        assertNotNull(bytes);
        bytes = ResourceUtil.resourceAsBytes("foo-nothing-here.properties");
        assertNull(bytes);
    }
}
