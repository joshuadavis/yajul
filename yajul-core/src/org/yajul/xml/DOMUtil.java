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

package org.yajul.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.yajul.util.StringUtil;

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
     * @param text The text that will be inside the new child.  Note: If 'text' is null, a
     * zero length string will be used in order to avoid NPE's (in XALAN2) later on.
     * @return Element - The new child element.
     */
    public static final Element addChildWithText(Document document,
                                                 Element parent,
                                                 String childTag,
                                                 String text)
    {
        Element child = addChild(document, parent, childTag);
        Text t = document.createTextNode(text == null ? "" : text);
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

    /**
     * Returns the names of all the attributes in the element as an array of strings.
     * @param elem The element.
     * @return the names of all the attributes in the element as an array of strings.
     */
    public static final String[] getAttributeNames(Element elem)
    {
        NamedNodeMap map = elem.getAttributes();
        String[] names = new String[map.getLength()];
        for (int i = 0; i < names.length; i++)
            names[i] = ((Attr)map.item(i)).getName();
        return names;
    }

    /**
     * Returns a map of all the attributes in the element.  The keys will be the attribute names and
     * the values will be the attribute values.
     * @param elem The element.
     * @return a map of all the attributes in the element.  The keys will be the attribute names and
     * the values will be the attribute values.
     */
    public static final Map getAttributeMap(Element elem)
    {
        NamedNodeMap nodeMap = elem.getAttributes();
        Map map = new HashMap(nodeMap.getLength());
        for (int i = 0; i < nodeMap.getLength(); i++)
        {
            Attr attr = ((Attr)nodeMap.item(i));
            map.put(attr.getName(),attr.getValue());
        }
        return map;
    }

    /**
     * Returns the value of the specified attribute as a boolean primitive.
     * @param elem The element.
     * @param attributeName The attribute name in the element.
     * @param defaultValue The default value.
     * @return the value of the specified attribute as a boolean primitive.
     */
    public static final boolean getBooleanAttribute(Element elem, String attributeName,boolean defaultValue)
    {
        String str = elem.getAttribute(attributeName);
        boolean val = defaultValue;
        if (!StringUtil.isEmpty(str))
            val = "true".equals(str);
        return val;
    }


    /**
     * Returns the value of the specified attribute as an int primitive.
     * @param elem The element.
     * @param attributeName The attribute name in the element.
     * @param defaultValue The default value.
     * @return the value of the specified attribute as an int primitive.
     */
    public static final int getIntAttribute(Element elem, String attributeName,int defaultValue)
    {
        String str = elem.getAttribute(attributeName);
        int val = defaultValue;
        if (!StringUtil.isEmpty(str))
            val = Integer.parseInt(str);
        return val;
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
     * @param fileName The name of the file to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP
     * implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the
     * input stream.
     */
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

    /**
     * Parse an XML file.
     * @param file The file to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP
     * implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the
     * input stream.
     */
    public static final Document parseFile(File file)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException
    {
        return parse(
                new java.io.BufferedInputStream(
                        new java.io.FileInputStream(
                                file)));
    }

    /**
     * Parse an XML resource.
     * @param resourceName The resource to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException If the JAXP
     * implementation is configured incorrectly
     * @throws org.xml.sax.SAXException If the document could not be parsed
     * @throws java.io.IOException If there was something wrong with the
     * input stream.
     */
    public static final Document parseResource(String resourceName)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null)
            throw new java.io.IOException("Resource not found: '" + resourceName + "'");
        return parse(is);
    }

    /**
     * Parse the input with the specified reader, producing a DOM Document.
     * @param reader The reader, which will produce SAX2 events.
     * @param input The input source
     * @return The DOM document.
     * @throws TransformerConfigurationException if the transformer doesn't support this operation.
     * @throws IOException if the input cannot be read
     * @throws SAXException if the input cannot be parsed
     */
    public static final Document parse(XMLReader reader,InputSource input) throws TransformerConfigurationException, IOException, SAXException
    {
        // Use the transformer factory to create a content handler that will build a DOM.
        TransformerFactory factory = TransformerFactory.newInstance();
        if (!factory.getFeature(SAXTransformerFactory.FEATURE))
            throw new TransformerConfigurationException("The transformer factory does not support SAX transformation!");
        TransformerHandler handler = ((SAXTransformerFactory) factory).newTransformerHandler();
        // Create a DOM result for the transformation.
        DOMResult domResult = new DOMResult();
        handler.setResult(domResult);
        // Register the content handler with the reader, and parse the input.
        reader.setContentHandler(handler);
        reader.parse(input);
        // Return the resulting document.
        return (Document)domResult.getNode();
    }

}