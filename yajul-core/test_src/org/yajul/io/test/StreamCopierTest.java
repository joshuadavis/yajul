
package org.yajul.io.test;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.Arrays;

import org.yajul.io.StreamCopier;
import org.yajul.io.EchoInputStream;
import org.yajul.io.TeeOutputStream;
import org.yajul.io.ByteCountingInputStream;
import org.yajul.io.ByteCountingOutputStream;
import org.yajul.io.HexDumpOutputStream;
import org.yajul.util.Copier;

/**
 * Tests org.yajul.io classes:
 * <ul>
 * <li>StreamCopier</li>
 * <li>EchoInputStream</li>
 * <li>TeeOutputStream</li>
 * <li>ByteCountingInputStream</li>
 * <li>ByteCountingOutputStream</li>
 * </ul>
 * User: josh
 * Date: Sep 22, 2002
 * Time: 12:38:27 AM
 */
public class StreamCopierTest extends TestCase
{
    private static final byte[] BYTES = "12345678901234567890".getBytes();
    private static final char[] CHARS = "12345678901234567890".toCharArray();

    public StreamCopierTest(String name)
    {
        super(name);
    }

    public void testStreamCopy()
    {
        byte[] a = BYTES;
        InputStream in = new ByteArrayInputStream(a);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamCopier copier = new StreamCopier(in,out);
        copier.run();
        Arrays.equals(a,out.toByteArray());
    }

    public void testEchoInputStream() throws IOException
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream echo = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream in = new EchoInputStream(input,echo);
        StreamCopier.unsyncCopy(in,output,8);
        assertTrue(Arrays.equals(bytes,output.toByteArray()));
        assertTrue(Arrays.equals(bytes,echo.toByteArray()));
    }

   public void test2OutputStreams() throws IOException
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream echo = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStream out = new TeeOutputStream(output,echo);
        StreamCopier.unsyncCopy(input,out,8);
        byte[] outputbytes = output.toByteArray();
        assertTrue(Arrays.equals(bytes,outputbytes));
        byte[] echobytes = echo.toByteArray();
        assertTrue(Arrays.equals(bytes,echobytes));
    }

    public void test3OutputStreams() throws IOException
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream[] streams = new ByteArrayOutputStream[3];
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new ByteArrayOutputStream();
        }

        OutputStream out = new TeeOutputStream(streams);
        StreamCopier.unsyncCopy(input,out,8);

        for (int i = 0; i < streams.length; i++)
        {
            ByteArrayOutputStream stream = streams[i];
            byte[] outputbytes = stream.toByteArray();
            assertTrue(Arrays.equals(bytes,outputbytes));
        }
    }

    public void testByteCountingInputStream() throws IOException
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteCountingInputStream in = new ByteCountingInputStream(input);
        StreamCopier.unsyncCopy(in,output,8);
        assertTrue(Arrays.equals(bytes,output.toByteArray()));
        assertEquals(bytes.length,in.getByteCount());
    }

    public void testByteCountingOutputStream() throws IOException
    {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteCountingOutputStream out = new ByteCountingOutputStream(output);
        StreamCopier.unsyncCopy(input,out,8);
        assertTrue(Arrays.equals(bytes,output.toByteArray()));
        assertEquals(bytes.length,out.getByteCount());
    }

    public void testHexDumpOutputStream() throws IOException
    {
        byte[] bytes = new byte[50];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)i;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HexDumpOutputStream out = new HexDumpOutputStream(output,16);
        StreamCopier.unsyncCopy(input,out,8);
        out.flush();
        // TODO: Test the result.
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
        ByteCountingOutputStream out = new ByteCountingOutputStream(output);
        Copier.copy(input,out,8,sz);
        byte[] result = new byte[sz];
        System.arraycopy(bytes,0,result,0,sz);
        assertEquals(sz,out.getByteCount());
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
