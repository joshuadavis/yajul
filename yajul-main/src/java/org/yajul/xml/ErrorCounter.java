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
 * Time: 4:46:22 PM
 */
package org.yajul.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.yajul.log.Logger;

import java.io.PrintWriter;
import java.io.OutputStream;

/**
 * A SAX ErrorHandler that counts all the error messages and formats
 * the messages to an output stream.
 * @author Kent Vogel
 * @author Joshua Davis (refactored for YAJUL)
 */
public class ErrorCounter implements ErrorHandler
{
    private static Logger log = Logger.getLogger(ErrorCounter.class);

    private boolean logErrors = false;
    private PrintWriter writer;
    private int errorCount;
    private int warningCount;

    /**
     * Creates an ErrorCounter that will write errors to the given output stream
     * @param out The output stream to write the errors to.
     */
    public ErrorCounter(OutputStream out)
    {
        if (out != null)
            writer = new PrintWriter(out);
        else
            writer = null;
    }

    /**
     * Enables logging of SAX errors to the Logger.  This is disabled by
     * default.
     * @param logErrors True to enable logging of errors, false to disable.
     */
    public void setLogErrors(boolean logErrors)
    {
        this.logErrors = logErrors;
    }

    /**
     * Constructs an ErrorCounter that does not show output messages.
     */
    public ErrorCounter()
    {
        writer = null;
    }

    /**
     * Returns the number of errors.
     * @return int - the number of errors encountered
     */
    public int getErrorCount()
    {
        return errorCount;
    }

    /**
     * Returns the number of warnings.
     * @return int - the number of warnings encountered
     */
    public int getWarningCount()
    {
        return warningCount;
    }

    // --- ErrorHandler implementation --

    /**
     * Receive notification of a recoverable error.
     *
     * <p>This corresponds to the definition of "error" in section 1.2
     * of the W3C XML 1.0 Recommendation.  For example, a validating
     * parser would use this callback to report the violation of a
     * validity constraint.  The default behaviour is to take no
     * action.</p>
     *
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.  If the
     * application cannot do so, then the parser should report a fatal
     * error even if the XML 1.0 recommendation does not require it to
     * do so.</p>
     *
     * <p>Filters may use this method to report other, non-XML errors
     * as well.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see SAXParseException
     */
    public void error(SAXParseException exception)
            throws SAXException
    {
        errorCount++;
        if (logErrors || writer != null)
        {
            String msg = formatError("ERROR", exception);
            if (logErrors)
                log.error(msg);
            if (writer != null)
                writer.println(msg);
        }
    }

    /**
     * Receive notification of a warning.
     *
     * <p>SAX parsers will use this method to report conditions that
     * are not errors or fatal errors as defined by the XML 1.0
     * recommendation.  The default behaviour is to take no action.</p>
     *
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.</p>
     *
     * <p>Filters may use this method to report other, non-XML warnings
     * as well.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  SAX parse exception.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see SAXParseException
     */
    public void warning(SAXParseException exception)
            throws SAXException
    {
        warningCount++;
        if (logErrors || writer != null)
        {
            String msg = formatError("WARNING", exception);
            if (logErrors)
                log.warn(msg);
            if (writer != null)
                writer.println(msg);
        }
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * <p>This corresponds to the definition of "fatal error" in
     * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
     * parser would use this callback to report the violation of a
     * well-formedness constraint.</p>
     *
     * <p>The application must assume that the document is unusable
     * after the parser has invoked this method, and should continue
     * (if at all) only for the sake of collecting addition error
     * messages: in fact, SAX parsers are free to stop reporting any
     * other events once this method has been invoked.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @exception SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see SAXParseException
     */
    public void fatalError(SAXParseException exception)
            throws SAXException
    {
        errorCount++;

        if (writer != null)
        {
            String msg = formatError("ERROR", exception);
            log.fatal(msg,exception);
            writer.println(msg);
        }
    }

    // --- Implementation methods ---

    /**
     * Formats a SAX error as a string
     * @param severity Prepended to the string.  e.g. 'ERROR'
     */
    private String formatError(String severity, SAXParseException e)
    {
        StringBuffer message = new StringBuffer();

        message.append(severity + " Line " + e.getLineNumber());
        if (e.getColumnNumber() != -1)
            message.append(" Position " + e.getColumnNumber());
        if (e.getSystemId() != null)
            message.append(" " + e.getSystemId());

        message.append(" " + e.getMessage());

        return message.toString();
    }
}
