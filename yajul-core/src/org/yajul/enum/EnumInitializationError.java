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
package org.yajul.enum;

import org.yajul.util.InitializationError;

/**
 * Error thrown when an enumerated type cannot be initialized.
 * @author josh Mar 12, 2004 8:13:12 PM
 */
public class EnumInitializationError extends InitializationError
{
    /**
     * Creates a new enum initialization error.
     */
    public EnumInitializationError()
    {
        super();
    }

    /**
     * Creates a new initialization error.
     * @param s The error message.
     */
    public EnumInitializationError(String s)
    {
        super(s);
    }

    /**
     * Creates a new initialization error.
     * @param t Nested exception detail.
     */
    public EnumInitializationError(Throwable t)
    {
        super(t);
    }

    /**
     * Creates a new initialization error.
     * @param s The error message.
     * @param t The nested exception detail.
     */
    public EnumInitializationError(String s, Throwable t)
    {
        super(s, t);
    }
}
