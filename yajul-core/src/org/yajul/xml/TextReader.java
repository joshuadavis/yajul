package org.yajul.xml;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.BufferedReader;

/**
 * TODO: Add javadoc
 * User: jdavis
 * Date: Mar 16, 2004
 * Time: 5:07:35 PM
 * @author jdavis
 */
public abstract class TextReader extends AbstractXMLReader
{
    public static final String DEFAULT_FILE_ELEMENT = "file";
    public static final String DEFAULT_LINE_ELEMENT = "line";

    protected static final Attributes EMPTY_ATTR =
            new AttributesImpl();

    private String fileElement = DEFAULT_FILE_ELEMENT;
    private String valueElement = DEFAULT_VALUE_ELEMENT;
    private String lineElement = DEFAULT_LINE_ELEMENT;

    public static final String DEFAULT_VALUE_ELEMENT = "value";

    private int lineCounter;

    /**
     * Parse the input source and send SAX2 events to the ContentHandler.
     * @param input The CSV formatted input
     */
    public void parse(
            InputSource input) throws IOException,
            SAXException
    {
        if (getContentHandler() == null)
            return;

        BufferedReader br = getBufferedReader(input);
        getContentHandler().startDocument();

        getContentHandler().startElement("", "", fileElement, EMPTY_ATTR);
        String curLine = null;
        lineCounter = 0;
        while ((curLine = br.readLine()) != null)
        {
            curLine = prepareLine(curLine);
            if (curLine.length() > 0)
            {
                getContentHandler().startElement("", "", lineElement, EMPTY_ATTR);
                parseLine(curLine, getContentHandler());
                getContentHandler().endElement("", "", lineElement);
            }
            lineCounter++;
        }

        getContentHandler().endElement("", "", fileElement);
        getContentHandler().endDocument();
    }

    protected String prepareLine(String curLine)
    {
        return curLine.trim();
    }

    public String getFileElement()
    {
        return fileElement;
    }

    public void setFileElement(String fileElement)
    {
        this.fileElement = fileElement;
    }

    public String getLineElement()
    {
        return lineElement;
    }

    public void setLineElement(String lineElement)
    {
        this.lineElement = lineElement;
    }

    public String getValueElement()
    {
        return valueElement;
    }

    public void setValueElement(String valueElement)
    {
        this.valueElement = valueElement;
    }

    protected int getLineCounter()
    {
        return lineCounter;
    }

    protected void emitTag(ContentHandler ch, String tagName, String value) throws SAXException
    {
        ch.startElement("", "", tagName, EMPTY_ATTR);
        ch.characters(value.toCharArray(), 0,
                value.length());
        ch.endElement("", "", tagName);
    }

    protected abstract void parseLine(String curLine, ContentHandler ch)
            throws IOException, SAXException;
}
