package org.yajul.util;

import junit.framework.TestCase;

import java.util.List;
import java.util.Properties;

/**
 * Test case for PropetiesHelper
 * <br>
 * User: Josh
 * Date: Nov 14, 2009
 * Time: 7:42:40 AM
 */
public class PropertiesHelperTest extends TestCase {

    public void testAccessors() {
        Properties properties = new Properties();
        properties.put("should.be.true", "true");
        assertTrue(PropertiesHelper.getBoolean(properties, "should.be.true"));
        assertTrue(PropertiesHelper.getBoolean(properties, "does.not.exist", true));
        assertFalse(PropertiesHelper.getBoolean(properties, "does.not.exist", false));
        properties.put("an.integer", "1234");
        assertEquals(1234, PropertiesHelper.getInt(properties, "an.integer", -1));
        assertEquals(999, PropertiesHelper.getInt(properties, "does.not.exist", 999));
        assertEquals(1234L, PropertiesHelper.getLong(properties, "an.integer", -1L));
        assertEquals(999L, PropertiesHelper.getLong(properties, "does.not.exist", 999L));
        assertEquals(new Integer(1234), PropertiesHelper.getInteger(properties, "an.integer"));
        assertNull(PropertiesHelper.getInteger(properties, "does.not.exist"));
        properties.put("a.double", "1234.56");
        assertEquals(1234.56, PropertiesHelper.getDouble(properties, "a.double", -1));
        assertEquals(9.99, PropertiesHelper.getDouble(properties, "does.not.exist", 9.99));
    }

    public void testBadIntegerProperty() {
        Properties properties = new Properties();
        properties.put("bad.integer", "12bad34");
        Exception e = null;
        try {
            PropertiesHelper.getInt(properties, "bad.integer", -1);
        } catch (NumberFormatException nfe) {
            e = nfe;
        }
        assertNotNull("Expected number format exception!", e);
    }

    public void testBadBooleanProperty() {
        Properties properties = new Properties();
        properties.put("bad.bool", " true");
        Exception e = null;
        try {
            PropertiesHelper.getBoolean(properties, "bad.bool", false, PropertiesHelper.BooleanParse.STRICT);
        } catch (IllegalArgumentException iae) {
            e = iae;
        }
        assertNotNull("Expected illegal argument exception!", e);
    }

    public void testLoaders() {
        Properties props = PropertiesHelper.loadFromResource("test-properties.properties", null, this.getClass());
        assertNotNull(props.get("this"));
        assertNotNull(props.get("that"));
    }

    public void testNameList() {
        Properties props = new Properties();
        props.put("one", "abc");
        props.put("two", "def");
        props.put("a.three", "ghi");
        List<String> names = PropertiesHelper.getNameList(props);
        assertEquals(3, names.size());
        assertTrue(names.contains("one"));
        assertTrue(names.contains("two"));
        assertTrue(names.contains("a.three"));
    }

    public void testInterpolator() {
        Properties p = new Properties();
        p.put("domain", "one.foo.com");
        p.put("url", "http://${domain}:${portnumber}${path}");

        String url = PropertiesHelper.interpolate(p.getProperty("url"), p);
        assertEquals(url, "http://one.foo.com:${portnumber}${path}");

        p.put("path", "/bar/baz");
        String url2 = PropertiesHelper.interpolate(p.getProperty("url"), p);
        assertEquals(url2, "http://one.foo.com:${portnumber}/bar/baz");
    }

    public void testInterpolateAll() {
        Properties p = new Properties();
        p.setProperty("path", "/bar/baz");
        p.setProperty("domain", "one.foo.com");
        p.setProperty("url", "http://${domain}:${portnumber}${path}");
        p.setProperty("portnumber", "8080");
        Properties interpolated = PropertiesHelper.interpolateAll(p);
        assertEquals(interpolated.getProperty("url"), "http://one.foo.com:8080/bar/baz");
    }
}
