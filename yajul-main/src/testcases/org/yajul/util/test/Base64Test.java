/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 15, 2002
 * Time: 2:21:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util.test;

import junit.framework.TestCase;

import org.yajul.util.Base64Decoder;
import org.yajul.util.Base64FormatException;
import org.yajul.util.Base64Encoder;

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
