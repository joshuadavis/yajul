package org.yajul.io;

import junit.framework.TestCase;

import java.io.*;

import org.yajul.util.Copier;

/**
 * Tests for the filter classes.
 * <br>
 * User: josh
 * Date: Apr 25, 2009
 * Time: 11:37:28 AM
 */
public class FilterTest extends TestCase {
    public void testAbstractByteFilterReader() throws IOException {
        Reader r = new AbstractByteFilterReader(new StringReader("abcdef|g")) {
            private boolean eos = false;

            public int read() throws IOException {
                if (eos)
                    return -1;
                int x = readByte();
                switch (x) {
                    case '|':
                        eos = true;
                        return -1;
                    default:
                        return x;
                }
            }
        };

        StringWriter sw = new StringWriter();
        Copier.copy(r,sw,Copier.DEFAULT_BUFFER_SIZE,Copier.UNLIMITED);
        TestCase.assertEquals("abcdef", sw.toString());
    }
    
    public void testAbstractByteFilterInputStream() throws IOException {
        InputStream r = new AbstractByteFilterInputStream(new StringInputStream("abcdef|g")) {
            private boolean eos = false;

            public int read() throws IOException {
                if (eos)
                    return -1;
                int x = readByte();
                switch (x) {
                    case '|':
                        eos = true;
                        return -1;
                    default:
                        return x;
                }
            }
        };

        ByteArrayOutputStream sw = new ByteArrayOutputStream();
        Copier.copy(r,sw,Copier.DEFAULT_BUFFER_SIZE,Copier.UNLIMITED);
        TestCase.assertEquals("abcdef", sw.toString());
    }

}