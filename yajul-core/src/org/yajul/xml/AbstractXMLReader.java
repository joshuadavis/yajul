package org.yajul.xml;

import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A partial implementation of XMLReader, which allows subclasses to implement only the
 * parse(InputSource) method.
 * User: jdavis
 * Date: Mar 11, 2004
 * Time: 7:40:48 PM
 * @author jdavis
 */
abstract public class AbstractXMLReader implements XMLReader
{
    private Map featureMap = new HashMap();
    private Map propertyMap = new HashMap();
    private EntityResolver entityResolver;
    private DTDHandler dtdHandler;
    private ContentHandler contentHandler;
    private ErrorHandler errorHandler;

    /**
     * Parse the input source and send SAX2 events to the ContentHandler.
     */
    public abstract void parse(InputSource input)
            throws IOException,
            SAXException;

    public boolean getFeature(String name)
            throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        Boolean featureValue = (Boolean) this.featureMap.get(name);
        return (featureValue == null) ? false
                : featureValue.booleanValue();
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        this.featureMap.put(name, new Boolean(value));
    }

    public Object getProperty(String name)
            throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        return this.propertyMap.get(name);
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException,
            SAXNotSupportedException
    {
        this.propertyMap.put(name, value);
    }

    public void setEntityResolver(EntityResolver entityResolver)
    {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver()
    {
        return this.entityResolver;
    }

    public void setDTDHandler(DTDHandler dtdHandler)
    {
        this.dtdHandler = dtdHandler;
    }

    public DTDHandler getDTDHandler()
    {
        return this.dtdHandler;
    }

    public void setContentHandler(ContentHandler contentHandler)
    {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler()
    {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler)
    {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler()
    {
        return this.errorHandler;
    }

    public void parse(String systemId) throws IOException,
            SAXException
    {
        parse(new InputSource(systemId));
    }

    /**
     * Returns a buffered reader for the given input source.
     * @param input The input source.
     * @return a buffered reader for the given input source
     * @throws IOException if the input cannot be read
     * @throws SAXException if the InputSource is of an unexpected type.
     */
    protected BufferedReader getBufferedReader(InputSource input) throws IOException, SAXException
    {
        BufferedReader br = null;
        if (input.getCharacterStream() != null)
        {
            br = new BufferedReader(input.getCharacterStream());
        }
        else if (input.getByteStream() != null)
        {
            br = new BufferedReader(new InputStreamReader(
                    input.getByteStream()));
        }
        else if (input.getSystemId() != null)
        {
            URL url = new URL(input.getSystemId());
            br = new BufferedReader(
                    new InputStreamReader(url.openStream()));
        }
        else
        {
            throw new SAXException("Invalid InputSource!");
        }
        return br;
    }
}
