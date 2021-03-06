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

import org.yajul.util.StringUtil;
import org.yajul.util.Bytes;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Encodes the input in hexadecimal form.
 * User: josh
 * Date: Jan 23, 2004
 * Time: 9:20:13 AM
 */
public class HexEncodingOutputStream extends FilterOutputStream {
    /**
     * Hex chars buffer. *
     */
    private char[] hex;
    /**
     * Hex byte buffer. *
     */
    private byte[] hexBytes;
    /**
     * The hexadecimal encoding character set. *
     */
    private char[] hexEncodingChars;

    public HexEncodingOutputStream(OutputStream out) {
        super(out);
        hexBytes = new byte[2];
        hex = new char[2];
        hexEncodingChars = Bytes.HEX_CHARS_LOWER;
    }

    public void write(int b) throws IOException {
        writeHex(b);
    }

    protected void writeHex(int b) throws IOException {
        StringUtil.hexChars(hexEncodingChars, b, hex);
        hexBytes[0] = (byte) hex[0];
        hexBytes[1] = (byte) hex[1];
        out.write(hexBytes);
    }
}
