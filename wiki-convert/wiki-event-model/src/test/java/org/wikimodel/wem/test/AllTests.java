/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.wikimodel.wem.creole");
        // $JUnit-BEGIN$
        suite.addTestSuite(CreoleWikiParserTest.class);
        suite.addTestSuite(GWikiParserTest.class);
        suite.addTestSuite(JspWikiParserTest.class);
        suite.addTestSuite(MediawikiParserTest.class);
        suite.addTestSuite(XWikiParserTest.class);
        suite.addTestSuite(CommonWikiParserTest.class);
        // $JUnit-END$
        return suite;
    }

}
