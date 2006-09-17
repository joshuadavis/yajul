package org.yajul.io.test;

import junit.framework.TestCase;
import org.yajul.io.FileUtil;

import java.io.*;

/**
 * Tests FileUtil.tail
 * <br>User: jdavis
 * Date: Sep 17, 2006
 * Time: 11:47:42 AM
 */
public class TailTest extends TestCase
{
    public void testTail() throws Exception
    {
        // Create a file with 300 lines.
        File f = new File("temp.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(f, false));
        int totalLines = 300;
        for (int i = 0; i < totalLines; i++)
        {
            pw.println("this is line " + i);
        }
        pw.close();

        // Tail the last three.
        StringWriter sw = new StringWriter();
        int tailLines = 3;
        FileUtil.tail(tailLines, f, sw);
        sw.close();
        String tail = sw.toString();
        LineNumberReader r = new LineNumberReader(new StringReader(tail));
        String line;
        int lines = 0;
        int expectedLine = totalLines - tailLines;
        while ((line = r.readLine()) != null)
        {
            assertEquals("this is line " + expectedLine, line);
            lines++;
            expectedLine++;
        }
        assertEquals(tailLines, lines);
    }
}
