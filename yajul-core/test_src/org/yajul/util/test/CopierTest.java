package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.Copier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.Arrays;

/**
 * Test the Copier class
 * <hr>
 * User: jdavis<br>
 * Date: May 28, 2004<br>
 * Time: 3:18:28 PM<br>
 * @author jdavis
 */
public class CopierTest extends TestCase
{
    private static final byte[] BYTES = "12345678901234567890".getBytes();
    private static final char[] CHARS = "12345678901234567890".toCharArray();

    public CopierTest(String name)
    {
        super(name);
    }

    public void testToByteArray() throws Exception
    {
        byte[] bytes = BYTES;
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        byte[] result = Copier.toByteArray(input);
        assertTrue(Arrays.equals(bytes,result));
    }

    public void testReaderCopy() throws Exception
    {
        char[] chars = CHARS;
        CharArrayReader reader = new CharArrayReader(chars);
        CharArrayWriter writer = new CharArrayWriter();
        Copier.copy(reader,writer,8,-1);
        assertTrue(Arrays.equals(chars,writer.toCharArray()));
    }

    public void testLimitCopy() throws Exception
    {
        byte[] bytes = BYTES;
        int sz = bytes.length - 4;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Copier.copy(input,output,8,sz);
        byte[] result = new byte[sz];
        System.arraycopy(bytes,0,result,0,sz);
        assertTrue(Arrays.equals(result,output.toByteArray()));

        char[] chars = CHARS;
        sz = chars.length - 4;
        CharArrayReader reader = new CharArrayReader(chars);
        CharArrayWriter writer = new CharArrayWriter();
        Copier.copy(reader,writer,8,sz);
        char[] resultchars = new char[sz];
        System.arraycopy(chars,0,resultchars,0,sz);
        assertTrue(Arrays.equals(resultchars,writer.toCharArray()));
    }

    public void testNullOutputCopy() throws Exception
    {
        byte[] bytes = BYTES;
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = null;
        Copier.copy(input,output,8,0);

        char[] chars = CHARS;
        CharArrayReader reader = new CharArrayReader(chars);
        CharArrayWriter writer = null;
        Copier.copy(reader,writer,8,0);
    }

    public void testExceptions() throws Exception
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        IllegalArgumentException iae = null;
        try
        {
            Copier.copy(input,output,-1,-1);
        }
        catch (IllegalArgumentException e)
        {
            iae = e;
        }
        assertNotNull(iae);


        char[] chars = CHARS;
        CharArrayReader reader = new CharArrayReader(chars);
        CharArrayWriter writer = new CharArrayWriter();

        iae = null;
        try
        {
            Copier.copy(reader,writer,-1,-1);
        }
        catch (IllegalArgumentException e)
        {
            iae = e;
        }
        assertNotNull(iae);
    }
}
