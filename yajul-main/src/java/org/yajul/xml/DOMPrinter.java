package org.yajul.xml;

import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Provides functions that print DOM objects to streams.  Works with
 * any JAXP1.1 compliant implementation (e.g. XALAN).
 * @author Joshua Davis
 */
public class DOMPrinter
{
    /** Transformer output property value for 'true'. **/
    public static final String TRUE = "yes";
    /** Transformer output property value for 'false'. **/
    public static final String FALSE = "no";

    /**
     * Prints a node (Document or Element) to the stream, without the
     * XML declaration.
     * @param node The new (Document or Element) to print.
     * @param stream The stream to print to.
     * @throws TransformerException - If there was a problem.
     */
    public static final void printNode(
            Node node,
            OutputStream stream) throws TransformerException
    {
        printNode(node, stream, true);
    }

    /**
     * Prints a node (Document or Element) to the stream, without the
     * XML declaration.
     * @param node The new (Document or Element) to print.
     * @param stream The stream to print to.
     * @param omitXMLDeclaration Use true to omit the
     * <code>&gt;?xml version="1.0" ?&lt;</code> declaration.
     * @throws TransformerException - If there was a problem.
     */
    public static final void printNode(
            Node node,
            OutputStream stream,
            boolean omitXMLDeclaration) throws TransformerException
    {
        // Create a default transformer.
        Transformer transformer = createTransformer(omitXMLDeclaration);

        // Do the transform.
        transformer.transform(new DOMSource(node), new StreamResult(stream));
    }

    /**
     * Prints a node (Document or Element) to the writer, without the
     * XML declaration.
     * @param node The new (Document or Element) to print.
     * @param writer The writer to print to.
     * @param omitXMLDeclaration Use true to omit the
     * <code>&gt;?xml version="1.0" ?&lt;</code> declaration.
     * @throws TransformerException - If there was a problem.
     */
    public static final void printNode(
            Node node,
            Writer writer,
            boolean omitXMLDeclaration) throws TransformerException
    {
        // Create a default transformer.
        Transformer transformer = createTransformer(omitXMLDeclaration);

        // Do the transform.
        transformer.transform(new DOMSource(node), new StreamResult(writer));
    }

    // -- Implementation methods --

    private static Transformer createTransformer(boolean omitXMLDeclaration)
            throws TransformerConfigurationException
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        // Set the output properties.
        setOutputProperties(transformer, omitXMLDeclaration);
        return transformer;
    }

    private static final void setOutputProperties(Transformer transformer,
                                                  boolean omitXMLDeclaration)
    {
        // Set the attributes of the transformer.
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, TRUE);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                omitXMLDeclaration ? TRUE : FALSE);
    }
}
