/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.StringUtil;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * TODO: Document this test case here.
 */
public class StringUtilTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public StringUtilTest(String name)
    {
        super(name);
    }

    /**
     * Test StringUtil.hexString()
     */
    public void testHexString()
    {
        byte[] bytes = new byte[] { 0, 10, 16, 63, 127,
                                    (byte)128, (byte)129,
                                    (byte)254, (byte)255,
                                    (byte)256 };

        String hex = StringUtil.hexString(bytes,",");
        assertEquals("00,0a,10,3f,7f,80,81,fe,ff,00",hex);
        hex = StringUtil.hexString(bytes);
        assertEquals("000a103f7f8081feff00",hex);
        byte[] bytes2 = StringUtil.parseHexString(hex);
        assertTrue(Arrays.equals(bytes,bytes2));
        hex = "000A103F7F8081FEFF00";
        assertTrue(Arrays.equals(bytes,StringUtil.parseHexString(hex)));
        hex = "00A103F7F8081FEFF00";
        assertTrue(Arrays.equals(bytes,StringUtil.parseHexString(hex)));
        StringBuffer buf = new StringBuffer();
        StringUtil.hexString(buf,bytes,",",false);
        hex = buf.toString();
        assertEquals("00,0A,10,3F,7F,80,81,FE,FF,00",hex);
    }

    public void testDefaultToString()
    {
        Object o = new Object();
        String s = StringUtil.defaultToString(o);
        assertEquals(o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o)),s);
        s = StringUtil.defaultToString(null);
        assertEquals("null",s);
    }

    public void testEmpty()
    {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("abc"));
        StringBuffer buf = new StringBuffer();
        StringUtil.appendIfNotEmpty("abc",buf);
        StringUtil.appendIfNotEmpty("",buf);
        StringUtil.appendIfNotEmpty(null,buf);
        StringUtil.appendIfNotEmpty("def",buf);
        assertEquals("abcdef",buf.toString());
    }

    public void testGetBytes()
    {
        byte[] bytes = StringUtil.getBytes("hello",null);
        String s = new String(bytes);
        assertEquals("hello",s);
        bytes = StringUtil.getBytes("hello","fubar");
        assertEquals("hello",new String(bytes));
    }

    public void testSubstringBeforeAfter()
    {
        String s = StringUtil.substringBefore("aa/bb","/");
        assertEquals("aa",s);
        s = StringUtil.substringAfter("aa/bb","/");
        assertEquals("bb",s);
        assertEquals("aa/bb",StringUtil.substringBefore("aa/bb","c"));
        assertNull(StringUtil.substringBefore("/aabb","/"));
        assertNull(StringUtil.substringAfter("aa/bb","c"));
    }
}
