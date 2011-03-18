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

/******************************************************************************
 * Old log...
 *      Revision 1.3  2001/03/13 20:58:45  jdavis
 *      Add in optional stream debugging to HTTPConnection.
 *
 *      Revision 1.2  2000/12/01 23:55:33  cvsuser
 *      First cut.
 *      date	2000.10.23.23.27.00;	author joshuad;	state Exp;
 *
 * 2     10/23/00 7:27p Joshuad
 * First cut.
 ******************************************************************************/

package org.yajul.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Like /dev/null... bytes go in, but they never come out.
 *
 * @author Josh
 */
public class NullOutputStream extends OutputStream {

    public NullOutputStream() {
    }

    @Override
    public void write(byte[] b, int off, int len)
            throws IOException {
    }

    public void write(int b)
            throws IOException {
    }
}