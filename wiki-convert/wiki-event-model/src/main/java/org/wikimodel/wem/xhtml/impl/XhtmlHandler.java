/*******************************************************************************
 * Copyright (c) 2005,2006 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem.xhtml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikimodel.wem.WikiPageUtil;
import org.wikimodel.wem.WikiParameter;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiStyle;
import org.wikimodel.wem.impl.IWikiScannerContext;
import org.wikimodel.wem.impl.WikiScannerContext;
import org.wikimodel.wem.xhtml.impl.XhtmlHandler.TagStack.TagContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author kotelnikov
 */
public class XhtmlHandler extends DefaultHandler {

    // TODO: add management of embedded block elements.
    public static class TagHandler {

        public boolean fAccumulateContent;

        /**
         * This flag is <code>true</code> if the current tag can have a text
         * content
         */
        private boolean fContentContainer;

        /**
         * This flag shows if the current tag can be used as a container for
         * embedded documents.
         */
        private boolean fDocumentContainer;

        /**
         * This flag shows if the current tag should be created as a direct
         * child of a document.
         */
        private boolean fRequiresDocument;

        /**
         * @param documentContainer
         * @param requiresDocument
         * @param contentContainer
         */
        public TagHandler(
            boolean documentContainer,
            boolean requiresDocument,
            boolean contentContainer) {
            fDocumentContainer = documentContainer;
            fRequiresDocument = requiresDocument;
            fContentContainer = contentContainer;
        }

        public void begin(TagContext context) {
        }

        public void end(TagContext context) {
        }

        public boolean isContentContainer() {
            return fContentContainer;
        }

        public boolean isDocumentContainer() {
            return fDocumentContainer;
        }

        public boolean requiresDocument() {
            return fRequiresDocument;
        }

    }

    protected static class TagStack {

        public class TagContext {

            private Attributes fAttributes;

            private StringBuffer fContent;

            public TagHandler fHandler;

            String fLocalName;

            private TagContext fParent;

            String fQName;

            String fUri;

            public TagContext(
                TagContext parent,
                String uri,
                String localName,
                String qName,
                Attributes attributes) {
                fUri = uri;
                fLocalName = localName;
                fQName = qName;
                fParent = parent;
                fAttributes = attributes;
            }

            public boolean appendContent(char[] array, int start, int length) {
                if (fHandler == null || !fHandler.fAccumulateContent)
                    return false;
                if (fContent == null) {
                    fContent = new StringBuffer();
                }
                fContent.append(array, start, length);
                return true;
            }

            public String getContent() {
                return fContent != null ? WikiPageUtil.escapeXmlString(fContent
                    .toString()) : "";
            }

            public String getLocalName() {
                return fLocalName;
            }

            private String getLocalName(
                String uri,
                String localName,
                String name,
                boolean upperCase) {
                String result = (localName != null && !"".equals(localName))
                    ? localName
                    : name;
                return upperCase ? result.toUpperCase() : result;
            }

            public String getName() {
                return getLocalName(fUri, fLocalName, fQName, false);
            }

            public WikiParameters getParams() {
                List<WikiParameter> list = new ArrayList<WikiParameter>();
                int len = fAttributes != null ? fAttributes.getLength() : 0;
                for (int i = 0; i < len; i++) {
                    String key = getLocalName(
                        fAttributes.getURI(i),
                        fAttributes.getQName(i),
                        fAttributes.getLocalName(i),
                        false);
                    String value = fAttributes.getValue(i);
                    WikiParameter param = new WikiParameter(key, value);
                    list.add(param);
                }
                WikiParameters params = new WikiParameters(list);
                return params;
            }

            public TagContext getParent() {
                return fParent;
            }

            public String getQName() {
                return fQName;
            }

            public WikiScannerContext getScannerContext() {
                return fScannerContext;
            }

            public String getUri() {
                return fUri;
            }

            public boolean isContentContainer() {
                return fHandler == null || fHandler.isContentContainer();
            }

            public boolean isTag(String string) {
                return string.equals(fLocalName.toLowerCase());
            }

        }

        private static final int CHARACTER = 0;

        private static Map<String, TagHandler> fMap = new HashMap<String, TagHandler>();

        private static final int NEW_LINE = 3;

        private static final char SPACE = 1;

        private static final int SPECIAL_SYMBOL = 2;

        public static void add(String tag, TagHandler handler) {
            fMap.put(tag, handler);
        }

        private TagContext fPeek;

        WikiScannerContext fScannerContext;

        public TagStack(WikiScannerContext context) {
            fScannerContext = context;
        }

        public void beginElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) {
            fPeek = new TagContext(fPeek, uri, localName, qName, attributes);
            localName = fPeek.getName();
            TagHandler handler = fMap.get(localName);
            if (handler != null) {
                fPeek.fHandler = handler;
                if (requiresParentDocument(fPeek)) {
                    fScannerContext.beginDocument();
                }
                handler.begin(fPeek);
            }
        }

        public void endElement() {
            TagHandler handler = fPeek.fHandler;
            if (handler != null) {
                handler.end(fPeek);
            }
            if (requiresParentDocument(fPeek)) {
                fScannerContext.endDocument();
            }
            fPeek = fPeek.fParent;
        }

        /**
         * @param buf
         * @param type
         */
        private void flushBuffer(StringBuffer buf, int type) {
            if (buf.length() > 0) {
                String str = buf.toString();
                switch (type) {
                    case SPACE:
                        fScannerContext.onSpace(str);
                        break;
                    case SPECIAL_SYMBOL:
                        fScannerContext.onSpecialSymbol(str);
                        break;
                    case CHARACTER:
                        str = WikiPageUtil.escapeXmlString(str);
                        fScannerContext.onWord(str);
                        break;
                    case NEW_LINE:
                        fScannerContext.onNewLine();
                        break;
                }
            }
            buf.delete(0, buf.length());
        }

        private int getCharacterType(char ch) {
            int type = CHARACTER;
            switch (ch) {
                case '!':
                case '\'':
                case '#':
                case '$':
                case '%':
                case '&':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '.':
                case '/':
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case '{':
                case '|':
                case '}':
                case '~':
                    type = SPECIAL_SYMBOL;
                    break;
                case ' ':
                case '\t':
                    type = SPACE;
                    break;
                case '\n':
                case '\r':
                    type = NEW_LINE;
                    break;
                default:
                    type = CHARACTER;
                    break;
            }
            return type;
        }

        public WikiScannerContext getScannerContext() {
            return fScannerContext;
        }

        public void onCharacters(char[] array, int start, int length) {
            if (!fPeek.isContentContainer())
                return;
            if (!fPeek.appendContent(array, start, length)) {
                StringBuffer buf = new StringBuffer();
                int type = CHARACTER;
                for (int i = 0; i < length; i++) {
                    char ch = array[start + i];
                    int oldType = type;
                    type = getCharacterType(ch);
                    if (type != oldType) {
                        flushBuffer(buf, type);
                    }
                    buf.append(ch);
                }
                flushBuffer(buf, type);
            }
        }

        /**
         * @param context
         * @return <code>true</code> if the current tag represented by the
         *         given context requires a parent document
         */
        private boolean requiresParentDocument(TagContext context) {
            if (context == null)
                return true;
            if (context.fHandler == null
                || !context.fHandler.requiresDocument())
                return false;
            boolean inContainer = false;
            TagContext parent = context.fParent;
            while (parent != null) {
                if (parent.fHandler != null) {
                    inContainer = parent.fHandler.isDocumentContainer();
                    break;
                }
                parent = parent.fParent;
            }
            return inContainer;
        }
    }

    static {
        TagStack.add("html", new TagHandler(false, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginDocument();
            }

            public void end(TagContext context) {
                context.getScannerContext().endDocument();
            }
        });

        // Simple block elements (p, pre, quotation...)
        TagStack.add("p", new TagHandler(false, true, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginParagraph(context.getParams());
            }

            public void end(TagContext context) {
                context.getScannerContext().endParagraph();
            }
        });

        // Tables
        TagStack.add("table", new TagHandler(false, true, false) {
            public void begin(TagContext context) {
                context.getScannerContext().beginTable(context.getParams());
            }

            public void end(TagContext context) {
                context.getScannerContext().endTable();
            }
        });
        TagStack.add("tr", new TagHandler(false, false, false) {
            public void begin(TagContext context) {
                context.getScannerContext().beginTableRow(false);
            }

            public void end(TagContext context) {
                context.getScannerContext().endTableRow();
            }
        });
        TagHandler handler = new TagHandler(true, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginTableCell(context.isTag("th"));
            }

            public void end(TagContext context) {
                context.getScannerContext().endTableCell();
            }
        };
        TagStack.add("td", handler);
        TagStack.add("th", handler);

        // Lists
        handler = new TagHandler(false, true, false) {
            public void begin(TagContext context) {
                context.getScannerContext().beginList();
            }

            public void end(TagContext context) {
                context.getScannerContext().endList();
            }
        };
        TagStack.add("ul", handler);
        TagStack.add("ol", handler);
        TagStack.add("li", new TagHandler(true, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginListItem("*");
            }

            public void end(TagContext context) {
                context.getScannerContext().endListItem();
            }
        });

        TagStack.add("dl", new TagHandler(false, true, false) {
            public void begin(TagContext context) {
                context.getScannerContext().beginList();
            }

            public void end(TagContext context) {
                context.getScannerContext().endList();
            }
        });
        TagStack.add("dt", new TagHandler(false, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginListItem(";");
            }

            public void end(TagContext context) {
                context.getScannerContext().endListItem();
            }
        });
        TagStack.add("dd", new TagHandler(true, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().beginListItem(":");
            }

            public void end(TagContext context) {
                context.getScannerContext().endListItem();
            }
        });

        // Headers
        handler = new TagHandler(false, true, true) {
            public void begin(TagContext context) {
                String tag = context.getName();
                int level = Integer.parseInt(tag.substring(1, 2));
                context.getScannerContext().beginHeader(level);
            }

            public void end(TagContext context) {
                context.getScannerContext().endHeader();
            }
        };
        TagStack.add("h1", handler);
        TagStack.add("h2", handler);
        TagStack.add("h3", handler);
        TagStack.add("h4", handler);
        TagStack.add("h5", handler);
        TagStack.add("h6", handler);

        // Unique block elements
        TagStack.add("hr", new TagHandler(false, true, false) {
            public void begin(TagContext context) {
                context.getScannerContext().onHorizontalLine();
            }
        });
        TagStack.add("pre", new TagHandler(false, true, true) {
            {
                fAccumulateContent = true;
            }

            public void end(TagContext context) {
                String str = context.getContent();
                context.getScannerContext().onVerbatim(str, false);
            }
        });

        // In-line elements
        TagStack.add("a", new TagHandler(false, false, true) {
            {
                fAccumulateContent = true;
            }

            public void begin(TagContext context) {
            }

            public void end(TagContext context) {
                // TODO: it should be replaced by a normal parameters
                WikiParameter ref = context.getParams().getParameter("href");
                if (ref != null) {
                    String content = context.getContent();
                    context.getScannerContext().onReference(
                        ref.getValue() + " " + content,
                        true);
                }
            }
        });

        handler = new TagHandler(false, false, true) {
            public void begin(TagContext context) {
                context
                    .getScannerContext()
                    .onFormat(IWikiScannerContext.STRONG);
            }

            public void end(TagContext context) {
                context
                    .getScannerContext()
                    .onFormat(IWikiScannerContext.STRONG);
            }
        };
        TagStack.add("strong", handler);
        TagStack.add("b", handler);

        handler = new TagHandler(false, false, true) {
            public void begin(TagContext context) {
                context.getScannerContext().onFormat(IWikiScannerContext.EM);
            }

            public void end(TagContext context) {
                context.getScannerContext().onFormat(IWikiScannerContext.EM);
            }
        };
        TagStack.add("em", handler);
        TagStack.add("i", handler);
    }

    protected String fDocumentSectionUri;

    protected String fDocumentUri;

    protected String fDocumentWikiProperties;

    TagStack fStack;

    /**
     * @param context
     */
    public XhtmlHandler(WikiScannerContext context) {
        fStack = new TagStack(context);
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] array, int start, int length)
        throws SAXException {
        fStack.onCharacters(array, start, length);
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        fStack.endElement();
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
        throws SAXException {
        fStack.endElement();
    }

    protected String getHref(Attributes attributes) {
        String value = attributes.getValue("HREF");
        if (value == null)
            value = attributes.getValue("href");
        if (value == null)
            value = attributes.getValue("src");
        if (value == null)
            value = attributes.getValue("SRC");
        return value;
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        fStack.beginElement(null, null, null, null);
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes) throws SAXException {
        fStack.beginElement(uri, localName, qName, attributes);
    }

}
