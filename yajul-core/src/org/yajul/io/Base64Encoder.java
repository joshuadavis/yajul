/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

// --- The orgiginal code is from: ---
// Base64Encoder.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.yajul.io;

import org.yajul.io.StreamCopier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * BASE64 encoder implementation.
 * This object takes as parameter an input stream and an output stream. It
 * encodes the input stream, using the BASE64 encoding rules, as defined
 * in <a href="http://ds.internic.net/rfc/rfc1521.txt">MIME specification</a>
 * and emit the resulting data to the output stream.
 * @see org.yajul.io.Base64OutputStream
 * @see org.yajul.io.Base64Decoder
 * @author josh (Refactored to use org.yajul.io.Base64OutputStream)
 */
public class Base64Encoder
{

    private InputStream in = null;
    private OutputStream out = null;
    boolean stringp = false;

    /**
     * Process the data: encode the input stream to the output stream.
     * This method runs through the input stream, encoding it to the output
     * stream.
     * @exception java.io.IOException If we weren't able to access the input stream or
     *    the output stream.
     */
    public void process()
            throws IOException
    {
        Base64OutputStream encoder = new Base64OutputStream(out);
        StreamCopier.unsyncCopy(in, encoder, 16);
        encoder.flush();
        return;
    }

    /**
     * Encode the content of this encoder, as a string.
     * This methods encode the String content, that was provided at creation
     * time, following the BASE64 rules, as specified in the rfc1521.
     * @return A String, reprenting the encoded content of the input String.
     */

    public String processString()
    {
        if (!stringp)
            throw new RuntimeException(this.getClass().getName()
                    + "[processString]"
                    + "invalid call (not a String)");
        try
        {
            process();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unexpected IOException: " +
                    e.getMessage());
        }
        return out.toString();
    }

    /**
     * A static utility method that encodes a string.
     * @param string    The input to be encoded.
     */
    public static final String encode(String string)
    {
        Base64Encoder enc = new Base64Encoder(string);
        return enc.processString();
    }

    /**
     * A static utility method that encodes an array of bytes.
     * @param bytes    The input to be encoded.
     */
    public static final String encode(byte[] bytes)
    {
        Base64Encoder enc = new Base64Encoder(bytes);
        return enc.processString();
    }

    /**
     * Create a new Base64 encoder, to encode the given string.
     * @param input The String to be encoded.
     */
    public Base64Encoder(String input)
    {
        byte bytes[];
        try
        {
            bytes = input.getBytes("ISO8859_1");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(this.getClass().getName() +
                    "[Constructor] Unable to convert" +
                    "properly char to bytes");
        }
        init(bytes);
    }

    /**
     * Create a new Base64 encoder, to encode the given array of bytes.
     * @param bytes The String to be encoded.
     */
    public Base64Encoder(byte[] bytes)
    {
        init(bytes);
    }

    /**
     * Create a new Base64 encoder, encoding input to output.
     * @param in The input stream to be encoded.
     * @param out The output stream, to write encoded data to.
     */
    public Base64Encoder(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
        this.stringp = false;
    }

    private void init(byte[] bytes)
    {
        this.stringp = true;
        this.in = new ByteArrayInputStream(bytes);
        this.out = new ByteArrayOutputStream();
    }

}
