/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 22, 2002
 * Time: 12:38:27 AM
 */
package org.yajul.io.test;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.yajul.io.StreamCopier;
import org.yajul.io.EchoInputStream;
import org.yajul.io.TeeOutputStream;
import org.yajul.io.ByteCountingInputStream;
import org.yajul.io.ByteCountingOutputStream;

/**
 * Tests org.yajul.io classes:
 * <ul>
 * <li>StreamCopier</li>
 * <li>EchoInputStream</li>
 * <li>TeeOutputStream</li>
 * <li>ByteCountingInputStream</li>
 * <li>ByteCountingOutputStream</li>
 * </ul>
 */
public class StreamCopierTest extends TestCase
{
    private static final byte[] BYTES = "12345678901234567890".getBytes();

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
//        System.out.println(StringUtil.hexString(outputbytes,","));
        assertTrue(Arrays.equals(bytes,outputbytes));
        byte[] echobytes = echo.toByteArray();
//        System.out.println(StringUtil.hexString(echobytes,","));
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
//            System.out.println(StringUtil.hexString(outputbytes,","));
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
}
