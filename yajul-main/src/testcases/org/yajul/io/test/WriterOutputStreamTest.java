/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2003
 * Time: 10:56:01 PM
 */
package org.yajul.io.test;

import junit.framework.TestCase;
import org.yajul.io.WriterOutputStream;

import java.io.OutputStream;
import java.io.StringWriter;

/**
 * JUnit test case for writer output stream.
 * @author josh
 */
public class WriterOutputStreamTest extends TestCase
{
    /**
     * Creates test case WriterOutputStreamTest
     * @param name The name of the test (method).
     */
    public WriterOutputStreamTest(String name)
    {
        super(name);
    }

    public void testWriteChars() throws Exception
    {
        StringWriter w = new StringWriter();
        OutputStream out = new WriterOutputStream(w);
        int limit = 256;

        StringBuffer buf = new StringBuffer();

        for(int i = 0; i < limit ; i ++)
        {
            out.write(i);
            buf.append((char)i);
        }
        out.close();

        String s = w.getBuffer().toString();
        String x = buf.toString();
        assertEquals(x,s);
    }

    public void testWriteBytes() throws Exception
    {
        byte[] b = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        char[] c = new char[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        StringWriter w = new StringWriter();
        OutputStream out = new WriterOutputStream(w);
        int limit = 3;

        StringBuffer buf = new StringBuffer();

        for(int i = 0; i < limit ; i ++)
        {
            out.write(b);
            buf.append(c);
        }
        out.close();

        String s = w.getBuffer().toString();
        String x = buf.toString();
        assertEquals(x,s);
    }
}
