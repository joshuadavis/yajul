/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 13, 2003
 * Time: 9:59:15 PM
 */
package org.yajul.xml.test;

import junit.framework.TestCase;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.yajul.xml.XMLWriter;

import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * JUnit test case.
 * @author josh
 */
public class XMLWriterTest extends TestCase
{

    private static SAXParserFactory factory = null;

    private static SAXParserFactory getFactory()
    {
        synchronized (XMLWriterTest.class)
        {
            if (factory == null)
            {
                factory = SAXParserFactory.newInstance();
                factory.setValidating(false);
            }
        }
        return factory;
    }

    /**
     * Creates test case XMLWriterTest
     * @param name The name of the test (method).
     */
    public XMLWriterTest(String name)
    {
        super(name);
    }

    public void testEchoXML() throws Exception
    {
        String content = "<test> this is a test <foo bar=\"true\">something</foo> </test>";
        InputSource is = new InputSource(new StringReader(content));
        XMLReader reader = getFactory().newSAXParser().getXMLReader();
        StringWriter sw = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(sw);
        xmlWriter.setExcludeXMLDecl(true);
        reader.setContentHandler(xmlWriter);
        reader.parse(is);
        String s = sw.toString();
//        System.out.println("["+s+"]");
        assertEquals(content,s);
    }
}
