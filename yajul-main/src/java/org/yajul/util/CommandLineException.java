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

/*******************************************************************************
* Old log...
*      Revision 1.2  2001/04/04 14:27:14  kvogel
*      Made sure getISOXXX methods return a date in GMT time
*
*      Revision 1.1  2000/12/01 23:56:08  cvsuser
*      Moved to com.rzzzzzzzz.util
*      date	2000.10.18.21.22.00;	author joshuad;	state Exp;
* 
* 1     10/18/00 5:22p Joshuad
* Moved to com.rzzzzzzz.util
*******************************************************************************/

package org.yajul.util;

/**
 * An exception thrown by the CommandLine argument parser when there is
 * an error.
 * @author Kent Vogel
 */
public class CommandLineException extends Exception
{
    /**
     * Creates a new command line exception.
     * @param message The error message.
     */
    public CommandLineException(String message)
    {
        super(message);
    }
}

