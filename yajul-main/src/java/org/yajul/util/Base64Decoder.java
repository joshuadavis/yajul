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

// Base64Decoder.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.yajul.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Decode a BASE64 encoded input stream to some output stream.
 * This class implements BASE64 decoding, as specified in the
 * <a href="http://ds.internic.net/rfc/rfc1521.txt">MIME specification</a>.
 * @see org.w3c.tools.codec.Base64Encoder
 * @see Base64Encoder
 * @author Unknown
 */
public class Base64Decoder
{
    private static final int BUFFER_SIZE = 1024;

    /**
     * Convenience method that will decode the given string.
     * @param input The input string
     * @return String - the decoded string.
     * @throws Base64FormatException if the input is not valid.
     */
    public static final String decode(String input) throws Base64FormatException
    {
        Base64Decoder dec = new Base64Decoder(input);
        return dec.processString();
    }

    /**
     * Convenience method that will decode the given byte array.
     * @param input The input byte array
     * @return String - the decoded byte array.
     * @throws Base64FormatException if the input is not valid.
     */
    public static final byte[] decode(byte[] input)
            throws Base64FormatException
    {
        Base64Decoder dec = new Base64Decoder(input);
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
            initialize(input.getBytes("ISO8859_1"));
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
     */
    public Base64Decoder(byte[] bytes)
    {
        initialize(bytes);
    }

    /**
     * Initializes the decoder with an array of base 64 encoded bytes.
     * @param bytes The array of bytes to be decoded.
     */
    public void initialize(byte[] bytes)
    {
        this.byteArrayOutputStream = true;
        this.in = new ByteArrayInputStream(bytes);
        this.out = new ByteArrayOutputStream();
    }

    /**
     * Do the actual decoding.
     * Process the input stream by decoding it and emiting the resulting bytes
     * into the output stream.
     * @exception IOException If the input or output stream accesses failed.
     * @exception Base64FormatException If the input stream is not compliant
     *    with the BASE64 specification.
     */
    public void process()
            throws IOException, Base64FormatException
    {
        byte buffer[] = new byte[BUFFER_SIZE];
        byte chunk[] = new byte[4];
        byte outbuf[] = new byte[3];

        int bufferLen = -1;
        int ready = 0;
        int length;

        fill:
        while ((bufferLen = in.read(buffer)) > 0)
        {
            int bufferPos = 0;
            while (bufferPos < bufferLen)
            {
                // Check for un-understood characters:
                while (ready < 4)
                {
                    if (bufferPos >= bufferLen)
                        continue fill;
                    int ch = check(buffer[bufferPos++]);
                    if (ch >= 0)
                        chunk[ready++] = (byte) ch;
                }

                length = decodeChunk(chunk, 0, outbuf);
                out.write(outbuf, 0, length);
                ready = 0;
            }
        }
        if (ready != 0)
            throw new Base64FormatException("Invalid length.");
        out.flush();
    }


    private static int decodeChunk(byte[] chunk, int offset, byte[] out)
    {
        out[0] = (byte) get1(chunk, offset);

        if (chunk[offset + 2] == 65)
            return 1;

        out[1] = (byte) get2(chunk, offset);

        if (chunk[offset + 3] == 65)
            return 2;

        out[2] = (byte) get3(chunk, offset);
        return 3;
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
     * @exception Base64FormatException If the input string is not compliant
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

    private static final int get1(byte buf[], int off)
    {
        return ((buf[off] & 0x3f) << 2) | ((buf[off + 1] & 0x30) >>> 4);
    }

    private static final int get2(byte buf[], int off)
    {
        return ((buf[off + 1] & 0x0f) << 4) | ((buf[off + 2] & 0x3c) >>> 2);
    }

    private static final int get3(byte buf[], int off)
    {
        return ((buf[off + 2] & 0x03) << 6) | (buf[off + 3] & 0x3f);
    }

    /**
     * Checks a character for correct BASE64 encoding.  Returns -1 if the
     * character is not valid.
     * @return int The binary value of the input character, or -1 if it is
     * not a valid BASE64 character.
     */
    private static final int check(int ch)
    {
        if ((ch >= 'A') && (ch <= 'Z'))
        {
            return ch - 'A';
        }
        else if ((ch >= 'a') && (ch <= 'z'))
        {
            return ch - 'a' + 26;
        }
        else if ((ch >= '0') && (ch <= '9'))
        {
            return ch - '0' + 52;
        }
        else
        {
            switch (ch)
            {
                case '=':
                    return 65;
                case '+':
                    return 62;
                case '/':
                    return 63;
                default:
                    return -1;
            }
        }
    }

}
