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
package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.DetailedError;
import org.yajul.io.StreamCopier;

/**
 * Test exception serialization, etc.
 */
public class ExceptionTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * 
     * @param name The name of the test case.
     */
    public ExceptionTest(String name)
    {
        super(name);
    }

    /**
     * Test exception serialziation.
     */
    public void testExceptionSerialization() throws Exception
    {
        Error e = null;
        try
        {
            throwDetailedError();
        }
        catch (DetailedError detailedError)
        {
            e = detailedError;
        }

        assertNotNull(e);
        byte[] bytes = StreamCopier.serializeObject(e);
        DetailedError e2 = (DetailedError)StreamCopier.unserializeObject(bytes);
        assertEquals(e.getMessage(),e2.getMessage());
    }

    private void throwDetailedError()
    {
        Object x = null;
        try
        {
            x.getClass();
        }
        catch (NullPointerException npe)
        {
            throw new DetailedError("Message!",npe);
        }
    }
}
