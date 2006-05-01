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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import junit.framework.TestCase;

import org.yajul.io.StreamCopier;
import org.yajul.util.DetailedError;
import org.yajul.util.DetailedException;
import org.yajul.util.DetailedRuntimeException;
import org.yajul.util.ExceptionList;
import org.yajul.util.InitializationError;
import org.yajul.util.InitializationException;

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

    public void testDetailedError()
    {
        DetailedError de = null;
        try
        {
            throw new DetailedError();
        }
        catch (DetailedError e)
        {
            de = e;
        }
        checkEmptyException(de);

        de = null;
        try
        {
            throw new DetailedError("test");
        }
        catch (DetailedError e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());

        de = null;
        try
        {
            throw new DetailedError(new Error("test 2"));
        }
        catch (DetailedError e)
        {
            de = e;
        }
        assertEquals("test 2",de.getCause().getMessage());
        printStackTrace(de);
    }

    private void checkEmptyException(Throwable t)
    {
        assertNotNull(t);
        assertNull(t.getMessage());
        assertNotNull(t.toString());
        printStackTrace(t);
    }

    public void testDetailedException()
    {
        DetailedException de = null;
        try
        {
            throw new DetailedException();
        }
        catch (DetailedException e)
        {
            de = e;
        }
        checkEmptyException(de);

        de = null;
        try
        {
            throw new DetailedException("test");
        }
        catch (DetailedException e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());

        de = null;
        try
        {
            throw new DetailedException(new Error("test 2"));
        }
        catch (DetailedException e)
        {
            de = e;
        }
        assertEquals("test 2",de.getCause().getMessage());
        de = null;
        try
        {
            throw new DetailedException("test it",new Error("test 3"));
        }
        catch (DetailedException e)
        {
            de = e;
        }
        assertEquals("test it",de.getMessage());
        assertEquals("test 3",de.getCause().getMessage());
        printStackTrace(de);
    }

    public void testDetailedRuntimeException()
    {
        DetailedRuntimeException de = null;
        try
        {
            throw new DetailedRuntimeException();
        }
        catch (DetailedRuntimeException e)
        {
            de = e;
        }
        checkEmptyException(de);

        de = null;
        try
        {
            throw new DetailedRuntimeException("test");
        }
        catch (DetailedRuntimeException e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());

        de = null;
        try
        {
            throw new DetailedRuntimeException(new Error("test 2"));
        }
        catch (DetailedRuntimeException e)
        {
            de = e;
        }
        assertEquals("test 2",de.getCause().getMessage());
        de = null;
        try
        {
            throw new DetailedRuntimeException("test it",new Error("test 3"));
        }
        catch (DetailedRuntimeException e)
        {
            de = e;
        }
        assertEquals("test it",de.getMessage());
        assertEquals("test 3",de.getCause().getMessage());
        printStackTrace(de);
    }

    public void testInitializationException()
    {
        InitializationException de = null;
        try
        {
            throw new InitializationException("test");
        }
        catch (InitializationException e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());

        de = null;
        try
        {
            throw new InitializationException();
        }
        catch (InitializationException e)
        {
            de = e;
        }
        assertNull(de.getMessage());
        
        de = null;
        try
        {
            throw new InitializationException(new Error("test 2"));
        }
        catch (InitializationException e)
        {
            de = e;
        }
        assertEquals("test 2",de.getCause().getMessage());
        de = null;
        try
        {
            throw new InitializationException("test it",new Error("test 3"));
        }
        catch (InitializationException e)
        {
            de = e;
        }
        assertEquals("test it",de.getMessage());
        assertEquals("test 3",de.getCause().getMessage());
        printStackTrace(de);
    }

    public void testInitializationError()
    {
        InitializationError de = null;
        try
        {
            throw new InitializationError("test");
        }
        catch (InitializationError e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());

        de = null;
        try
        {
            throw new InitializationError();
        }
        catch (InitializationError e)
        {
            de = e;
        }
        assertNull(de.getMessage());

        de = null;
        try
        {
            throw new InitializationError(new Error("test 2"));
        }
        catch (InitializationError e)
        {
            de = e;
        }
        assertEquals("test 2",de.getCause().getMessage());
        de = null;
        try
        {
            throw new InitializationError("test it",new Error("test 3"));
        }
        catch (InitializationError e)
        {
            de = e;
        }
        assertEquals("test it",de.getMessage());
        assertEquals("test 3",de.getCause().getMessage());
        printStackTrace(de);
    }

    public void testExceptionList() throws Throwable
    {
        // Empty exception list.
        ExceptionList de = null;
        try
        {
            throw new ExceptionList();
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        checkEmptyException(de);

        // Exception list with one exception.
        de = null;
        try
        {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.add(new Exception("test"));
            throw exceptionList;
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        assertNotNull(de);
        assertEquals("test",de.getMessage());
        assertEquals("test",de.getLocalizedMessage());

        // Throw the exception if the exception list has exactly one exception.
        Exception ex = null;
        try
        {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.add(new Exception("test"));
            exceptionList.throwIfException();
        }
        catch (Exception e)
        {
            ex = e;
        }
        assertNotNull(ex);
        assertEquals("test",ex.getMessage());

        // Throw the exception list if the exception list has exactly one non-exception
        ex = null;
        try
        {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.add(new Error("test"));
            exceptionList.throwIfException();
        }
        catch (Exception e)
        {
            ex = e;
        }
        assertNotNull(ex);
        assertTrue(ex instanceof ExceptionList);

        // Throw the exception list if the exception list has more than one exception.
        ex = null;
        try
        {
            ExceptionList exceptionList = new ExceptionList();
            exceptionList.add(new Exception("test a"));
            exceptionList.add(new Exception("test b"));
            exceptionList.throwIfException();
        }
        catch (Exception e)
        {
            ex = e;
        }
        assertTrue(ex instanceof ExceptionList);

        de = null;
        try
        {
            throw new ExceptionList("test");
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        assertEquals("test",de.getMessage());
        assertNull(de.getCause());

        de = null;
        try
        {
            throw new ExceptionList(new Error("test 2"));
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        assertNotNull(de);
        assertEquals("test 2",de.getCause().getMessage());
        de = null;
        try
        {
            throw new ExceptionList("test it",new Error("test 3"));
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        assertEquals("test it",de.getMessage());
        assertEquals("test 3",de.getCause().getMessage());
        printStackTrace(de);

        Throwable th = null;
        try
        {
            de.throwIfThrowable();
        }
        catch(Throwable t)
        {
            th = t;
        }
        assertEquals(th.getMessage(),"test 3");

        de.add(new Error("another"));
        assertEquals(2,de.size());
        Iterator iter = de.iterator();
        Throwable t = (Throwable) iter.next();
        assertEquals("test 3",t.getMessage());
        t = (Throwable) iter.next();
        assertEquals("another",t.getMessage());
        th = null;
        try
        {
            de.throwIfThrowable();
        }
        catch(Throwable tt)
        {
            th = tt;
        }
        assertSame(de,th);

        th = null;
        try
        {
            de.throwIfException();
        }
        catch(Throwable tt)
        {
            th = tt;
        }
        assertSame(de,th);

        // Test empty list.
        de = null;
        try
        {
            throw new ExceptionList("test empty list");
        }
        catch (ExceptionList e)
        {
            de = e;
        }
        assertEquals("test empty list",de.getMessage());
        assertNull(de.getCause());

        th = null;
        de.throwIfThrowable();
        de.throwIfException();
        assertNull(th);
    }

    private void printStackTrace(Throwable t)
    {
        PrintStream o = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        t.printStackTrace();
        StringWriter stringWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        t.printStackTrace(ps);
        System.setErr(o);
    }
}
