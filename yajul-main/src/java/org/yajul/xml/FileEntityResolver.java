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
/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 4, 2003
 * Time: 4:46:53 PM
 */
package org.yajul.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.yajul.log.Logger;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * TODO: Add a description of this class here.
 * @author josh
 */
public class FileEntityResolver implements EntityResolver
{
    /** A logger for this class. */
    private static Logger log = Logger.getLogger(FileEntityResolver.class);

    private String defaultPath;

    public FileEntityResolver(String defaultPath)
    {
        this.defaultPath = defaultPath;
    }

    /**
     * Allow the application to resolve external entities.
     *
     * <p>The Parser will call this method before opening any external
     * entity except the top-level document entity (including the
     * external DTD subset, external entities referenced within the
     * DTD, and external entities referenced within the document
     * element): the application may request that the parser resolve
     * the entity itself, that it use an alternative URI, or that it
     * use an entirely different input source.</p>
     *
     * <p>Application writers can use this method to redirect external
     * system identifiers to secure and/or local URIs, to look up
     * public identifiers in a catalogue, or to read an entity from a
     * database or other input source (including, for example, a dialog
     * box).</p>
     *
     * <p>If the system identifier is a URL, the SAX parser must
     * resolve it fully before reporting it to the application.</p>
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     * @return An InputSource object describing the new input source,
     *         or null to request that the parser open a regular
     *         URI connection to the system identifier.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception IOException A Java-specific IO exception,
     *            possibly the result of creating a new InputStream
     *            or Reader for the InputSource.
     * @see InputSource
     */
    public InputSource resolveEntity(String publicId,
                                     String systemId)
            throws SAXException, IOException
    {
        if (log.isDebugEnabled())
            log.debug("resolveEntity(" + publicId + ","+systemId
                    + ") defaultPath = " + defaultPath);
        File file = new File(systemId);
        if (file.exists())
            return createInputSource(file);
        file = new File(defaultPath,systemId);
        if (file.exists())
            return createInputSource(file);
        return null;
    }

    private InputSource createInputSource(File file) throws FileNotFoundException
    {
        InputSource is = new InputSource(new FileInputStream(file));
        String systemId = "file://"+file.getAbsolutePath();
        is.setSystemId(systemId);
        if (log.isDebugEnabled())
            log.debug("Using " + systemId);
        return is;
    }
}
