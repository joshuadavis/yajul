// $Id$
package org.yajul.net;

import junit.framework.TestCase;

import java.net.MalformedURLException;

/**
 * TODO: Add class javadoc
 * 
 * @author josh May 11, 2004 8:41:29 AM
 */
public class URLParserTest extends TestCase
{
    public URLParserTest(String name)
    {
        super(name);
    }

    public void testAbsoluteURLs() throws Exception
    {
        assertEquals("http",URLParser.parseProtocol("http://www.bozonet.net"));
        assertEquals("www.bozonet.net",URLParser.parseHost("http://www.bozonet.net"));
        assertEquals(123,URLParser.parsePort("http://www.bozonet.net:123"));
        assertEquals("/filey/file.html",URLParser.parseFile("http://www.bozonet.net:123/filey/file.html"));
    }

    public void testRelativeURLs() throws Exception
    {
        MalformedURLException ex = null;
        try
        {
            assertNull(URLParser.parseProtocol("./oney/one.html"));
        }
        catch (MalformedURLException e)
        {
            ex = e;
        }
        assertNotNull(ex);
        assertNull(URLParser.parseProtocol("./oney/one.html",false));
        assertNull(URLParser.parseHost("./oney/one.html"));
        assertEquals(URLParser.NO_PORT,URLParser.parsePort("./oney/one.html"));
        assertEquals("./oney/one.html",URLParser.parseFile("./oney/one.html"));
        assertEquals("/oney/one.html",URLParser.parseFile("/oney/one.html"));
    }
}
