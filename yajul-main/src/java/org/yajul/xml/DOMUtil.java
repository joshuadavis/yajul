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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Provides commonly used DOM operations in convenient methods.
 * Good for replacing many lines of DOM code with a nice one-liner.
 * Works with any JAXP 1.1 implementation (e.g. XERCES/XALAN).
 * @author Joshua Davis
 */
public class DOMUtil
{
    /** The default URI used for parsing XML documents. **/
    private static final String DEFAULT_URI = "file://";

    /**
     * Creates a new DOM document with the specified root element
     * as the 'document element'.
     * @param rootElementTag The tag name for the root element.
     * @return Document - The new document.
     * @throws ParserConfigurationException - If the document could not
     * be created.
     */
    public static final Document createDocument(String rootElementTag)
            throws ParserConfigurationException
    {
        Document document = createDocument();
        Element root = document.createElement(rootElementTag);
        document.appendChild(root);
        return document;
    }

    /**
     * Creates a new DOM document with no root element.
     * @return Document - The new document.
     * @throws ParserConfigurationException - If the document could not
     * be created.
     */
    public static final Document createDocument()
            throws ParserConfigurationException
    {
        DocumentBuilder builder = getDocumentBuilder();
        Document document = builder.newDocument();
        return document;
    }

    /**
     * Adds a child element to the specified parent element.
     * @param document The document that contains the parent.
     * @param parent The parent element.
     * @param childTag The tag name for the new child.
     * @return Element - The new child element.
     */
    public static final Element addChild(Document document,
                                         Element parent, String childTag)
    {
        Element child = document.createElement(childTag);
        parent.appendChild(child);
        return child;
    }

    /**
     * Adds a child element to the specified parent element containing
     * some text.
     * @param document The document that contains the parent.
     * @param parent The parent element.
     * @param childTag The tag name for the new child.
     * @param text The text that will be inside the new child.
     * @return Element - The new child element.
     */
    public static final Element addChildWithText(Document document,
                                                 Element parent,
                                                 String childTag,
                                                 String text)
    {
        Element child = addChild(document, parent, childTag);
        Text t = document.createTextNode(text);
        child.appendChild(t);
        return child;
    }

    /**
     * Returns a new document builder.
     * @return DocumentBuilder - A new document builder.
     * @throws ParserConfigurationException - If the JAXP
     * implementation is configured incorrectly
     */
    public static final DocumentBuilder getDocumentBuilder()
            throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

    // --- Navigation methods ---

    /**
     * Returns an array of elements, given a node list.
     * @param nodeList - The node list.
     * @return Element[] - The array of elements in the node list.
     **/
    public static final Element[] toElementArray(NodeList nodeList)
    {
        int size = nodeList.getLength();
        ArrayList list = new ArrayList(size);
        Node n = null;
        for (int i = 0; i < size; i++)
        {
            n = nodeList.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            list.add(n);
        }
        Element[] array = new Element[list.size()];
        return (Element[]) list.toArray(array);
    }

    /**
     * Returns an array of child elements.
     * @param parent - The parent element.
     * @return Element[] - The array of elements in parent.
     **/
    public static final Element[] getChildElements(Element parent)
    {
        NodeList children = parent.getChildNodes();
        return toElementArray(children);
    }

    /**
     * Returns an array of child elements.
     * @param parent - The parent document.
     * @return Element[] - The array of elements in parent.
     **/
    public static final Element[] getChildElements(Document parent)
    {
        return getChildElements(parent.getDocumentElement());
    }

    /**
     * Returns an array of ALL child elements in this element (either directly
     * or inside a descendant) with the specified tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element[] - The array of ALL elements in parent that have the
     * specified tag name.
     **/
    public static final Element[] getChildElements(Element parent, String tag)
    {
        NodeList children = parent.getElementsByTagName(tag);
        return toElementArray(children);
    }

    /**
     * Returns an array of child elements with the specified tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element[] - The array of ALL elements in parent that have the
     * specified tag name.
     **/
    public static final Element[] getChildElements(Document parent, String tag)
    {
        return getChildElements(parent.getDocumentElement(), tag);
    }

    /**
     * Returns the text inside an element as a string.  If there are multiple
     * text nodes, they will be concantenated.
     * @param element - The element containing the text.
     * @return String - The text in the element.
     **/
    public static final String getChildText(Element element)
    {
        // Iterate through the children and append text nodes
        // to the StringBuffer.
        StringBuffer buf = new StringBuffer();
        Node n = element.getFirstChild();
        Text t = null;
        while (n != null)
        {
            if (n.getNodeType() == Node.TEXT_NODE)
            {
                t = (Text) n;
                buf.append(t.getData());
            }
            n = n.getNextSibling();
        } // for
        return buf.toString();
    }

    // --- Parsing methods ---

    /**
     * Parses the input stream and returns a DOM document.
     * @param input - The input stream to parse.
     * @return Document - The DOM Document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP
     * implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the
     * input stream.
     **/
    public static final Document parse(InputStream input)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException, java.io.IOException
    {
        DocumentBuilder builder = getDocumentBuilder();
        return builder.parse(input, DEFAULT_URI);
    }

    /**
     * Parse a file using it's name.
     * @param fileName - The name of the file to parse.
     * @return document - The DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP
     * implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the
     * input stream.
     **/
    public static final Document parseFile(String fileName)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException
    {
        return parse(
                new java.io.BufferedInputStream(
                        new java.io.FileInputStream(
                                fileName)));
    }
}
