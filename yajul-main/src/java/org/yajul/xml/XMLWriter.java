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

package org.yajul.xml;

// ***** ORIGINAL COMMENTS *****
// XMLWriter.java - based class for a SAX-based XML writer.
// Copyright (c) 1999 by Megginson Technologies Ltd.
// No warranty; free redistribution permitted.
// Written by David Megginson, david@megginson.com
// Id: XMLWriter.java,v 1.1 1999/07/22 01:24:01 david Exp

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Arrays;

/**
 * A SAX ContentHandler that write an XML document to an underlying Writer.
 *
 * This class effectively serializes SAX ContentHandler events back
 * into XML.  The result is identical to the original document from
 * the SAX 1.0 perspective, but it loses information (such as
 * comments or the document type declaration) that are not available
 * as SAX events.  This version also does not support DTDHandler events,
 * though they could be added in a future version if there were a
 * demand.
 *
 * This class can also function as an application for normalizing
 * an XML document.
 *
 * <br>
 * TODO: Handle namespaces correctly.
 * TODO: Add setters for boolean flags.
 *
 * <br>
 * 2003-02-13 [jsd] - Replaced deprecated interfaces with newer,
 * JAXP 1.1 interfaces.   Eliminated 'bit flags' from public view.
 *
 * @author David Megginson, david@megginson.com
 * @author Joshua Davis, pgmjsd@sourceforge.net (refactoring and updating)
 * @version 1.1
 * @see java.io.Writer
 */
public class XMLWriter implements DTDHandler, ContentHandler
        // implements DocumentHandler <<<-- DEPRECATED!
{
    /**
     * Escape non-ASCII characters.
     *
     * This flag will cause all non-US-ASCII characters to be escaped
     * as character references.  The flag does not affect characters
     * in element names, attribute names, or processing instructions.
     */
    private boolean escapeNonAscii = false;


    /**
     * Add newlines for readability.
     *
     * This flag will cause newlines to be added before each attribute
     * specification and before the closing delimiter of start and
     * end tags, to improve document readability.
     */
    private boolean addNewlines = false;


    /**
     * Include a DOCTYPE declaration.
     *
     * This flag causes the XML writer to include a DOCTYPE declaration
     * immediately before the beginning of the root element.  It also
     * activates the methods for writing notation and unparsed enitity
     * declarations.
     *
     * @see #notationDecl
     * @see #unparsedEntityDecl
     */
    private boolean includeDoctype = false;


    /**
     * Exclude the XML declaration.
     *
     * This flag causes the XML declaration to be omitted.
     */
    private boolean excludeXMLDecl = false;


    /**
     * Don't sort the attributes.
     *
     * By default, attributes are sorted so that they appear in
     * a standard order for the sake of comparison; if this flag
     * is set, the writer will run much faster, but attributes
     * will appear in random order.
     */
    private boolean unsortedAttributes = true;


    /**
     * Kludge output for compatibility with level-3 HTML browsers.
     *
     * When this flag is set, no XML declaration, processing
     * instructions, or document type declaration will be
     * displayed, and empty element tags will have a space before
     * the closing delimiter if the ADD_NEWLINES flag is not
     * set.  If this flag is set, EXCLUDE_XMLDECL will be enabled
     * and INCLUDE_DOCTYPE will be disabled automatically.
     */
    private boolean level3Compatibility = false;

    /**
     * Filter ignorable whitespace.
     *
     * Any ignorable whitespace will be... IGNORED! The default
     * setting is to outtput any ignorable whitespace.
     */
    private boolean noIgnorableWhitespace = false;

    /**
     * Use empty tag shorthand.
     *
     * If this flag is set, elements with no children will be written
     * as empty tags.
     */
    private boolean useEmptyTags = false;

    /**
     * Add a newline at the end of the document.
     */
    private boolean newlineAtEndOfDocument = false;

    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private String encoding;
    private boolean writing = false;
    private boolean seenElement = false;
    private Stack elementStack;
    private Writer output;
    private Locator locator;
    private ArrayList declarations;

    // Remember the potentially empty tag for making <tag/> shorthand.
    private String emptyElementTag;

    private Attributes emptyElementAttributes;

    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Construct a new XML writer using the writer supplied.
     *
     * @param output The writer for the output.
     */
    public XMLWriter(Writer output)
    {
        super();
        init(output, null);
    }

    /**
     * Construct a new XML writer with an explicit encoding.
     *
     * The XML writer has no way to check that the writer supplied
     * actually uses the specified encoding -- it is up to the
     * application to ensure that the two are in sync.
     *
     * @param output The writer for the output.
     * @param encoding The output encoding for the writer.
     */
    public XMLWriter(Writer output, String encoding)
    {
        super();
        init(output, encoding);
    }

    ////////////////////////////////////////////////////////////////////
    // Public methods specific to this class.
    ////////////////////////////////////////////////////////////////////

    public void setExcludeXMLDecl(boolean flag)
    {
        excludeXMLDecl = flag;
    }

    public void setLevel3Compatibility(boolean flag)
    {
        if (flag)
        {
            excludeXMLDecl = true;
            includeDoctype = false;
        }

    }

    public void emptyElement(String name, Attributes atts)
            throws SAXException
    {
        // Basic well-formedness checks.
        checkWriting("emptyElement");
        if (elementStack.empty())
        {
            if (seenElement)
            {
                error("XML allows only one root element in each document.");
            }
            else if (includeDoctype)
            {
                writeDoctype(name);
            }
        }
        seenElement = true;
        writeTag(name, atts, true);
        // Forget that we wrote the empty element.
        emptyElementTag = null;
        emptyElementAttributes = null;
    }


    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.DTDHandler
    ////////////////////////////////////////////////////////////////////


    /**
     * Save a notation declaration to be written later.
     *
     * This method will work only when the INCLUDE_DOCTYPE flag
     * is set (it is not set by default).  A SAX parser will report
     * notation declaration events only when the writer has been
     * registered as the DTD handler.
     *
     * The declaration will not be written right away; instead, it
     * will be saved and then written into the DOCTYPE declaration
     * just before the beginning of the document element.
     *
     * @param name The notation name.
     * @param publicId The notation's public identifier.
     * @param systemId The notation's system identifer.
     * @exception org.xml.sax.SAXException When the document is not
     *            currently being written.
     * @see #includeDoctype
     * @see org.xml.sax.DTDHandler#notationDecl
     */
    public void notationDecl(String name,
                             String publicId,
                             String systemId)
            throws SAXException
    {
        // Return if we're not doing the
        // DOCTYPE thing.
        if (!includeDoctype)
        {
            return;
        }
        // Check for well-formedness.
        checkWriting("notationDecl");
        if (seenElement)
        {
            error("Notation declaration after start of document element");
        }
        // Save a copy of the declaration.
        String decl = "<!NOTATION " + name;
        if (publicId != null)
        {
            decl = decl + " PUBLIC \"" + publicId + '"';
        }
        if (systemId != null)
        {
            if (publicId == null)
            {
                decl = decl + " SYSTEM";
            }
            decl = decl + " \"" + systemId + '"';
        }
        decl = decl + '>';
        declarations.add(decl);
    }


    /**
     * Save an unparsed entity declaration to be written later.
     *
     * This method will work only when the INCLUDE_DOCTYPE flag
     * is set (it is not set by default).  A SAX parser will report
     * unparsed entity declaration events only when the XML writer
     * has been registered as the DTD handler.
     *
     * The declaration will not be written right away; instead, it
     * will be saved and then written into the DOCTYPE declaration
     * just before the beginning of the document element.
     *
     * @param name The unparsed entity name.
     * @param publicId The public identifier.
     * @param systemId The system identifier.
     * @param notationName The name of the data content notation.
     * @exception org.xml.sax.SAXException When the document is not
     *            currently being written.
     * @see #includeDoctype
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl
     */
    public void unparsedEntityDecl(String name,
                                   String publicId,
                                   String systemId,
                                   String notationName)
            throws SAXException
    {
        // Return if we're not doing the
        // DOCTYPE thing.
        if (!includeDoctype)
        {
            return;
        }
        // Check for well-formedness.
        checkWriting("unparsedEntityDecl");
        if (seenElement)
        {
            error("NDATA entity declaration after start of document element");
        }
        // Save a copy of the declaration.
        String decl = "<!ENTITY " + name;
        if (publicId != null)
        {
            decl = decl + " PUBLIC \"" + publicId + '"';
        }
        if (systemId != null)
        {
            if (publicId == null)
            {
                decl = decl + " SYSTEM";
            }
            decl = decl + " \"" + systemId + '"';
        }
        decl = decl + " NDATA " + notationName + '>';
        declarations.add(decl);
    }


    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.DocumentHandler
    ////////////////////////////////////////////////////////////////////


    /**
     * Receive a document locator for error reporting.
     *
     * This method is optional; there is no requirement to deliver
     * a locator, but it can make error reporting more accurate.
     *
     * @param locator The document locator.
     */
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }


    /**
     * Start an XML document.
     *
     * Add a generic XML declaration.
     *
     * @exception org.xml.sax.SAXException If the writer has already
     *            started (but not finished) another document.
     */
    public void startDocument()
            throws SAXException
    {
        elementStack = new Stack();
        if (includeDoctype)
        {
            declarations = new ArrayList();
        }
        if (writing)
        {
            error("startDocument() called twice");
        }
        else
        {
            writing = true;
            seenElement = false;
            if (!excludeXMLDecl)
            {
                write("<?xml version=\"1.0\"");
                if (encoding != null)
                {
                    write(" encoding=\"");
                    write(encoding);
                    write('"');
                }
                write("?>\n\n");
            }
        }
    }


    /**
     * End an XML document.
     *
     * The application must invoke this method if it wishes to reuse
     * the XMLWriter object.  This method also checks that all
     * open elements are closed, and prints a final newline.
     *
     * @exception SAXException If there are open elements remaining, or
     *            if there is not a document currently being written.
     */
    public void endDocument()
            throws SAXException
    {
        // If there is a tag that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
            writeTag(emptyElementTag, emptyElementAttributes, false);

        checkWriting("endDocument");

        if (!elementStack.empty())
        {
            error("end of document while element \"" +
                    elementStack.pop() +
                    "\" not closed");
        }
        else
        {
            if (newlineAtEndOfDocument)
                write("\n");
            writing = false;
        }
    }


    /**
     * Write some character data, escaping as necessary.
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception org.xml.sax.SAXException If there is not a document
     *            currently being written, or if the document element
     *            is not currently open.
     */
    public void characters(char ch[], int start, int length)
            throws SAXException
    {
        // If there is a tag that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
            writeTag(emptyElementTag, emptyElementAttributes, false);

        // Basic well-formedness checking.
        checkWriting("characters");
        if (elementStack.empty())
        {
            error("characters outside of document element");
        }

        // Write each of the characters,
        // escaping if necessary.

        // TODO: this may be inefficient;
        // it might be better to scan and
        // write large chunks that don't
        // need escaping.
        writeChars(ch, start, length, false);
    }


    /**
     * Write whitespace in element content.
     *
     * This method directly invokes characters().
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception org.xml.sax.SAXException If there is not a document
     *            currently being written, or if the document element
     *            is not currently open.
     */
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException
    {
        // If ignorable whitespace is not to be written, just return.
        if (noIgnorableWhitespace)
            return;

        // If there is a tag that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
            writeTag(emptyElementTag, emptyElementAttributes, false);

        // Minimal well-formedness checking.
        checkWriting("ignorableWhitespace");

        // Invoke the characters() method;
        // we don't care about the distinction
        // here.
        characters(ch, start, length);
    }


    /**
     * Write a processing instruction.
     *
     * If the processing instruction is not within the document
     * element, add a newline after it for readability.
     */
    public void processingInstruction(String target, String data)
            throws SAXException
    {
        // If there is a tag that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
            writeTag(emptyElementTag, emptyElementAttributes, false);

        // Minimal well-formedness checking
        // (the PI must appear in a document).
        checkWriting("processingInstruction");
        // Skip PIs in level 3 browser
        // compatibility mode.
        if (level3Compatibility)
        {
            return;
        }
        // Write the processing instruction.
        write("<?");
        write(target);
        if (data != null && !data.equals(""))
        {
            write(' ');
            write(data);
        }

        // Add a newline outside of the
        // document element.
        if (elementStack.empty())
        {
            write("?>\n");
        }
        else
        {
            write("?>");
        }
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     *
     * <p>The information from this event is not necessary for
     * normal Namespace processing: the SAX XML reader will
     * automatically replace prefixes for element and attribute
     * names when the <code>http://xml.org/sax/features/namespaces</code>
     * feature is <var>true</var> (the default).</p>
     *
     * <p>There are cases, however, when applications need to
     * use prefixes in character data or in attribute values,
     * where they cannot safely be expanded automatically; the
     * start/endPrefixMapping event supplies the information
     * to the application to expand prefixes in those contexts
     * itself, if necessary.</p>
     *
     * <p>Note that start/endPrefixMapping events are not
     * guaranteed to be properly nested relative to each-other:
     * all startPrefixMapping events will occur before the
     * corresponding {@link #startElement startElement} event,
     * and all {@link #endPrefixMapping endPrefixMapping}
     * events will occur after the corresponding {@link #endElement
     * endElement} event, but their order is not otherwise
     * guaranteed.</p>
     *
     * <p>There should never be start/endPrefixMapping events for the
     * "xml" prefix, since it is predeclared and immutable.</p>
     *
     * @param prefix The Namespace prefix being declared.
     * @param uri The Namespace URI the prefix is mapped to.
     * @exception SAXException The client may throw
     *            an exception during processing.
     * @see #endPrefixMapping
     * @see #startElement
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException
    {
    }

    /**
     * End the scope of a prefix-URI mapping.
     *
     * <p>See {@link #startPrefixMapping startPrefixMapping} for
     * details.  This event will always occur after the corresponding
     * {@link #endElement endElement} event, but the order of
     * {@link #endPrefixMapping endPrefixMapping} events is not otherwise
     * guaranteed.</p>
     *
     * @param prefix The prefix that was being mapping.
     * @exception SAXException The client may throw
     *            an exception during processing.
     * @see #startPrefixMapping
     * @see #endElement
     */
    public void endPrefixMapping(String prefix)
            throws SAXException
    {
    }

    /**
     * Receive notification of the beginning of an element.
     *
     * <p>The Parser will invoke this method at the beginning of every
     * element in the XML document; there will be a corresponding
     * {@link #endElement endElement} event for every startElement event
     * (even when the element is empty). All of the element's content will be
     * reported, in order, before the corresponding endElement
     * event.</p>
     *
     * <p>This event allows up to three name components for each
     * element:</p>
     *
     * <ol>
     * <li>the Namespace URI;</li>
     * <li>the local name; and</li>
     * <li>the qualified (prefixed) name.</li>
     * </ol>
     *
     * <p>Any or all of these may be provided, depending on the
     * values of the <var>http://xml.org/sax/features/namespaces</var>
     * and the <var>http://xml.org/sax/features/namespace-prefixes</var>
     * properties:</p>
     *
     * <ul>
     * <li>the Namespace URI and local name are required when
     * the namespaces property is <var>true</var> (the default), and are
     * optional when the namespaces property is <var>false</var> (if one is
     * specified, both must be);</li>
     * <li>the qualified name is required when the namespace-prefixes property
     * is <var>true</var>, and is optional when the namespace-prefixes property
     * is <var>false</var> (the default).</li>
     * </ul>
     *
     * <p>Note that the attribute list provided will contain only
     * attributes with explicit values (specified or defaulted):
     * #IMPLIED attributes will be omitted.  The attribute list
     * will contain attributes used for Namespace declarations
     * (xmlns* attributes) only if the
     * <code>http://xml.org/sax/features/namespace-prefixes</code>
     * property is true (it is false by default, and support for a
     * true value is optional).</p>
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param atts The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #endElement
     * @see Attributes
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
            throws SAXException
    {
        // If there is a tag that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
            writeTag(emptyElementTag, emptyElementAttributes, false);

        // Do some basic well-formedness checking,
        // and optionally generate a document
        // type declaration.
        checkWriting("startElement");

        String name = qName;
        if (elementStack.empty())
        {
            if (seenElement)
            {
                error("XML allows only one root element in each document.");
            }
            else if (includeDoctype)
            {
                writeDoctype(name);
            }
        }
        seenElement = true;
        elementStack.push(name);

        emptyElementTag = name;
        emptyElementAttributes = atts;

        // If USE_EMPTY_TAGS is not selected, write out the start tag.
        if (!useEmptyTags)
            writeTag(name, atts, false);

    }

    /**
     * Receive notification of the end of an element.
     *
     * <p>The SAX parser will invoke this method at the end of every
     * element in the XML document; there will be a corresponding
     * {@link #startElement startElement} event for every endElement
     * event (even when the element is empty).</p>
     *
     * <p>For information on the names, see startElement.</p>
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified XML 1.0 name (with prefix), or the
     *        empty string if qualified names are not available.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public void endElement(String namespaceURI, String localName,
                           String qName)
            throws SAXException
    {
        // Basic well-formedness checking.
        checkWriting("endElement");
        String current = (String) elementStack.pop();
        String name = qName;
        if (!name.equals(current))
        {
            error("Mismatched end tag \"" +
                    name +
                    "\" (expected \"" +
                    current +
                    '"');
        }
        // If there is an empty element that is not written, write it!
        if (useEmptyTags && emptyElementTag != null)
        {
            if (!emptyElementTag.equals(name))
                throw new SAXException("Start of empty tag " + emptyElementTag + " didn't mach the end tag " + name);

            writeTag(emptyElementTag, emptyElementAttributes, true);
            return;
        }

        // Write the end tag.
        write("</");
        write(name);
        if (addNewlines)
        {
            write("\n>");
        }
        else
        {
            write('>');
        }
    }

    /**
     * Receive notification of a skipped entity.
     *
     * <p>The Parser will invoke this method once for each entity
     * skipped.  Non-validating processors may skip entities if they
     * have not seen the declarations (because, for example, the
     * entity was declared in an external DTD subset).  All processors
     * may skip external entities, depending on the values of the
     * <code>http://xml.org/sax/features/external-general-entities</code>
     * and the
     * <code>http://xml.org/sax/features/external-parameter-entities</code>
     * properties.</p>
     *
     * @param name The name of the skipped entity.  If it is a
     *        parameter entity, the name will begin with '%', and if
     *        it is the external DTD subset, it will be the string
     *        "[dtd]".
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    public void skippedEntity(String name)
            throws SAXException
    {
    }

    ////////////////////////////////////////////////////////////////////
    // Extended IO methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Write a single character, trapping IOException.
     *
     * Write a single character; trap any IOExceptions and wrap
     * them in SAXException.
     *
     * @param c The character to write.
     * @exception org.xml.sax.SAXException A wrapper around an
     *            IOException, if one occurs.
     * @see java.io.IOException
     */
    private void write(int c)
            throws SAXException
    {
        try
        {
            output.write(c);
        }
        catch (IOException e)
        {
            writing = false;
            throw new SAXException(e);
        }
    }


    /**
     * Write a string, trapping IOException.
     *
     * Write a string; trap any IOExceptions and wrap
     * them in SAXException.
     *
     * @param s The string to write.
     * @exception org.xml.sax.SAXException A wrapper around an
     *            IOException, if one occurs.
     * @see java.io.IOException
     */
    private void write(String s)
            throws SAXException
    {
        try
        {
            output.write(s);
        }
        catch (IOException e)
        {
            writing = false;
            throw new SAXException(e);
        }
    }


    /**
     * Write an array of characters, with XML escaping.
     *
     * Trap any IOException and wrap it as a SAXException.
     *
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to write.
     * @param isLiteral Is this for a literal value?
     * @exception org.xml.sax.SAXException A wrapper around an
     *            IOException, if one occurs.
     */
    private void writeChars(char ch[], int start, int length,
                            boolean isLiteral)
            throws SAXException
    {
        try
        {
            for (int i = start; i < start + length; i++)
            {
                switch (ch[i])
                {
                    case '&':
                        output.write("&amp;");
                        break;
                    case '<':
                        output.write("&lt;");
                        break;
                    case '>':
                        output.write("&gt;");
                        break;
                    case '"':
                        if (isLiteral)
                        {
                            output.write("&quot;");
                            break;
                        }
                        // else fall through...
                    default:
                        if (escapeNonAscii && ch[i] > 127)
                        {
                            output.write("&#");
                            output.write(new Integer(ch[i]).toString());
                            output.write(';');
                        }
                        else
                        {
                            output.write(ch[i]);
                        }
                }
            }
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }
    }


    /**
     * Write a document type declaration.
     */
    private void writeDoctype(String name)
            throws SAXException
    {
        write("<!DOCTYPE ");
        write(name);
        if (declarations.size() > 0)
        {
            write(" [");
            for (Iterator iterator = declarations.iterator(); iterator.hasNext();)
            {
                write("\n  ");
                write(iterator.next().toString());
            }
            write("\n]");
        }
        write(">\n\n");
    }


    /**
     * Display a start tag or empty element tag.
     *
     * @param name The element name.
     * @param atts The attribute list.
     * @param isEmpty true if this is an empty element tag.
     * @exception org.xml.sax.SAXException If there is a problem
     *            writing the tag.
     */
    private void writeTag(String name, Attributes atts, boolean isEmpty)
            throws SAXException
    {
        String delim;

        if (isEmpty)
        {
            delim = "/>";
        }
        else
        {
            delim = ">";
        }

        // Write the beginning of the tag.
        write('<');
        write(name);

        // Write the attributes
        writeAttributes(atts);

        // Finish the start tag.
        if (addNewlines)
        {
            write('\n');
        }
        else if (isEmpty && level3Compatibility)
        {
            write(' ');
        }
        write(delim);
        emptyElementTag = null;
        emptyElementAttributes = null;
    }


    /**
     * Display a list of attributes.
     *
     * @param atts The attribute list.
     */
    private void writeAttributes(Attributes atts)
            throws SAXException
    {
        int length = atts.getLength();
        if (length == 0)
        {
            return;
        }

        if (unsortedAttributes)
        {
            // Write the atts unsorted.
            for (int i = 0; i < atts.getLength(); i++)
            {
                writeAttribute(atts.getQName(i), atts.getValue(i));
            }

        }
        else
        {
/* Hmmm... this implementation is a bit odd [jsd].

            // Write the atts sorted.  First,
            // populate an array of indices.
            int indices[] = new int[length];
            for (int i = 0; i < length; i++)
            {
                indices[i] = i;
            }

            // Insertion sort stolen from James
            // Clark's XMLTest.
            for (int i = 1; i < length; i++)
            {
                int n = indices[i];
                String s = atts.getQName(n);
                int j;
                for (j = i - 1; j >= 0; j--)
                {
                    if (s.compareTo(atts.getQName(indices[j])) >= 0)
                        break;
                    indices[j + 1] = indices[j];
                }
                indices[j + 1] = n;
            }
*/

            // Create an array of the QNames.
            String[] qnames = new String[length];
            for (int i = 0; i < qnames.length; i++)
                qnames[i] = atts.getQName(i);

            // Sort the qnames.
            Arrays.sort(qnames);

            // Now, display the sorted atts.
            for (int i = 0; i < length; i++)
            {
                writeAttribute(qnames[i],
                        atts.getValue(qnames[i]));
            }
        }
    }


    /**
     * Write a single attribute.
     */
    private void writeAttribute(String name, String value)
            throws SAXException
    {
        if (addNewlines)
        {
            write('\n');
        }
        else
        {
            write(' ');
        }
        write(name);
        write("=\"");
        char ch[] = value.toCharArray();
        writeChars(ch, 0, ch.length, true);
        write('"');
    }


    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Throw an exception if the document has not started.
     *
     * @param name The name of the method calling this one.
     * @exception org.xml.sax.SAXException When startDocument()
     *            has not yet been called, or endDocument()
     *            was called more recently.
     */
    private void checkWriting(String name)
            throws SAXException
    {
        if (!writing)
        {
            error("Must call startDocument() before " + name + "()");
        }
    }


    /**
     * Throw an exception, using a locator if available.
     *
     * If there is a locator available, this method throws a
     * SAXParseException; otherwise, it throws a SAXException.
     *
     * @param message The error message.
     * @exception org.xml.sax.SAXException This method always
     *            throws an exception.
     */
    private void error(String message)
            throws SAXException
    {
        writing = false;
        if (locator != null)
        {
            throw new SAXParseException(message, locator);
        }
        else
        {
            throw new SAXException(message);
        }
    }

    /**
     * Virtual constructor.
     * <i>I guess this guy doesn't know that that doesn't make sense! [jsd]</i>
     *
     * @param output The output writer.
     * @param encoding The output encoding, or null if none.
     */
    private void init(Writer output, String encoding)
    {
        this.output = output;
        this.encoding = encoding;
    }
}

// end of XMLWriter.java

