package org.yajul.io;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Provides an input stream that reads from a string.  This implementation is
 * a very simple extension of ByteArrayInputStream.
 * (BTW, What's up with BEA's implementation of this class?  Take a look at
 * it if you need a laugh).
 * User: jdavis
 * Date: Aug 12, 2003
 * Time: 3:03:00 PM
 * @author jdavis
 */
public class StringInputStream extends ByteArrayInputStream
{
    /**
     * Creates a new StringInputStream that will read the supplied string.
     * @param s The string to read.
     */
    public StringInputStream(String s)
    {
        super(s.getBytes());
    }

    /**
     * Creates a new StringInputStream that will read the supplied string, given
     * the encoding.
     * @param s The string to read.
     * @param encoding The string encoding.
     * @throws UnsupportedEncodingException If the supplied encoding is not
     * supported.
     */
    public StringInputStream(String s,String encoding)
            throws UnsupportedEncodingException
    {
        super(s.getBytes(encoding));
    }

}
