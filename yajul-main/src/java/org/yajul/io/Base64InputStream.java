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

import org.yajul.util.Base64Decoder;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * Provides decoding of BASE64 encoded data as an input stream filter.  The
 * underlying input stream is expected to be encoded in BASE64 form.
 * @author Joshua Davis
 */
public class Base64InputStream extends FilterInputStream
{
    private Base64Decoder decoder = new Base64Decoder();

    /**
     * Creates a new Base64 decoding input stream, using the input stream
     * (encoded stream).
     * @param   in  The base 64 encoded input stream
     */
    public Base64InputStream(InputStream in)
    {
        super(in);
    }
}
