/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 15, 2002
 * Time: 2:21:04 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
        TestCase.assertEquals(plain, decoded);
    }

    public void testOneByte()  throws Base64FormatException, IOException
    {
        // Test one byte.
        byte[] inputBytes = new byte[] { 42 };
        byte[] encoded = encode(inputBytes);
        TestCase.assertEquals(4, encoded.length);
        byte[] decoded = decode(encoded);
        TestCase.assertTrue(Arrays.equals(inputBytes, decoded));
    }

    public void testTwoBytes()  throws Base64FormatException, IOException
    {
        // Test one byte.
        byte[] inputBytes = new byte[] { 42, 43 };
        byte[] encoded = encode(inputBytes);
        TestCase.assertEquals(4, encoded.length);
        byte[] decoded = decode(encoded);
        TestCase.assertTrue(Arrays.equals(inputBytes, decoded));
    }

    public void testThreeBytes()  throws Base64FormatException, IOException
    {
        // Test one byte.
        byte[] inputBytes = new byte[] { 42, 43, 44 };
        byte[] encoded = encode(inputBytes);
        TestCase.assertEquals(4, encoded.length);
        byte[] decoded = decode(encoded);
        TestCase.assertTrue(Arrays.equals(inputBytes, decoded));
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
            byte[] encodedBytes = encode(inputBytes);
            TestCase.assertTrue(!Arrays.equals(inputBytes, encodedBytes));
            TestCase.assertTrue(inputBytes.length <= encodedBytes.length);
            byte[] decodedBytes = decode(encodedBytes);
            TestCase.assertTrue(Arrays.equals(inputBytes, decodedBytes));
        }
    }

    private byte[] decode(byte[] encodedBytes) throws IOException, Base64FormatException
    {
        ByteArrayInputStream in;
        ByteArrayOutputStream out;
        in = new ByteArrayInputStream(encodedBytes);
        out = new ByteArrayOutputStream();
        Base64Decoder dec = new Base64Decoder(in, out);
        dec.process();
        byte[] decodedBytes = out.toByteArray();
//        System.out.println("decoded = "+hexdump(decodedBytes));
        return decodedBytes;
    }

    private byte[] encode(byte[] inputBytes) throws IOException
    {
//        System.out.println("input = "+hexdump(inputBytes));
        ByteArrayInputStream in = new ByteArrayInputStream(inputBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Base64Encoder enc = new Base64Encoder(in, out);
        enc.process();
        byte[] encodedBytes = out.toByteArray();
//        System.out.println("encoded = "+hexdump(encodedBytes));
        return encodedBytes;
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