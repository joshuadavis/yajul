package org.yajul.io.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.yajul.io.DocumentArchiver;
import org.yajul.xml.DOMUtil;
import org.yajul.xml.XMLDocumentArchiver;
import org.yajul.xml.DOMPrinter;
import org.w3c.dom.Document;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * TODO: Add javadoc
 * <hr>
 * User: jdavis<br>
 * Date: May 14, 2004<br>
 * Time: 11:59:26 AM<br>
 * @author jdavis
 */
public class DocumentArchiverTest extends TestCase
{
    private DocumentArchiver archiver;
    private XMLDocumentArchiver xmlarchiver;
    private Date date;
    private Long id;

    public DocumentArchiverTest(String name)
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
        archiver = new DocumentArchiver();
        archiver.setStoreageDirectory(new File("./temp/document-archiver-test"));
        xmlarchiver = new XMLDocumentArchiver();
        xmlarchiver.setStoreageDirectory(new File("./temp/document-archiver-test"));
        date = new Date(0);
        id = new Long(1);
    }

    /**
     * Cleans up any state that needs to be undone after
     * the test has completed.
     */
    protected void tearDown()
    {
    }

    /**
     * Test object storeage/retrieval.
     */
    public void testObject() throws Exception
    {
        String thing = "this is a test.";
        String fileName = archiver.storeObject("objects",id,date,thing);
        assertNotNull(fileName);
        String retrieved = (String) archiver.retrieveObject("objects",id,date);
        assertEquals(thing,retrieved);
    }

    public void testDocument() throws Exception
    {
        Document doc = DOMUtil.createDocument("test");
        DOMUtil.addChildWithText(doc,doc.getDocumentElement(),"elem","this is a test");
        xmlarchiver.storeDocument("documents",id,date,doc);
        Document retrieved = xmlarchiver.retrieveDocument("documents",id,date);
        String expected = DOMPrinter.nodeToString(doc);
        String x = DOMPrinter.nodeToString(retrieved);
        assertEquals(expected,x);
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
        // TODO: Add setups and decorators here.
        return new TestSuite(DocumentArchiverTest.class);
    }
}
