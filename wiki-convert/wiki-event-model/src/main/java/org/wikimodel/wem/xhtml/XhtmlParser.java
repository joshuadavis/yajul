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
package org.wikimodel.wem.xhtml;

import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.impl.WikiScannerContext;
import org.wikimodel.wem.xhtml.impl.XhtmlHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author MikhailKotelnikov
 */
public class XhtmlParser implements IWikiParser {

    /**
     * 
     */
    public XhtmlParser() {
        super();
    }

    /**
     * @param listener the listener object wich will be used to report about all
     *        structural elements on the wiki page.
     * @return a XHTML SAX handler wich can be used to generate well-formed
     *         sequence of WEM events; all events will be reported to the given
     *         listener object.
     */
    public DefaultHandler getHandler(IWemListener listener) {
        WikiScannerContext context = new WikiScannerContext(listener);
        XhtmlHandler handler = new XhtmlHandler(context);
        return handler;
    }

    /**
     * @see org.wikimodel.wem.IWikiParser#parse(java.io.Reader,
     *      org.wikimodel.wem.IWemListener)
     */
    public void parse(Reader reader, IWemListener listener)
        throws WikiParserException {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            InputSource source = new InputSource(reader);
            DefaultHandler handler = getHandler(listener);
            parser.parse(source, handler);
        } catch (Exception e) {
            throw new WikiParserException(e);
        }
    }

}
