package org.yajul.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.yajul.io.DocumentArchiver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Adds DOM document storeage methods to DocumentArchiver.
 * <hr>
 * User: jdavis<br>
 * Date: May 14, 2004<br>
 * Time: 10:04:09 AM<br>
 * @author jdavis
 */
public class XMLDocumentArchiver extends DocumentArchiver
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(XMLDocumentArchiver.class.getName());

    /** The default file extension for archived XML documents. **/
    public static final String DEFAULT_EXTENSION = ".xml.gz";

    public XMLDocumentArchiver()
    {
        setExtension(DEFAULT_EXTENSION);
    }

    /**
     * Stores a DOM document, given the id, date and sub-directory.
     *
     * @param subDirectory The sub-directory of the storeage directory where documents of this type are stored.
     * @param id           The id object that will be used to generate the file name.
     * @param date         The date, which will be used to generate the directory name.
     * @param document     The document that will be stored.
     * @return The name of the file that was used to store the object.
     * @throws IOException if something goes wrong.
     */
    public String storeDocument(String subDirectory, Object id, Date date, Document document) throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("storeDocument() : ENTER");
        try
        {
            Sink docOut = getSink(subDirectory, id, date);
            OutputStream stream = docOut.getStream();
            DOMPrinter.printNode(document,stream);
            stream.close();
            if (log.isDebugEnabled())
                log.debug("storeDocument() : Document sucessfully stored.");
            return docOut.getFilename();    // Return the relative file name.
        }
        catch (IOException e)
        {
            log.error(e, e);
            throw e;
        }
        catch (TransformerConfigurationException e)
        {
            log.error(e, e);
            throw new IOException(e.getMessage());
        }
        catch (TransformerException e)
        {
            log.error(e, e);
            throw new IOException(e.getMessage());
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug("storeDocument() : LEAVE");
        }
    }

    /**
     * Retrieves a DOM Document given the sub-directory, id and date.
     * @param subDirectory The sub-directory of the storeage directory where documents of this type are stored.
     * @param id           The id object that will be used to generate the file name.
     * @param date         The date, which will be used to generate the directory name.
     * @return The document (DOM).
     * @throws IOException if something goes wrong.
     */
    public Document retrieveDocument(String subDirectory, Long id, Date date) throws IOException
    {
        return retrieveDocument(subDirectory,generateFileName(id,date));
    }

    /**
     * Retrieves a DOM Document given the sub-directory and the file name.
     * @param subDirectory The sub-directory.
     * @param fileName     The name of the file, as returned by the storeObject() method.
     * @return The document (DOM).
     * @throws IOException if something goes wrong.
     */
    public Document retrieveDocument(String subDirectory, String fileName) throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("retrieveDocument() : ENTER");
        try
        {
            Source source = getSource(subDirectory, fileName);
            log.info("retrieveDocument() : " + source.getFilename());
            Document document = DOMUtil.parse(source.getStream());
            if (log.isDebugEnabled())
                log.debug("retrieveDocument() : Document sucessfully retrieved.");
            return document;
        } // try
        catch (FileNotFoundException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            log.error(e, e);
            throw e;
        }
        catch (ParserConfigurationException e)
        {
            log.error(e, e);
            throw new IOException(e.getMessage());
        }
        catch (SAXException e)
        {
            log.error(e, e);
            throw new IOException(e.getMessage());
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug("retrieveDocument() : LEAVE");
        }
    }

}
