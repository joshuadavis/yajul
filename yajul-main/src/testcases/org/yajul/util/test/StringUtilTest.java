/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 21, 2002
 * Time: 11:55:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util.test;

import junit.framework.TestCase;

import org.yajul.util.StringUtil;

public class StringUtilTest  extends TestCase
{
    public StringUtilTest(String name)
    {
        super(name);
    }

    public void testDefaultToString()
    {
        Object o = new Object();
        String s = StringUtil.defaultToString(o);
        assertEquals(o.toString(),s);
    }

    public void testEquals()
    {
        String a = "foo-bar";
        String b = "foo-bar";
        assertTrue(StringUtil.equals(a,b));
        assertTrue(StringUtil.equals(null,null));
        assertTrue(!StringUtil.equals(a,null));
        assertTrue(!StringUtil.equals(null,b));
        assertTrue(!StringUtil.equals(a,"whaaaat?"));
    }

    public void testIsEmpty()
    {
        String a = "foo-bar";
        assertTrue(!StringUtil.isEmpty(a));
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        String[] aa = new String[] { "one", "two", "three" };
        String[] bb = new String[] { "one", "two", "three" };
        assertTrue(StringUtil.arrayEquals(aa,bb));
        assertTrue(!StringUtil.arrayEquals(aa,null));
        assertTrue(!StringUtil.arrayEquals(null,bb));
        String[] cc = new String[] { "one", "two", "threee" };
        assertTrue(!StringUtil.arrayEquals(aa,cc));
        String[] dd = new String[] { "one", "two" };
        assertTrue(!StringUtil.arrayEquals(aa,dd));
    }

    public void testSplitAndJoin()
    {
        String[] a = new String[] { "one", "two", "three" };
        String b = StringUtil.join(a,"|");
        assertEquals("one|two|three",b);
        String[] c = StringUtil.split(b,"|");
        assertTrue(StringUtil.arrayEquals(a,c));
    }

    public void testPadLeft()
    {
        String a = "123";
        assertEquals("123 ",StringUtil.padLeft(a,4,false));
        assertEquals("123",StringUtil.padLeft(a,3,false));
        assertEquals("123",StringUtil.padLeft(a,2,false));
        assertEquals("12",StringUtil.padLeft(a,2,true));

        assertEquals("123x",StringUtil.padLeft(a, 4, false,"xyznnnn"));
        assertEquals("123xy",StringUtil.padLeft(a, 5, false,"xyznnnn"));
        assertEquals("123xyz",StringUtil.padLeft(a, 6, false,"xyznnnn"));
        assertEquals("123xyzn",StringUtil.padLeft(a, 7, false,"xyznnnn"));
        assertEquals("123xyzx",StringUtil.padLeft(a, 7, false,"xyz"));
        assertEquals("123xyzxyz",StringUtil.padLeft(a, 9, false,"xyz"));
    }

    public void testPadRight()
    {
        String a = "123";
        assertEquals(" 123",StringUtil.padRight(a,4,false));
        assertEquals("123",StringUtil.padRight(a,3,false));
        assertEquals("123",StringUtil.padRight(a,2,false));
        assertEquals("12",StringUtil.padRight(a,2,true));

        assertEquals("x123",StringUtil.padRight(a, 4, false,"xyznnnn"));
        assertEquals("xy123",StringUtil.padRight(a, 5, false,"xyznnnn"));
        assertEquals("xyz123",StringUtil.padRight(a, 6, false,"xyznnnn"));
        assertEquals("xyzn123",StringUtil.padRight(a, 7, false,"xyznnnn"));
        assertEquals("xyzx123",StringUtil.padRight(a, 7, false,"xyz"));
        assertEquals("xyzxyz123",StringUtil.padRight(a, 9, false,"xyz"));
    }

    public void testReplace()
    {
        String a = "xxxabcyyy";
        String b = "abc";
        String c = "def";
        assertEquals("xxxdefyyy",StringUtil.replace(a,b,c));
        assertEquals("xxxyyy",StringUtil.replace(a,b,""));
    }

    public void testHexString()
    {
        byte[] bytes = new byte[] { 0, 10, 16, 63, 127,
                                    (byte)128, (byte)129,
                                    (byte)254, (byte)255,
                                    (byte)256 };

        String hex = StringUtil.hexString(bytes,",");
        assertEquals("00,0a,10,3f,7f,80,81,fe,ff,00",hex);
    }
}

