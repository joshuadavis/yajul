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
package org.yajul.net.http;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Jan 25, 2004
 * Time: 9:52:35 AM
 */
public interface StatusCodes
{
    /**
     * Unknown status.
     */
    public final static int UNKNOWN  = 0;
    /**
     * 100 Continue <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.1.1">10.1.1</a>
     */
    public final static int CONTINUE = 100;
    /**
     * 204 No Content <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">10.2.5</a>
     */
    public final static int NO_CONTENT = 204;
    /**
     * 302 Moved (redirect)
     */
    public final static int MOVED = 302;
}
