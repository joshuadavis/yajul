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

package org.yajul.util;

/**
 * Thrown when a class initializer encounters an unexpected state.
 * User: jdavis
 * Date: Jul 21, 2003
 * Time: 1:41:47 PM
 * @author jdavis
 */
public class InitializationError extends Error
{
    /**
     * Default constructor.
     */
    public InitializationError()
    {
    }

    /**
     * Creates a new exception with no nested exception and the specified
     * detail message.
     * @param s the detail message.
     */
    public InitializationError(String s)
    {
        super(s);
    }

    /**
     * Constructs a InitializationError with the specified Throwable as the
     * nested exception.
     * @param t an object of type Throwable
     */
    public InitializationError(Throwable t)
    {
        super(t);
    }

    /**
     * Constructs a InitializationError with the specified detail message and
     * the specified throwable as the nested exception.
     * @param s the detail message.
     * @param t an object of type Throwable
     */
    public InitializationError(String s, Throwable t)
    {
        super(s, t);
    }
}
