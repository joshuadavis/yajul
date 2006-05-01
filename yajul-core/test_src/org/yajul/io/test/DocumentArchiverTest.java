package org.yajul.io.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;
import org.yajul.io.DocumentArchiver;
import org.yajul.xml.DOMPrinter;
import org.yajul.xml.DOMUtil;
import org.yajul.xml.XMLDocumentArchiver;

/**
 * Tests DocumentArchiver and XMLDocumentArchiver
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
    private static final String SUB_DIRECTORY = "objects";

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
        archiver.init();
        String fileName = archiver.storeObject(SUB_DIRECTORY,id,date,thing);
        assertNotNull(fileName);
        String retrieved = (String) archiver.retrieveObject(SUB_DIRECTORY,id,date);
        assertEquals(thing,retrieved);
        DocumentArchiver.Source source = archiver.getSource(SUB_DIRECTORY,id,date);
        assertNotNull(source);
        assertNotNull(archiver.getStoreageDirectory());
        assertNull(archiver.getRetrieveDirectories());
        assertEquals(0,archiver.getRetrieveDirectoryCount());
    }

    /**
     * Test file not found.
     */
    public void testFileNotFound() throws Exception
    {
        FileNotFoundException fnfe = null;
        try
        {
            archiver.retrieveObject(SUB_DIRECTORY,new Long(999),date);
        }
        catch (FileNotFoundException e)
        {
            fnfe = e;
        }
        assertNotNull(fnfe);
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
        return new TestSuite(DocumentArchiverTest.class);
    }
}
