package org.yajul.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Provides commonly used DOM operations in convenient methods.
 * Good for replacing many lines of DOM code with a nice one-liner.
 * Works with any JAXP 1.1 implementation (e.g. XERCES/XALAN).
 * @author Joshua Davis
 */
public class DOMUtil
{
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
    public static final Document createDocument() throws ParserConfigurationException
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
                                   Element parent,String childTag)
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
     * @return Element - The new child element.
     */
    public static final Element addChildWithText(Document document,
                                   Element parent,String childTag,
                                   String text)
    {
        Element child = addChild(document,parent,childTag);
        Text t = document.createTextNode(text);
        child.appendChild(t);
        return child;
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }
}
