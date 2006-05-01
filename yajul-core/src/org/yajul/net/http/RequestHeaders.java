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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Jan 22, 2004
 * Time: 8:19:51 PM
 */
public class RequestHeaders
{
    private String command;
    private String[] commandTokens;
    private String[] headers;
    private Map valueByLowerCaseName = new HashMap();

    public RequestHeaders(String command, String[] headers)
    {
        this.command = command;
        this.headers = headers;
        StringTokenizer tokens = new StringTokenizer(command);
        commandTokens = new String[tokens.countTokens()];
        for (int i = 0; i < commandTokens.length; i++)
            commandTokens[i] = tokens.nextToken();
        for (int i = 0; i < headers.length; i++)
        {
            String header = headers[i];
            int index = header.indexOf(':');
            if (index > 0)
            {
                String name = header.substring(0, index);
                String value = header.substring(index + 1);
                String key = name.toLowerCase();
                // log.info("key='" + key + "' value='" + value +"'");
                valueByLowerCaseName.put(key, value);
            }
        }
    }

    public String getHeaderValue(String name)
    {
        String key = name.toLowerCase();
        return (String) valueByLowerCaseName.get(key);
    }

    public String getCommand()
    {
        return command;
    }

    public String[] getCommandTokens()
    {
        return commandTokens;
    }

    public String[] getHeaders()
    {
        return headers;
    }

    public String getMethod()
    {
        if (commandTokens.length > 0)
            return commandTokens[0];
        else
            return null;
    }

    public String getUri()
    {
        if (commandTokens.length > 1)
            return commandTokens[1];
        else
            return null;
    }

    public String getHttpVersion()
    {
        if (commandTokens.length > 2)
            return commandTokens[2];
        else
            return null;
    }

    public int getContentLength()
    {
        // TODO: Implement this.
        return -1;
    }

    public boolean isKeepAlive()
    {
        // TODO: Implement this
        // serverAttributes.getKeepAlive() && lineLower.indexOf("keep-alive") > -1;
        return false;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * @return  a string representation of the object.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("command = '").append(command).append("'\n");
        for (int i = 0; i < headers.length; i++)
        {
            String header = headers[i];
            buf.append("headers[").append(i).append("]='").append(header).append("'\n");
        }
        return buf.toString();
    }

}
