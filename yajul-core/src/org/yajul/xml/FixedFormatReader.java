package org.yajul.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Reads a fixed format file and generates SAX2 events.
 * <br>
 * Parses files of the following form:
 * <ol>
 * <li>Any number of 'title' lines, which will not be parsed into field values.</li>
 * <li>Any number of 'field heading' lines.  The field definitions will be used to parse
 * the 'column names' from these lines.</li>
 * <li>Any number of records (lines), which will be parsed according to the field definitions.</li>
 * <ol>
 * User: jdavis
 * Date: Mar 16, 2004
 * Time: 5:05:50 PM
 * @author jdavis
 */
public class FixedFormatReader extends TextReader
{

    public static final String DEFAULT_TITLE_ELEMENT = "title";
    public static final String DEFAULT_HEADING_ELEMENT = "heading";

    /** The number of title lines, these will not be parsed with the fixed format parser. **/
    private int titleLines;
    /** The number of lines used for column names. **/
    private int headerLines;
    private ArrayList fieldDefinitions = new ArrayList();
    private String titleElement = DEFAULT_TITLE_ELEMENT;
    private String headingElement = DEFAULT_HEADING_ELEMENT;
    public static final int NO_SEPARATOR = -1;


    public int getTitleLines()
    {
        return titleLines;
    }

    public void setTitleLines(int titleLines)
    {
        this.titleLines = titleLines;
    }

    public int getHeaderLines()
    {
        return headerLines;
    }

    public void setHeaderLines(int headerLines)
    {
        this.headerLines = headerLines;
    }

    public void addField(int offset,int length,int separator)
    {
        FieldDefinition def = new FieldDefinition(offset,length,separator,true);
        fieldDefinitions.add(def);
    }

    /** Ordered set: For each field, the offset and length, separator char (after) **/

    protected void parseLine(String curLine, ContentHandler ch)
            throws IOException, SAXException
    {

        // If this is a title line, don't parse it.
        if (getLineCounter() < titleLines)
        {
            emitTag(ch,titleElement,curLine);
        }
        else if (getLineCounter() < (titleLines + headerLines))
        {
            String[] tokens = getFields(curLine);
            // Emit each token as a heading element.
            for (int i = 0; i < tokens.length; i++)
                emitTag(ch,headingElement,tokens[i]);
        }
        else
        {
            String[] tokens = getFields(curLine);
            // Emit each token as a heading element.
            for (int i = 0; i < tokens.length; i++)
                emitTag(ch,getValueElement(),tokens[i]);
        }
    }

    private String[] getFields(String currentLine) throws SAXException
    {
        ArrayList tokens = new ArrayList();
        for (Iterator iterator = fieldDefinitions.iterator(); iterator.hasNext();)
        {
            FieldDefinition fieldDefinition = (FieldDefinition) iterator.next();
            tokens.add(fieldDefinition.getToken(currentLine));
        } // for
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    protected String prepareLine(String curLine)
    {
        // Fixed format: We don't want to trim or anything like that because it's stupid fixed format.
        return curLine;
    }

    private class FieldDefinition
    {
        int offset;
        int length;
        int separator;
        boolean trim;

        public FieldDefinition(int offset, int length, int separator, boolean trim)
        {
            this.offset = offset;
            this.length = length;
            this.separator = separator;
            this.trim = trim;
        }

        String getToken(String currentLine) throws SAXException
        {
            // If the offset is past the end of the line, then BOOM!
            if (offset >= currentLine.length())
                throw new SAXException(
                        "Field offset " + offset + " is greater than the line length!\n" +
                        "current line: '" + currentLine + "'");

            int endIndex = offset + length;
            // If the end index is past the end of the line, we should be nice and just get what
            // we can.
            if (endIndex > currentLine.length())
                throw new SAXException(
                        "End index " + endIndex + " is greater than the line length!\n" +
                        "current line: '" + currentLine + "'");

            String token = currentLine.substring(offset, endIndex);
            if (separator != -1)
            {
                char c = currentLine.charAt(endIndex);
                if (separator != c)
                    throw new SAXException("Unexpected separator character '" + c + "'!");
            } // if

            // If trimming is enabled, trim the value.
            if (trim)
                token = token.trim();

            return token;
        }
    }
}
