package org.yajul.xml;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.BufferedReader;

/**
 * Reads a CSV (comma separated values) file and emits SAX2 events into a
 * ContentHandler.
 * User: jdavis
 * Date: Mar 11, 2004
 * Time: 7:45:11 PM
 * @author jdavis
 */
public class CSVReader extends AbstractXMLReader
{
    private static final Attributes EMPTY_ATTR =
            new AttributesImpl();

    public static final String DEFAULT_FILE_ELEMENT = "file";
    public static final String DEFAULT_LINE_ELEMENT = "line";
    public static final String DEFAULT_VALUE_ELEMENT = "value";

    private String fileElement = DEFAULT_FILE_ELEMENT;
    private String lineElement = DEFAULT_LINE_ELEMENT;
    private String valueElement = DEFAULT_VALUE_ELEMENT;

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
        while ((curLine = br.readLine()) != null)
        {
            curLine = curLine.trim();
            if (curLine.length() > 0)
            {
                getContentHandler().startElement("", "", lineElement, EMPTY_ATTR);
                parseLine(curLine, getContentHandler());
                getContentHandler().endElement("", "", lineElement);
            }
        }

        getContentHandler().endElement("", "", fileElement);
        getContentHandler().endDocument();
    }

    private void parseLine(String curLine, ContentHandler ch)
            throws IOException, SAXException
    {

        String firstToken = null;
        String remainderOfLine = null;
        int commaIndex = locateFirstDelimiter(curLine);
        if (commaIndex > -1)
        {
            firstToken = curLine.substring(0,
                    commaIndex).trim();
            remainderOfLine =
                    curLine.substring(commaIndex + 1).trim();
        }
        else
        {
            // no commas, so the entire line is the token
            firstToken = curLine;
        }

        // remove redundant quotes
        firstToken = cleanupQuotes(firstToken);

        // emit the <value> element
        ch.startElement("", "", valueElement, EMPTY_ATTR);
        ch.characters(firstToken.toCharArray(), 0,
                firstToken.length());
        ch.endElement("", "", valueElement);

        // recursively process the remainder of the line
        if (remainderOfLine != null)
        {
            parseLine(remainderOfLine, ch);
        }
    }

    // locate the position of the comma, taking into account that
    // a quoted token may contain ignorable commas.
    private int locateFirstDelimiter(String curLine)
    {
        if (curLine.startsWith("\""))
        {
            boolean inQuote = true;
            int numChars = curLine.length();
            for (int i = 1; i < numChars; i++)
            {
                char curChar = curLine.charAt(i);
                if (curChar == '"')
                {
                    inQuote = !inQuote;
                }
                else if (curChar == ',' && !inQuote)
                {
                    return i;
                }
            }
            return -1;
        }
        else
        {
            return curLine.indexOf(',');
        }
    }

    // remove quotes around a token, as well as pairs of quotes
    // within a token.
    private String cleanupQuotes(String token)
    {
        StringBuffer buf = new StringBuffer();
        int length = token.length();
        int curIndex = 0;

        if (token.startsWith("\"") && token.endsWith("\""))
        {
            curIndex = 1;
            length--;
        }

        boolean oneQuoteFound = false;
        boolean twoQuotesFound = false;

        while (curIndex < length)
        {
            char curChar = token.charAt(curIndex);
            if (curChar == '"')
            {
                twoQuotesFound = (oneQuoteFound) ? true : false;
                oneQuoteFound = true;
            }
            else
            {
                oneQuoteFound = false;
                twoQuotesFound = false;
            }

            if (twoQuotesFound)
            {
                twoQuotesFound = false;
                oneQuoteFound = false;
                curIndex++;
                continue;
            }

            buf.append(curChar);
            curIndex++;
        }

        return buf.toString();
    }
}
