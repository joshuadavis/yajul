/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Adds tokenizing / parsing methods to an input stream.
 * User: josh
 * Date: Jan 29, 2004
 * Time: 7:12:28 AM
 */
public class ParsingInputStream extends PushbackInputStream
{
    /**
     * Creates a new parsing input stream with a pushback bufer of the size specified
     */
    public ParsingInputStream(InputStream in, int bufferSize)
    {
        super(in, bufferSize);
    }

    /**
     * Reads the next char if it matches 'aChar'.
     */
    public int skipChar(int aChar)
            throws IOException
    {
        int c = read();
        if (c == aChar)
            return c;
        else
        {
            unread(c);
            return c;
        }
    }

    /**
     * Reads the sequence of characters in 'bytes' from the stream.  If
     * the sequence does not match, all characters are pushed back into the stream.
     */
    public byte[] matchBytes(byte[] bytes)
            throws IOException
    {
        byte[] read = new byte[bytes.length];
        int c,i;
        for (i = 0; i < bytes.length; i++)
        {
            c = read();
            read[i] = (byte) c;
            if (c != bytes[i])
                break;
        }
        if (i != bytes.length)      // If the entire sequence did not match...
        {
            unread(read, 0, i + 1);     // Put all of the bytes back into the stream.
            read = null;            // ... return null.
        }
        return read;
    }

    /**
     * Reads characters that are / are not in the set of characters specified.  Returns
     * the characters read.  The 'match' parameter determines the type of matching:
     * <pre>
     *    true  - Input stream characters must be in 'chars' (matching mode).
     *            (like C <i>strspn()</i>)
     *    false - Input stream characters must <b>not</b> be in 'chars' (delimiter mode).
     *            (like C <i>strtok()</i>)
     * </pre>
     *
     * At the end of the operation, the stream (Reader) will be positioned at the first
     * character that did not meet the matching condition.
     *
     * @param chars The set of chars.
     * @param match If true, this will read until a char that is *not* in 'chars'.
     * If false, this will read until a char that is in 'chars'.
     */
    public byte[] readCharset(byte[] chars, boolean match)
            throws IOException
    {
        int c;
        if (chars.length == 0)
            return null;

        boolean found = true;
        byte[] read = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (true)
        {
            c = read();
            found = false;
            for (int i = 0; i < chars.length && !found; i++)
            {
                if (c == chars[i])
                    found = true;
            }
            if ((match && !found) || (!match && found))
            {
                read = baos.toByteArray();
                unread(c);
                break;
            }
            baos.write(c);
        } // while

        return read;
    }

    /**
     * Blocks the thread that calls this method until
     * there is input available.
     */
    public void waitUntilReady()
            throws IOException
    {
        int c = read();
        unread(c);
    }

}
