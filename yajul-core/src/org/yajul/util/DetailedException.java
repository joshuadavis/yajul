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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Provides an exception that contains a throwable detail exception..  This allows the caller to throw a new
 * exception adding a new message and retaining the existing (nested) stack trace.  NOTE: This is not needed for JDK
 * 1.4 and higher, as new constructors for Exception were introduced.
 * @author Joshua Davis
 */
public class DetailedException extends Exception
{
    /**
     * The nested exception... An object of type Throwable.
     */
    private Throwable cause;

    /**
     * Default contstructor.
     */
    public DetailedException()
    {
    }

    /**
     * Creates a new exception with no nested exception and the specified detail message.
     * @param s the detail message.
     */
    public DetailedException(String s)
    {
        super(s);
    }

    /**
     * Constructs a DetailedException with the specified Throwable as the nested exception.
     * @param t an object of type Throwable
     */
    public DetailedException(Throwable t)
    {
        cause = t;
    }

    /**
     * Constructs a DetailedException with the specified detail message and
     * the specified throwable as the nested exception.
     * @param s the detail message.
     * @param t an object of type Throwable
     */
    public DetailedException(String s,Throwable t)
    {
        super(s);
        cause = t;
    }

    /**
     * Gets the exception.
     * @return an object of type Throwable.
     */
    public Throwable getCause()
    {
        return cause;
    }

    /**
     * Converts everything to a string.
     * @return a string.
     */
    public String toString()
    {
        if (cause == null)
            return super.toString();
        else
            return super.toString() + "[throwable=" + cause.toString() + "]";
    }

    /**
     * Prints a stack trace.
     */
    public void printStackTrace()
    {
        super.printStackTrace();
        if (cause != null)
            cause.printStackTrace();
    }

    /**
     * Prints a stack trace to the specified print stream.
     * @param s the print stream.
     */
    public void printStackTrace(PrintStream s)
    {
        super.printStackTrace(s);
        if (cause != null)
        {
            s.println("Detail:");
            cause.printStackTrace(s);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified print writer.
     * @param w the print writer.s
     */
     public void printStackTrace(PrintWriter w)
     {
        super.printStackTrace(w);
        if (cause != null)
        {
            w.println("Detail:");
            cause.printStackTrace(w);
        }
    }
}
