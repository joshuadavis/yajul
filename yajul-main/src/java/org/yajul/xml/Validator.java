/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
/*******************************************************************************
 *  Old log...
 *      Revision 1.3  2001/04/05 18:50:17  jdavis
 *      For a clean build, xalan.jar is still necessary.
 *
 *      Revision 1.2  2001/04/04 15:44:20  kvogel
 *      Updated to jaxp1.1
 *
 *      Revision 1.1  2000/12/02 00:02:29  cvsuser
 *      Added.
 *      date	2000.10.30.14.42.00;	author kentv;	state Exp;

 * 1     10/30/00 9:42a Kentv
 * Added.
 *******************************************************************************/

package org.yajul.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.yajul.log.Logger;
import org.yajul.util.CommandLine;
import org.yajul.util.CommandLineException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Validates an XML document.   If the document is a file, a special
 * entity resolver will be used to resolve the DTD reference using a relative
 * path.
 * @author Kent Vogel
 * @author Joshua Davis
 */
public class Validator
{
    private static Logger log = Logger.getLogger(Validator.class);

    /** A static, validating document builder factory. **/
    private static DocumentBuilderFactory factory = null;

    /**
     * Validates and XML document
     * @param fileName The name of the XML file.  Like, to help find the DTD.
     * @param errors The stream to write errors to.  If it is null, no errors
     * will be displayed, but they will be counted.
     * @return true iff the document had no errors
     */
    public static boolean validateFile(String fileName,
                                       OutputStream errors) throws FileNotFoundException
    {
        ErrorCounter errorHandler = new ErrorCounter(errors);

        //make input source
        InputSource is = new InputSource(new FileInputStream(fileName));
        File file = new File(fileName);
        String systemID = file.getAbsolutePath();
//        log.debug("systemId = " + systemID);
        is.setSystemId(systemID);
        EntityResolver resolver = new FileEntityResolver(file.getParent());
        return validate(is, errorHandler, resolver);

    }

    private static boolean validate(InputSource is, ErrorCounter errorHandler,
                                    EntityResolver resolver)
    {
        try
        {
            DocumentBuilder builder = getFactory().newDocumentBuilder();
            builder.setErrorHandler(errorHandler);
            builder.setEntityResolver(resolver);
            builder.parse(is);
        }
        catch (ParserConfigurationException e)
        {
            log.error("Unexpected exception: " + e.getMessage(), e);
            return false;
        }
        catch (FactoryConfigurationError factoryConfigurationError)
        {
            log.error("Unexpected exception: " + factoryConfigurationError.getMessage(), factoryConfigurationError);
            return false;
        }
        catch (SAXException e)
        {
            log.error("Unexpected exception: " + e.getMessage(), e);
            return false;
        }
        catch (IOException e)
        {
            log.error("Unexpected exception: " + e.getMessage(), e);
            return false;
        }

        return errorHandler.getErrorCount() == 0;
    }

    private static DocumentBuilderFactory getFactory()
    {
        synchronized (Validator.class)
        {
            if (factory == null)
            {
                factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(true);
            }
        }
        return factory;
    }

    /**
     * Command line validator.<br>
     * <code>usage: org.yajul.xml.Validator filename</code>
     */
    public static void main(String[] args)
    {
        CommandLine cl = new CommandLine(args);

        try
        {
            //run the validator
            validateFile(cl.get(0), System.out);
        }
        catch (CommandLineException e)
        {
            System.out.println(
                    "Usage: java org.yajul.xml.Validator file");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

