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

/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 23, 2002
 * Time: 10:46:52 PM
 */
package org.yajul.io;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;


/**
 * Provides decoding of BASE64 encoded data as an input stream filter.  The
 * underlying input stream is expected to be encoded in BASE64 form.
 * @author Joshua Davis
 */
public class Base64InputStream
        extends AbstractByteFilterInputStream
{
    private static final int END_OF_INPUT = 65;

    /** Keeps track of the state (byte number) in the input stream. **/
    private int state;

    /** The previous encoded and parsed byte. **/
    private int previous;

    /**
     * Creates a new Base64 decoding input stream, using the input stream
     * (encoded stream).
     * @param   in  The base 64 encoded input stream
     */
    public Base64InputStream(InputStream in)
    {
        super(in);
        state = 0;
        previous = 0;
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     * <p>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        FilterInputStream#in
     */
    public int read() throws IOException
    {
        int input = 0;
        int parsed = 0;
        int output = 0;
        if (state == -1)    // EOF reached?
            return -1;      // Just return -1

        while ((input = super.readByte()) >= 0)
        {
            parsed = parseCharacter(input);
            // If the parsed character is valid BASE64 input, then
            // enter the state machine.  Otherwise, keep reading.
            if (parsed >= 0 && parsed <= 64)
            {
                // A simple finite state machine based on the state, and the
                // current and previous *parsed* input.
                state++;
                switch (state)
                {
                    case 1: // 1 byte of input, nothing to do.
                        previous = parsed;
                        break;
                    case 2: // 2 bytes of input, output the first byte.
                        output = get1((byte) previous, (byte) parsed);
                        previous = parsed;
                        return output;
                    case 3: // 3 bytes parsed, output the second byte.
                        output = get2((byte) previous, (byte) parsed);
                        previous = parsed;
                        return output;
                    case 4: // 4 bytes parsed, output the third byte.
                        output = get3((byte) previous, (byte) parsed);
                        state = 0;  // Go back to the initial state.
                        return output;
                    default:
                        throw new IllegalStateException(
                                "Unexpected state: " + state);

                } // switch
            } // if
            else if (parsed == END_OF_INPUT)
            {
                return lastByte();
            }
        } // while

        return lastByte();
    }

    private int lastByte() throws IOException
    {
        int output = -1;
        switch (state)
        {
            case 1: // 1 byte of encoded input... no output can be written!
                throw new Base64FormatException(
                        "Base64 encoded input is not terminated properly!");
            case 0:
            case 2: // 2 bytes of encoded input, the first decoded byte has been written.
            case 3: // 3 bytes of encoded input, the second decoded byte has been written.
            case 4: // 4 bytes parsed, third byte already written.
                output = -1;
                break;
            default:
                throw new IllegalStateException(
                        "Unexpected state: " + state);

        } // switch
        // Set 'end of file' state.
        state = -1;
        return output;
    }

    /**
     * Returns the first decoded byte, given the first two bytes of
     * a set of BASE64 encoded input.
     * @param byte1 The first encoded byte.
     * @param byte2 The second encoded byte.
     * @return The first decoded byte.
     */
    private static final int get1(byte byte1, byte byte2)
    {
        return ((byte1 & 0x3f) << 2) | ((byte2 & 0x30) >>> 4);
    }

    /**
     * Returns the second decoded byte, given the second and third bytes
     * of a aset of BASE64 encoded input.
     * @param byte2 The second encoded byte.
     * @param byte3 The third encoded byte.
     * @return The second decoded byte.
     */
    private static final int get2(byte byte2, byte byte3)
    {
        return ((byte2 & 0x0f) << 4) | ((byte3 & 0x3c) >>> 2);
    }

    private static final int get3(byte byte3, byte byte4)
    {
        return ((byte3 & 0x03) << 6) | (byte4 & 0x3f);
    }

    /**
     * Checks a character for correct BASE64 encoding.  Returns -1 if the
     * character is not valid.   Returns END_OF_INPUT if 'ch' is the end of
     * input character.
     * @return int The binary value of the input character, or -1 if it is
     * not a valid BASE64 character.
     */
    public static final int parseCharacter(int ch)
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
                    return END_OF_INPUT;
                case '+':
                    return 62;
                case '/':
                    return 63;
                default:
                    // Invalid BASE64 character.
                    return -1;
            }
        }
    }
}
