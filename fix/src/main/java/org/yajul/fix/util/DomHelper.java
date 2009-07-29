package org.yajul.fix.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DOM helper methods (copied from YAJUL).
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 10:02:03 AM
 */
public class DomHelper {
    /**
     * The default URI used for parsing XML documents. *
     */
    private static final String DEFAULT_URI = "file://";

    /**
     * Creates a new DOM document with the specified root element
     * as the 'document element'.
     *
     * @param rootElementTag The tag name for the root element.
     * @return Document - The new document.
     * @throws javax.xml.parsers.ParserConfigurationException
     *          - If the document could not
     *          be created.
     */
    public static Document createDocument(String rootElementTag)
            throws ParserConfigurationException {
        Document document = createDocument();
        Element root = document.createElement(rootElementTag);
        document.appendChild(root);
        return document;
    }

    /**
     * Creates a new DOM document with no root element.
     *
     * @return Document - The new document.
     * @throws ParserConfigurationException - If the document could not
     *                                      be created.
     */
    public static Document createDocument()
            throws ParserConfigurationException {
        DocumentBuilder builder = getDocumentBuilder();
        return builder.newDocument();
    }

    /**
     * Adds a child element to the specified parent element.
     *
     * @param document The document that contains the parent.
     * @param parent   The parent element.
     * @param childTag The tag name for the new child.
     * @return Element - The new child element.
     */
    public static Element addChild(Document document,
                                   Element parent, String childTag) {
        Element child = document.createElement(childTag);
        parent.appendChild(child);
        return child;
    }

    /**
     * Adds a child element to the specified parent element containing
     * some text.
     *
     * @param document The document that contains the parent.
     * @param parent   The parent element.
     * @param childTag The tag name for the new child.
     * @param text     The text that will be inside the new child.  Note: If 'text' is null, a
     *                 zero length string will be used in order to avoid NPE's (in XALAN2) later on.
     * @return Element - The new child element.
     */
    public static Element addChildWithText(Document document,
                                           Element parent,
                                           String childTag,
                                           String text) {
        Element child = addChild(document, parent, childTag);
        Text t = document.createTextNode(text == null ? "" : text);
        child.appendChild(t);
        return child;
    }

    /**
     * Returns a new document builder.
     *
     * @return DocumentBuilder - A new document builder.
     * @throws ParserConfigurationException - If the JAXP
     *                                      implementation is configured incorrectly
     */
    public static DocumentBuilder getDocumentBuilder()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

    // --- Navigation methods ---

    /**
     * Returns an array of elements, given a node list.
     *
     * @param nodeList The node list.
     * @return a list of elements
     */
    public static List<Element> toElementList(NodeList nodeList) {
        int size = nodeList.getLength();
        ArrayList<Element> list = new ArrayList<Element>(size);
        Node n;
        for (int i = 0; i < size; i++) {
            n = nodeList.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            list.add((Element) n);
        }
        return list;
    }

    /**
     * Returns the list of child elements in a parent element
     *
     * @param parent The parent element.
     * @return the list of elements in the parent
     */
    public static List<Element> getChildElements(Element parent) {
        NodeList children = parent.getChildNodes();
        return toElementList(children);
    }

    /**
     * Returns a list of child elements.
     *
     * @param parent The parent document.
     * @return the list of elements in the parent
     */
    public static List<Element> getChildElements(Document parent) {
        return getChildElements(parent.getDocumentElement());
    }

    /**
     * Returns a list of ALL child elements in this element (either directly
     * or inside a descendant) with the specified tag name.
     *
     * @param parent The parent element.
     * @param tag    The child element tag name.
     * @return The list of ALL elements in parent that have the
     *         specified tag name.
     */
    public static List<Element> getChildElements(Element parent, String tag) {
        NodeList children = parent.getElementsByTagName(tag);
        return toElementList(children);
    }

    /**
     * Returns an array of child elements with the specified tag name.
     *
     * @param parent The parent element.
     * @param tag    - The child element tag name.
     * @return The list of ALL elements in parent that have the
     *         specified tag name.
     */
    public static List<Element> getChildElements(Document parent, String tag) {
        return getChildElements(parent.getDocumentElement(), tag);
    }

    /**
     * Returns the text inside an element as a string.  If there are multiple
     * text nodes, they will be concantenated.
     *
     * @param element The element containing the text.
     * @return String - The text in the element.
     */
    public static String getChildText(Element element) {
        // Iterate through the children and append text nodes
        // to the StringBuffer.
        StringBuffer buf = new StringBuffer();
        Node n = element.getFirstChild();
        Text t;
        while (n != null) {
            if (n.getNodeType() == Node.TEXT_NODE) {
                t = (Text) n;
                buf.append(t.getData());
            }
            n = n.getNextSibling();
        } // for
        return buf.toString();
    }

    /**
     * Returns the names of all the attributes in the element as an array of strings.
     *
     * @param elem The element.
     * @return the names of all the attributes in the element as an array of strings.
     */
    public static List<String> getAttributeNames(Element elem) {
        NamedNodeMap map = elem.getAttributes();
        ArrayList<String> names = new ArrayList<String>(map.getLength());
        for (int i = 0; i < map.getLength(); i++)
            names.add(((Attr) map.item(i)).getName());
        return names;
    }

    /**
     * Returns a map of all the attributes in the element.  The keys will be the attribute names and
     * the values will be the attribute values.
     *
     * @param elem The element.
     * @return a map of all the attributes in the element.  The keys will be the attribute names and
     *         the values will be the attribute values.
     */
    public static Map<String, String> getAttributeMap(Node elem) {
        NamedNodeMap nodeMap = elem.getAttributes();
        Map<String, String> map = new HashMap<String, String>(nodeMap.getLength());
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Attr attr = ((Attr) nodeMap.item(i));
            map.put(attr.getName(), attr.getValue());
        }
        return map;
    }

    public static String getAttribute(Node node, String name) {
        return getAttribute(node, name, null);
    }

    public static String getAttribute(Node node, String name, String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node namedItem = attributes.getNamedItem(name);
            return namedItem != null ? namedItem.getNodeValue() : null;
        }
        return defaultValue;
    }

    /**
     * Returns the value of the specified attribute as a boolean primitive.
     *
     * @param elem          The element.
     * @param attributeName The attribute name in the element.
     * @param defaultValue  The default value.
     * @return the value of the specified attribute as a boolean primitive.
     */
    public static boolean getBooleanAttribute(Node elem, String attributeName, boolean defaultValue) {
        String str = getAttribute(elem,attributeName);
        boolean val = defaultValue;
        if (str != null)
            val = "true".equals(str);
        return val;
    }


    /**
     * Returns the value of the specified attribute as an int primitive.
     *
     * @param elem          The element.
     * @param attributeName The attribute name in the element.
     * @param defaultValue  The default value.
     * @return the value of the specified attribute as an int primitive.
     */
    public static int getIntAttribute(Node elem, String attributeName, int defaultValue) {
        String str = getAttribute(elem,attributeName);
        int val = defaultValue;
        if (str != null && str.length() > 0)
            val = Integer.parseInt(str);
        return val;
    }

    // --- Parsing methods ---

    /**
     * Parses the input stream and returns a DOM document.
     *
     * @param input - The input stream to parse.
     * @return Document - The DOM Document.
     * @throws javax.xml.parsers.ParserConfigurationException
     *                                  - If the JAXP
     *                                  implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException      - If there was something wrong with the
     *                                  input stream.
     */
    public static Document parse(InputStream input)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException, java.io.IOException {
        DocumentBuilder builder = getDocumentBuilder();
        return builder.parse(input, DEFAULT_URI);
    }

    /**
     * Parse a file using it's name.
     *
     * @param fileName The name of the file to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException
     *                                  - If the JAXP
     *                                  implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException      - If there was something wrong with the
     *                                  input stream.
     */
    public static Document parseFile(String fileName)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException {
        return parse(
                new java.io.BufferedInputStream(
                        new java.io.FileInputStream(
                                fileName)));
    }

    /**
     * Parse an XML file.
     *
     * @param file The file to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException
     *                                  - If the JAXP
     *                                  implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException      - If there was something wrong with the
     *                                  input stream.
     */
    public static Document parseFile(File file)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException {
        return parse(
                new java.io.BufferedInputStream(
                        new java.io.FileInputStream(
                                file)));
    }

    /**
     * Parse an XML resource.
     *
     * @param resourceName The resource to parse.
     * @return A DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException
     *                                  If the JAXP
     *                                  implementation is configured incorrectly
     * @throws org.xml.sax.SAXException If the document could not be parsed
     * @throws java.io.IOException      If there was something wrong with the
     *                                  input stream.
     */
    public static Document parseResource(String resourceName)
            throws javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null)
            throw new java.io.IOException("Resource not found: '" + resourceName + "'");
        return parse(is);
    }
}
