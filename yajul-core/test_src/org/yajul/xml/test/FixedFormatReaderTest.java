package org.yajul.xml.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.yajul.xml.CSVReader;
import org.yajul.xml.DOMUtil;
import org.yajul.xml.FixedFormatReader;
import org.yajul.xml.DOMPrinter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.StringReader;
import java.io.IOException;

/**
 * TODO: Add javadoc
 * User: jdavis
 * Date: Mar 11, 2004
 * Time: 7:56:27 PM
 * @author jdavis
 */
public class FixedFormatReaderTest extends TestCase
{

    public FixedFormatReaderTest(String name)
    {
        super(name);
    }

    /**
     * Performs any set up that is required by the test,
     * such as initializing instance variables of the test
     * case class, etc. Invoked before every test method.
     */
    protected void setUp()
    {
    }

    /**
     * Cleans up any state that needs to be undone after
     * the test has completed.
     */
    protected void tearDown()
    {
    }

    /**
     * Test the CSV reader.
     */
    public void testFixedFormatReader() throws Exception
    {
        FixedFormatReader reader = new FixedFormatReader();
        reader.setTitleLines(1);
        reader.setHeaderLines(1);
        reader.addField(0,3,' ');
        reader.addField(4,5,' ');
        reader.addField(10,10,FixedFormatReader.NO_SEPARATOR);   // No separator

        // These inputs should be the same.
        checkFixedFormatStream("This is the title\nXYZ ABCDE 0123456789\n111 22222 3333333333", reader);
        SAXException se = null;
        try
        {
            checkFixedFormatStream("This is the title\nXYZ  ABCDE 0123456789\n111 22222 3333333333", reader);
        }
        catch (SAXException e)
        {
            se = e;
        }
        assertNotNull(se);
    }

    private void checkFixedFormatStream(String inputString, FixedFormatReader reader) throws TransformerException, IOException, SAXException
    {
        InputSource input = new InputSource(new StringReader(inputString));
        Document doc = DOMUtil.parse(reader,input);
        Element elem = doc.getDocumentElement();
        DOMPrinter.printNode(doc,System.out);
/*
        assertEquals(reader.getFileElement(),elem.getTagName());
        assertTrue(elem.hasChildNodes());
        Element[] lines = DOMUtil.getChildElements(elem);
        assertEquals(2,lines.length);
        for (int i = 0; i < lines.length; i++)
        {
            Element line = lines[i];
            assertEquals(reader.getLineElement(),line.getTagName());
            Element[] values = DOMUtil.getChildElements(line);
            assertEquals(3,values.length);
            for (int j = 0; j < values.length; j++)
            {
                Element value = values[j];
                assertEquals(reader.getValueElement(),value.getTagName());
                assertEquals("" + i + "|" + j,DOMUtil.getChildText(value));
            }
        }
*/
    }

    /**
     * Constructs a test suite for this test case, providing any required
     * Setup wrappers, or decorators as well.
     * @return Test - The test suite.
     */
    public static Test suite()
    {
        // Return the default test suite: No setup, all public methods with
        // no return value, no parameters, and names that begin with 'test'
        // are added to the suite.
        return new TestSuite(FixedFormatReaderTest.class);
    }
}
