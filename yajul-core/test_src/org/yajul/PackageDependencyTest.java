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
package org.yajul;

import jdepend.framework.JDepend;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.yajul.util.ObjectFactory;

/**
 * Tests all packages for compliance with package dependency rules.
 */
public class PackageDependencyTest extends TestCase
{
    private JDepend jdepend;

    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public PackageDependencyTest(String name)
    {
        super(name);
    }


    protected void setUp()
    {

        jdepend = new JDepend();

        try
        {
            // Find the location of the yajul-core main classes
            String root = ObjectFactory.getClasspathRoot(ObjectFactory.class);
            jdepend.addDirectory(root);
        }
        catch (Exception e)
        {
            Logger.getLogger(this.getClass()).error("Unexpected: " + e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Tests that a package dependency cycle does not
     * exist for any of the analyzed packages.
     */
    public void testCyclicDependencies()
    {
        jdepend.analyze();
        assertEquals("Cyclic package dependencies found!",
                false, jdepend.containsCycles());
    }

    /**
     * Tests that the package dependency constraint
     * is met for the analyzed packages.
     */
    public void testLibraryDependency()
    {
        // TODO: Make sure that org.yajul.* does not depend on commons-logging.
    }
}
