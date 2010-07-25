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

import java.io.StringReader;

import junit.framework.TestCase;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.WikiPrinter;
import org.wikimodel.wem.xhtml.PrintListener;

/**
 * @author MikhailKotelnikov
 */
public abstract class AbstractWikiParserTest extends TestCase {

    private boolean fOutputEnabled;

    /**
     * @param name
     */
    public AbstractWikiParserTest(String name) {
        super(name);
    }

    protected void enableOutput(boolean enable) {
        fOutputEnabled = enable;
    }

    protected abstract IWikiParser newWikiParser();

    protected void println(String str) {
        if (fOutputEnabled)
            System.out.println(str);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        enableOutput(true);
    }

    /**
     * @param string
     * @throws WikiParserException
     */
    protected void test(String string) throws WikiParserException {
        test(string, null);
    }

    /**
     * @param string
     * @throws WikiParserException
     */
    protected void test(String string, String control)
        throws WikiParserException {
        println("==================================================");
        StringReader reader = new StringReader(string);
        IWikiParser parser = newWikiParser();
        final StringBuffer buf = new StringBuffer();
        IWemListener listener = newParserListener(buf);
        parser.parse(reader, listener);
        String test = buf.toString();
        println(test);
        checkResults(control, test);
    }

    /**
     * @param control
     * @param test
     */
    protected void checkResults(String control, String test) {
        if (control != null) {
            control = "<div class='doc'>\n" + control + "\n</div>\n";
            assertEquals(control, test);
        }
    }

    /**
     * @param buf
     * @return
     */
    protected IWemListener newParserListener(final StringBuffer buf) {
        IWikiPrinter printer = newPrinter(buf);
        IWemListener listener = new PrintListener(printer);
        return listener;
    }

    /**
     * @param buf
     * @return
     */
    protected IWikiPrinter newPrinter(final StringBuffer buf) {
        IWikiPrinter printer = new IWikiPrinter() {

            public void print(String str) {
                buf.append(str);
            }

            public void println(String str) {
                buf.append(str);
                buf.append("\n");
            }
        };
        return printer;
    }

}
