package org.yajul.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Reads a CSV (comma separated values) file and emits SAX2 events into a
 * ContentHandler.
 * User: jdavis
 * Date: Mar 11, 2004
 * Time: 7:45:11 PM
 * @author jdavis
 */
public class CSVReader extends TextReader
{

    protected void parseLine(String line, ContentHandler handler)
            throws IOException, SAXException
    {

        String firstToken = null;
        String remainderOfLine = null;
        int commaIndex = locateFirstDelimiter(line);
        if (commaIndex > -1)
        {
            firstToken = line.substring(0,commaIndex).trim();
            remainderOfLine = line.substring(commaIndex + 1).trim();
        }
        else
        {
            // No commas, so the entire line is the token
            firstToken = line;
        }

        // Remove redundant quotes
        firstToken = cleanupQuotes(firstToken);

        // emit the <value> element
        emitTag(handler, getValueElement(), firstToken);

        // Recursively process the remainder of the line
        if (remainderOfLine != null)
        {
            parseLine(remainderOfLine, handler);
        }
    }

    private int locateFirstDelimiter(String curLine)
    {
        // If the line starts with a double quote, then find the end quote...
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
            return curLine.indexOf(',');
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
