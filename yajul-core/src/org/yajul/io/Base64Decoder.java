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
// Base64Decoder.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.yajul.io;

import java.io.*;

/**
 * Decode a BASE64 encoded input stream to some output stream.
 * This class implements BASE64 decoding, as specified in the
 * <a href="http://ds.internic.net/rfc/rfc1521.txt">MIME specification</a>.
 * @see org.yajul.io.Base64Encoder
 * @author josh (Refactored to use stream filter)
 */
public class Base64Decoder
{
    /**
     * Convenience method that will decode the given string.
     * @param input The input string
     * @return String - the decoded string.
     * @throws org.yajul.io.Base64FormatException if the input is not valid.
     */
    public static String decode(String input) throws Base64FormatException
    {
        Base64Decoder dec = new Base64Decoder(input);
        return dec.processString();
    }

    /**
     * Convenience method that will decode the given byte array.
     * @param input The input byte array
     * @return String - the decoded byte array.
     * @throws org.yajul.io.Base64FormatException if the input is not valid.
     */
    public static byte[] decode(byte[] input)
            throws Base64FormatException
    {
        return decode(input,0,input.length);
    }

    /**
     * Convenience method that will decode the given byte array.
     * @param input The input byte array
     * @param offset The offset in the buffer of the first byte to read.
     * @param length The maximum number of bytes to read from the buffer.
     * @return String - the decoded byte array.
     * @throws org.yajul.io.Base64FormatException if the input is not valid.
     */
    public static byte[] decode(byte[] input,int offset,int length)
            throws Base64FormatException
    {
        Base64Decoder dec = new Base64Decoder(input,offset,length);
        try
        {
            dec.process();
        }
        catch (IOException e)
        {
            throw new Base64FormatException("Unable to decode input due to: "
                    + e.getMessage());
        }
        return dec.toByteArray();
    }

    private InputStream in = null;
    private OutputStream out = null;
    private boolean byteArrayOutputStream = false;

    /**
     * Default constructor.
     */
    public Base64Decoder()
    {
    }

    /**
     * Create a decoder to decode a stream.
     * @param in The input stream (to be decoded).
     * @param out The output stream, to write decoded data to.
     */
    public Base64Decoder(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
        this.byteArrayOutputStream = false;
    }

    /**
     * Create a decoder to decode a String.
     * @param input The string to be decoded.
     */
    public Base64Decoder(String input)
    {
        try
        {
            byte[] bytes = input.getBytes("ISO8859_1");
            initialize(bytes,0,bytes.length);
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(this.getClass().getName()
                    + "[Constructor] Unable to convert"
                    + "properly char to bytes");
        }

    }

    /**
     * Creates a decoder that will decode the specified array of bytes.
     * @param bytes The array of bytes to be decoded.
     * @param offset The offset in the buffer of the first byte to read.
     * @param length The maximum number of bytes to read from the buffer.
     */
    public Base64Decoder(byte[] bytes,int offset,int length)
    {
        initialize(bytes,offset,length);
    }

    /**
     * Initializes the decoder with an array of base 64 encoded bytes.
     * @param bytes The array of bytes to be decoded.
     * @param offset The offset in the buffer of the first byte to read.
     * @param length The maximum number of bytes to read from the buffer.
     */
    public void initialize(byte[] bytes,int offset,int length)
    {
        this.byteArrayOutputStream = true;
        this.in = new ByteArrayInputStream(bytes,offset,length);
        this.out = new ByteArrayOutputStream();
    }

    /**
     * Do the actual decoding.
     * Process the input stream by decoding it and emiting the resulting bytes
     * into the output stream.
     * @exception java.io.IOException If the input or output stream accesses failed.
     * @exception org.yajul.io.Base64FormatException If the input stream is not compliant
     *    with the BASE64 specification.
     */
    public void process()
            throws IOException, Base64FormatException
    {
        Base64InputStream decodedInput = new Base64InputStream(in);
        StreamCopier.unsyncCopy(decodedInput, out, 16);
        out.flush();
    }

    /**
     * Do the decoding, and return a String.
     * This methods should be called when the decoder is used in
     * <em>String</em> mode. It decodes the input string to an output string
     * that is returned.
     * <br>
     * Throws: RuntimeException If the object wasn't constructed to
     *    decode a String.
     * @return String - The decoded string.
     * @exception org.yajul.io.Base64FormatException If the input string is not compliant
     *     with the BASE64 specification.
     */
    public String processString()
            throws Base64FormatException
    {
        if (!byteArrayOutputStream)
            throw new IllegalStateException(this.getClass().getName()
                    + ".processString() : "
                    + "Not initialized properly for this operation.");
        try
        {
            process();
        }
        catch (IOException e)
        {
            throw new Base64FormatException("Unexpected IOException: "
                    + e.getMessage());
        }
        String s;
        try
        {
            s = ((ByteArrayOutputStream) out).toString("ISO8859_1");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(this.getClass().getName()
                    + "[processString] Unable to convert"
                    + "properly char to bytes");
        }
        return s;
    }

    /**
     * Returns the byte array that is the result of the decoding the input
     * array of bytes.  The instance must have been initialized using
     * the initialize(byte[]) method.
     * @return byte[] An array of decoded bytes.
     */
    public byte[] toByteArray()
    {
        if (!byteArrayOutputStream)
            throw new IllegalStateException(this.getClass().getName()
                    + ".toByteArray() : "
                    + "Not initialized properly for this operation.");
        return ((ByteArrayOutputStream) out).toByteArray();
    }
}
