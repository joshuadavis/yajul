/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 15, 2002
 * Time: 2:21:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util;

import junit.framework.TestCase;

public class Base64Test extends TestCase
{
    public Base64Test(String name)
    {
        super(name);
    }

    public void testString() throws Base64FormatException
    {
        String plain = "hello there!";
        String encoded = Base64Encoder.encode(plain);
        String decoded = Base64Decoder.decode(encoded);
        assertEquals(plain,decoded);
    }

}
