package org.yajul.xml;

// JDK
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Properties;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

// JAXP
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

// DOM
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.DocumentType;

/**
 * Provides utility functions that simplify using the DOM interfaces.
 * <ul>
 * <li>Add attributes, child nodes, and text nodes on JDK objects.</li>
 * <li>Retrieve elements as arrays of elements, which can be simpler than using the NodeList interface.</li>
 * <li>Parse byte arrays and input streams into DOM documents.  Thread safe usage of javax.xml.parsers.DocumentBuilderFactory.</li>
 * <li>Retrieve element text as a string.<li>
 * <li>Print out nodes and documents into byte arrays or streams.</li>
 * </ul>
 *
 * @see org.w3c.dom.Document
 * @see org.w3c.dom.Node
 * @see org.w3c.dom.Text
 * @see org.w3c.dom.Element
 * @see org.w3c.dom.NodeList
 * @see javax.xml.parsers.DocumentBuilder
 * @author Joshua Davis
 **/
public class DOMUtil
{
    /** A static document builder factory for parsing streams into DOM documents. **/
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    /** A shared XSLT transformer factory. */
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /** The default URI used for parsing XML documents. **/
    private static final String DEFAULT_URI = "file://";

    // --- DOM manipulation methods (adding elements) ---

    /**
     * Creates a new element that is a chile of the element passed in.
     * @param parent - The parent element.
     * @param tag - The element's tag name.
     * @return Element - The new child element.
     **/
    public static final Element addChildElement(Element parent,String tag)
    {
        Document doc = parent.getOwnerDocument();
        Element child = doc.createElement(tag);
        parent.appendChild(child);
        return child;
    }

    /**
     * Creates a new text node and adds it as a child of the element.  If 'text' is null, 
     * node with no text will be produced.
     * @param elem - The element to add the text node to.
     * @param text - The text.  If this is null, no text node will be added.
     * @return Text - The new child text element, or null if 'text' is null.
     **/
    public static final Text addText(Element elem,String text)
    {
        Document doc = elem.getOwnerDocument();
        if (text != null)   // If the text string is not null, add the grandchild text node.
        {
            Text tx = doc.createTextNode(text);
            elem.appendChild(tx);
            return tx;
        }
        else
            return null;
    }

    /**
     * Creates a new child element with the specified tag, and a single text
     * node as it's content.
     * @param parent - The parent element.
     * @param tag - The tag of the child element.
     * @param text - The text to place inside the child element.
     * @return Element - The new child element with a text child.
     **/
    public static final Element addChildText(Element parent,String tag,String text)
    {
        Element child = addChildElement(parent,tag);
        addText(child,text);
        return child;
    }

    // --- Retrieval methods ---
    
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
        for(int i = 0; i < size; i++)
        {
            n = nodeList.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            list.add((Element)n);
        }
        Element[] array = new Element[list.size()];
        return (Element[])list.toArray(array);
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
     * Returns an array of ALL child elements in this element (either directly or inside a descendant) with the
     * specified tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element[] - The array of ALL elements in parent that have the specified tag name.
     **/
    public static final Element[] getChildElements(Element parent,String tag)
    {
        NodeList children = parent.getElementsByTagName(tag);
        return toElementArray(children);
    }

    /**
     * Returns an array of child elements with the specified tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element[] - The array of ALL elements in parent that have the specified tag name.
     **/
    public static final Element[] getChildElements(Document parent,String tag)
    {
        return getChildElements(parent.getDocumentElement(),tag);
    }

    /**
     * Returns an array of immediate descendent child elements with the specified tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element[] - The array of immediate descendent elements in parent that have the specified tag name.
     */
    public static final Element[] getImmediateChildElements(Element parent,String tag)
    {
        //NodeList children = parent.getElementsByTagName(tag);
        //return toElementArray(children);

        Element[] allChildren = getChildElements( parent );
        ArrayList matchingChildren = new ArrayList();
        for ( int i = 0; i < allChildren.length; i++ )
        {
            if ( allChildren[i].getTagName().equals( tag ) )
                matchingChildren.add( allChildren[i] );
        }
        return (Element[])matchingChildren.toArray( new Element[ matchingChildren.size() ] );
    }
    

    /**
     * Returns the first child node that is an element.
     * @param n - The parent node.
     * @return Element - The first child that is an Element.
     **/
    public static final Element getFirstChildElement(Node n)
    {
        Node child = n.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE)
            child = child.getNextSibling();
        return (Element)child;
    }

    /**
     * Returns the first child element with the given tag name.
     * @param parent - The parent element.
     * @param tag - The child element tag name.
     * @return Element - The first element in the parent with the specified tag.
     **/
    public static final Element getFirstChildElement(Element parent,String tag)
    {
        NodeList children = parent.getElementsByTagName(tag);
        if (children.getLength() < 1)
            return null;
        Node n = children.item(0);
        if (n.getNodeType() != Node.ELEMENT_NODE)
            throw new Error("Expected element node!");
        return (Element)n;
    }

    /**
     * Returns the text inside an element as a string.  If there are multiple text nodes,
     * they will be concantenated.
     * @param element - The element containing the text.
     * @return String - The text in the element.
     **/
    public static final String getChildText(Element element)
    {
        // Iterate through the children and append text nodes to the StringBuffer.
        StringBuffer buf = new StringBuffer();
        Node n = element.getFirstChild();
        Text t = null;
        while (n != null)          
        {
            if (n.getNodeType() == Node.TEXT_NODE)
            {
                t = (Text)n;
                buf.append(t.getData());
            }
            n = n.getNextSibling();
        } // for
        return buf.toString();        
    }

    /**
     * Converts the child elements into a java.util.Properties.  Each child
     * element is expected to have two attributes that will be used as the name and
     * attribute values.
     * @param parent - The parent element.
     * @param nameAttribute - The name of the attribute that will be used as the 'name' in the Properties.
     * @param valueAttribute - The name of the attribute that will be used as the 'value' in the Properties.
     * @return Properties - The java.util.Properties object.
     */
    public static final Properties toProperties(Element parent,String nameAttribute,String valueAttribute)
    {
        // Iterate through each child node...
        Properties props = new Properties();
        Node n = parent.getFirstChild();
        Element e = null;
        while (n != null)          
        {
            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                e = (Element)n;
                props.put( e.getAttribute(nameAttribute) , e.getAttribute(valueAttribute) );
            }
            n = n.getNextSibling();
        } // for
        return props;
    }
    
    // --- Parsing methods ---
    
    /**
     * Parses the input stream and returns a DOM document.
     * @param input - The input stream to parse.
     * @return Document - The DOM Document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the input stream.
     **/
    public static final Document parse(InputStream input)
        throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
        DocumentBuilder builder;
        // Create the objects necessary for parsing the responses.
        synchronized (factory)  // Serialize the threads on the factory....
        {                       // DocumentBuilderFactory is not thread-safe :( [ew,jsd]
            builder = factory.newDocumentBuilder();
        }                       // ... threads can proceed independently now.
        return  builder.parse(input,DEFAULT_URI);
    }

    /**
     * Parse an array of bytes into a DOM document.
     * @param bytes - The array of bytes.
     * @return document - The DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the input stream.
     **/
    public static final Document parse(byte[] bytes)
        throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
        return parse(new java.io.ByteArrayInputStream(bytes));
    }

    /** 
     * Parse a file using it's name.
     * @param fileName - The name of the file to parse.
     * @return document - The DOM document.
     * @throws javax.xml.parsers.ParserConfigurationException - If the JAXP implementation is configured incorrectly
     * @throws org.xml.sax.SAXException - If the document could not be parsed
     * @throws java.io.IOException - If there was something wrong with the input stream.
     **/
    public static final Document parseFile(String fileName)
        throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
        return parse(
            new java.io.BufferedInputStream(
                new java.io.FileInputStream(
                    fileName)));
    }  
    
    /**
     * Returns a new Document with a root element as the parameter
     * @param rootElementName the name root element of new DOM document
     * @return newly created DOM document with a root element of parameter name
     * @exception ParserConfigurationException if new document could be created
     */
    public static final Document createDocument(String rootElementName)
        throws ParserConfigurationException 
    {
        DocumentBuilder builder;
        synchronized (factory)  // Serialize the threads on the factory....
        {                       // DocumentBuilderFactory is not thread-safe :( [ew,jsd]
            builder = factory.newDocumentBuilder();
        }                       // ... threads can proceed independently now.
        Document document = builder.newDocument() ; 
        Element newElement = document.createElement(rootElementName) ; 
        document.appendChild(newElement) ; 
        return document ; 
    }

    /**
     * Pretty prints a DOM node (Document, Element, or otherwise) into the output stream specified.
     * @param node - The node (or document) to print.
     * @param stream - The output stream to print onto.
     * @param includeXMLDeclaration - Set to true to include the <?XML ... ?> processing directive.
     * @throws IOException - When there is a problem writing to the stream.
     */
    public static final void writeNode(Node node,OutputStream stream,boolean includeXMLDeclaration) throws IOException
    {
        // Just wrap the output stream in a writer.
        writeNode(node,new OutputStreamWriter(stream),includeXMLDeclaration);
    }

    /**
     * Pretty prints a DOM node (Document, Element, or otherwise) into the output stream specified.
     * @param node - The node (or document) to print.
     * @param stream - The output stream to print onto.
     * @param includeXMLDeclaration - Set to true to include the <?XML ... ?> processing directive.
     * @throws IOException - When there is a problem writing to the stream.
     */
    public static final void writeNode(Node node,Writer stream,boolean includeXMLDeclaration) throws IOException
    {
        if (node == null)
            throw new IllegalArgumentException("DOM node cannot be null!");

        try
        {
            // 1) Create a 'default' transformer.
            Transformer transformer = null;

            // TransformerFactory is not known to be thread safe, so this is synchronized, just to be sure. [jsd]
            synchronized(transformerFactory)
            {
                transformer = transformerFactory.newTransformer();
            }

            // 2) Give the default transformer some hints about how to format the resulting document.
            Properties props = transformer.getOutputProperties();
            props.put(OutputKeys.METHOD, "xml");
            props.put(OutputKeys.OMIT_XML_DECLARATION, includeXMLDeclaration ? "true" : "false");
            props.put(OutputKeys.INDENT, "true");
            if ( node.getNodeType() == Node.DOCUMENT_NODE )
            {
                // rseroka - get the <!DOCTYPE ....> to come out
                DocumentType docType = ((Document)node).getDoctype() ;
                if ( docType != null )
                {
                    String publicId = docType.getPublicId() ;
                    String systemId = docType.getSystemId() ;
                    if ( publicId != null )
                        props.put(OutputKeys.DOCTYPE_PUBLIC, publicId) ;
                    if ( systemId != null )
                        props.put(OutputKeys.DOCTYPE_SYSTEM, systemId) ;
                }
            }
            transformer.setOutputProperties(props);

            // 3) Do the transform using a stream result.
            transformer.transform(new DOMSource(node), new StreamResult(stream));
        }
        catch (Exception e)
        {
            // 2002-01-04 [jsd]
            // TODO: Log the error.   Can't do this right now because the component that this
            // code is expected to live in (/vobs/common) does not have any logging facility.  :(
            throw new IOException("Unable to print XML document due to: " + e.getMessage());
        }
    }

    /**
     * Transforms a DOM node (Document, Element, or otherwise), into an array of bytes.
     * @param node - The node (or document) to print.
     * @param includeXMLDeclaration - Set to true to include the <?XML ... ?> processing directive.
     * @return byte[] - The array of bytes (XML stream for the node).
     * @throws IOException - When there is a problem writing to the stream.
     */
    public static final byte[] nodeToByteArray(Node node,boolean includeXMLDeclaration) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeNode(node,baos,true);
        return baos.toByteArray();
    }

    /**
     * Transforms a DOM node (Document, Element, or otherwise), into an input stream.
     * @param node - The node (or document) to print.
     * @param includeXMLDeclaration - Set to true to include the <?XML ... ?> processing directive.
     * @return InputStream - An input stream, with the XML stream in it.
     * @throws IOException - When there is a problem writing to the stream.
     */
    public static final InputStream nodeToInputStream(Node node,boolean includeXMLDeclaration) throws IOException
    {
        // Use a byte array to store the formatted document.  If this proves to be too inefficient, this implementation
        // can be changed later. [jsd]
        byte[] bytes = nodeToByteArray(node,includeXMLDeclaration);
        return new ByteArrayInputStream(bytes);
    }

} // DOMUtil







