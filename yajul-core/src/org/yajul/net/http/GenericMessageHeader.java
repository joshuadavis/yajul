/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
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
package org.yajul.net.http;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the start line and headers of a generic message, as per RFC 2616:
 * <pre>
 *        generic-message = start-line
 *                          *(message-header CRLF)
 *                          CRLF
 *                          [ message-body ]
 *        start-line      = Request-Line | Status-Line
 *        Status-Line     = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 *        Request-Line    = Method SP Request-URI SP HTTP-Version CRLF
 * </pre>
 * User: josh
 * Date: Jan 29, 2004
 * Time: 7:45:11 AM
 */
public class GenericMessageHeader
{
    /** The request / status line. **/
    private String startLine;
    /** A map of the headers, by their *lowercase* name. **/
    private Map headersByName = new HashMap();

    public String getStartLine()
    {
        return startLine;
    }

    public Iterator iterateHeaders()
    {
        return headersByName.values().iterator();
    }

    public boolean containsHeader(String name)
    {
        return headersByName.containsKey(name.toLowerCase());
    }

    public void setHeaderValue(String name, String value)
    {
        putHeader(new HTTPHeader(name, value));
    }

    public String getHeadersAsString()
    {
        StringBuffer buf = new StringBuffer();
        Iterator i = iterateHeaders();
        while (i.hasNext())
        {
            HTTPHeader h = (HTTPHeader) i.next();
            buf.append(h.getName());
            buf.append(": ");
            buf.append(h.getValue());
            buf.append(HTTPConstants.CRLF);
        }
        return buf.toString();
    }

    public void putHeader(HTTPHeader h)
    {
        headersByName.put(h.getName().toLowerCase(), h);
    }

    public void setStartLine(String startLine)
    {
        this.startLine = startLine;
    }

    public HTTPHeader getHeader(String name)
    {
        return (HTTPHeader) headersByName.get(name.toLowerCase());
    }

    protected void init()
    {
        headersByName.clear();      // Remove all headers.
        setStartLine(null);
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("[GenericMessageHeader\nstart-line: '").append(startLine).append("'\n");
        Iterator iter = iterateHeaders();
        while (iter.hasNext())
        {
            HTTPHeader httpHeader = (HTTPHeader) iter.next();
            buf.append(httpHeader.toString()).append("\n");
        }
        buf.append("]");
        return buf.toString();
    }
}
