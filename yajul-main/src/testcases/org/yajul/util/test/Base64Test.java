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
import org.yajul.io.Base64InputStream;
import org.yajul.io.StreamCopier;
import org.yajul.util.Base64Decoder;
import org.yajul.util.Base64Encoder;
import org.yajul.util.Base64FormatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
        assertEquals(plain, decoded);
    }

    public void test128() throws Base64FormatException, IOException
    {
        for (int i = 1; i <= 128; i++)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int j = 0; j < i; j++)
            {
                baos.write(j);
            }
            byte[] inputBytes = baos.toByteArray();
//            System.out.println("input = "+hexdump(inputBytes));
            ByteArrayInputStream in = new ByteArrayInputStream(inputBytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64Encoder enc = new Base64Encoder(in, out);
            enc.process();
            byte[] encodedBytes = out.toByteArray();
//            System.out.println("encoded = "+hexdump(encodedBytes));
//            System.out.println("encoded (string) = "+new String(encodedBytes));
            assertTrue(!Arrays.equals(inputBytes, encodedBytes));
            assertTrue(inputBytes.length <= encodedBytes.length);
            in = new ByteArrayInputStream(encodedBytes);
            out = new ByteArrayOutputStream();
            Base64Decoder dec = new Base64Decoder(in, out);
            dec.process();
            byte[] decodedBytes = out.toByteArray();
//            System.out.println("output = "+hexdump(decodedBytes));
            assertTrue(Arrays.equals(inputBytes,decodedBytes));
        }
    }

    private String hexdump(byte[] bytes)
    {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < bytes.length; i++)
        {
            buf.append(Integer.toHexString(bytes[i]));
        }
        return buf.toString();
    }

    public void testInputStream() throws
            Base64FormatException, IOException
    {
        String plain = "hello there!";
        String encoded = Base64Encoder.encode(plain);
        Base64InputStream in = new Base64InputStream(
                new ByteArrayInputStream(encoded.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamCopier.copy(in, out);
        String decoded = new String(out.toByteArray());
        System.out.println(decoded);
    }
}