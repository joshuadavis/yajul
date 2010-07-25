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
package org.wikimodel.wem.xwiki;

import java.io.Reader;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.impl.WikiScannerContext;
import org.wikimodel.wem.xwiki.javacc.ParseException;
import org.wikimodel.wem.xwiki.javacc.XWikiScanner;

/**
 * @author MikhailKotelnikov
 */
public class XWikiParser implements IWikiParser {

    /**
     * 
     */
    public XWikiParser() {
        super();
    }

    /**
     * @see org.wikimodel.wem.IWikiParser#parse(java.io.Reader,
     *      org.wikimodel.wem.IWemListener)
     */
    public void parse(Reader reader, IWemListener listener)
        throws WikiParserException {
        try {
            XWikiScanner scanner = new XWikiScanner(reader);
            WikiScannerContext context = new WikiScannerContext(listener);
            scanner.parse(context);
        } catch (ParseException e) {
            throw new WikiParserException(e);
        }
    }

}
