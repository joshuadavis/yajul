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

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Nov 16, 2002
 * Time: 8:38:06 AM
 */
package org.yajul.util.test;


import junit.framework.TestCase;
import org.yajul.util.MRUSet;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

/**
 * JUnit test case for MRUSet.
 * @author josh
 */
public class MRUSetTest extends TestCase
{
    /**
     * Creates test case MRUSetTest
     * @param name The name of the test (method).
     */
    public MRUSetTest(String name)
    {
        super(name);
    }

    public void testMRU()
    {
        MRUSet set = new MRUSet();
        set.add("a");
        set.add("b");
        set.add("c");


        // Make sure size, MRU and LRU are correct.
        assertEquals(3,set.size());
        assertEquals("c",set.getMRU());
        assertEquals("a",set.getLRU());

        // Touch.
        assertEquals("b",set.touch("b"));
        assertEquals("b",set.getMRU());
        assertEquals("a",set.getLRU());

        // Remove.
        assertEquals(true,set.remove("a"));
        assertEquals(2,set.size());
        assertEquals("c",set.getLRU());

        // Remove non-existing.
        assertEquals(false,set.remove("a"));
        assertEquals(2,set.size());
        assertEquals("c",set.getLRU());

        // Touch non-existing
        assertNull(set.touch("q"));

        // Remove non-existing, never added
        assertEquals(false,set.remove("q"));
        assertEquals(2,set.size());

        // Clear
        set.clear();
        assertEquals(0,set.size());
        assertEquals(false,set.contains("a"));
        assertEquals(false,set.contains("b"));
        assertEquals(false,set.contains("c"));
    }

    public void testRemoveLRU()
    {
        MRUSet set = new MRUSet();
        set.add("a");
        set.add("b");
        set.add("c");

        // Remove one.
        assertEquals("a",set.removeLRU());
        assertEquals("b",set.getLRU());
        assertEquals("c",set.getMRU());
        assertEquals(2,set.size());

        // Remove another.
        assertEquals("b",set.removeLRU());
        assertEquals("c",set.getLRU());
        assertEquals("c",set.getMRU());
        assertEquals(1,set.size());

        // Remove another.
        assertEquals("c",set.removeLRU());
        assertNull(set.getLRU());
        assertNull(set.getMRU());
        assertEquals(0,set.size());
    }

    public void testSet()
    {
        MRUSet set = new MRUSet();

        // Add from a collection.
        String[] values = new String[] { "x", "y", "z" };
        List list = Arrays.asList(values);
        set.addAll(list);
        assertEquals(true,set.contains("x"));
        assertEquals(true,set.contains("y"));
        assertEquals(true,set.contains("z"));

        // Iterator
        for (Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            Object o = (Object) iterator.next();
            assertEquals(true,set.contains(o));
        }
        for (Iterator iterator = set.iterator(); iterator.hasNext();)
        {
            Object o = (Object) iterator.next();
            assertEquals(true,list.contains(o));
        }
    }
}
