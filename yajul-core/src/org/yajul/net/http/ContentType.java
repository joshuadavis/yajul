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

import org.yajul.util.NameValuePair;

import java.util.StringTokenizer;

/**
 * Parses a content type header and encapsulates the values.
 * User: josh
 * Date: Jan 24, 2004
 * Time: 3:59:17 PM
 */
public class ContentType
{
    private String      type;
    private String      subtype;
    private NameValuePair[]    parameters;

    public ContentType(String value)
    {
        int fs = value.indexOf('/');
        type  = value.substring(0,fs);
        StringTokenizer st = new StringTokenizer(value.substring(fs+1),";");
        int count = st.countTokens();
        subtype = st.nextToken();
        parameters = new NameValuePair[count-1];
        for (int i = 0; i < count - 1; i++)
        {
            String token = st.nextToken();
            int eq = token.indexOf('=');
            parameters[i] = new NameValuePair(token.substring(0,eq),token.substring(eq+1));
        }
    }

    public String getType() { return type; }

    public String getSubtype() { return subtype; }

    public NameValuePair[] getParameters() { return parameters; }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(type);
        buf.append("/");
        buf.append(subtype);
        if (parameters != null)
        {
            for (int i = 0; i < parameters.length; i++)
            {
                buf.append(";");
                buf.append(parameters[i].getName());
                buf.append("=");
                buf.append(parameters[i].getValue());
            } // for
        } // if
        return buf.toString();
    }
}
