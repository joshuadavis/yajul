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
        FieldDefinition def = new FieldDefinition();
        def.offset = offset;
        def.length = length;
        def.separator = separator;
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
            int endIndex = fieldDefinition.offset + fieldDefinition.length;
            String sub = currentLine.substring(fieldDefinition.offset, endIndex);
            if (fieldDefinition.separator != -1)
            {
                char c = currentLine.charAt(endIndex);
                if (fieldDefinition.separator != c)
                    throw new SAXException("Unexpected separator character '" + c + "'!");
            } // if
            tokens.add(sub);
        } // for
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }


    private class FieldDefinition
    {
        int offset;
        int length;
        int separator;
    }
}
