// $Id$
package org.yajul.net.xmlrpc;

import org.yajul.io.Base64Encoder;
import org.yajul.xml.XMLWriter;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A specialized XMLWriter that encodes XML-RPC requests and responses.
 *
 * @author josh Apr 8, 2004 7:25:09 AM
 */
public class XmlRpcWriter extends XMLWriter
{
    /**
     * Simple date format string, ISO 8601
     * (4 digit year, 2 digit month, 2 digit day of month, 2 digit hours, minutes, and
     * seconds, separated by colons).
     */
    public final static String ISO8601_FORMAT = "yyyyMMdd'T'HH:mm:ss";

    private DateFormat df = new SimpleDateFormat(ISO8601_FORMAT);

    public XmlRpcWriter()
    {
        super();
    }

    public XmlRpcWriter(Writer writer)
    {
        super(writer);
    }

    public XmlRpcWriter(Writer writer, String encoding)
    {
        super(writer, encoding);
    }

    /**
     * Generate an XML-RPC request from a method name and a list of parameters.
     */
    public void writeRequest(String method, List params) throws IOException
    {
        startElement("methodCall");
        startElement("methodName");
        chardata(method);
        endElement("methodName");
        startElement("params");
        if (params != null)
        {
            for (Iterator iterator = params.iterator(); iterator.hasNext();)
            {
                Object o = (Object) iterator.next();
                startElement("param");
                writeObject(o);
                endElement("param");
            }
        }
        endElement("params");
        endElement("methodCall");
    }

    /**
     * Writes the XML-RPC representation of a supported Java object.
     */
    public void writeObject(Object obj) throws IOException
    {
        startElement("value");
        if (obj == null)
        {
            // try sending experimental <ni/> element
            emptyElement("nil");
        }
        else if (obj instanceof String)
        {
            chardata(obj.toString());
        }
        else if (obj instanceof Boolean)
        {
            startElement("boolean");
            write(((Boolean) obj).booleanValue() ? "1" : "0");
            endElement("boolean");
        }
        else if (obj instanceof Double || obj instanceof Float || obj instanceof Integer)
        {
            startElement("double");
            write(obj.toString());
            endElement("double");
        }
        else if (obj instanceof Date)
        {
            startElement("dateTime.iso8601");
            Date d = (Date) obj;
            write(df.format(d));
            endElement("dateTime.iso8601");
        }
        else if (obj instanceof byte[])
        {
            startElement("base64");
            write(Base64Encoder.encode((byte[]) obj));
            endElement("base64");
        }
        else if (obj instanceof List)
        {
            startElement("array");
            startElement("data");
            List list = (List) obj;
            int size = list.size();
            for (int i = 0; i < size; i++)
                writeObject(list.get(i));
            endElement("data");
            endElement("array");
        }
        else if (obj instanceof Map)
        {
            startElement("struct");
            Map m = (Map)obj;
            Set entries = m.entrySet();
            for (Iterator iterator = entries.iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                startElement("member");
                startElement("name");
                chardata(entry.getKey().toString());
                endElement("name");
                writeObject(entry.getValue());
                endElement("member");
            }
            endElement("struct");
        }
        else
            throw new RuntimeException("unsupported Java type: " + obj.getClass());
        endElement("value");
    }
}
