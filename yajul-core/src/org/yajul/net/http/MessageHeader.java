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

import org.apache.log4j.Logger;
import org.yajul.util.StringUtil;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Represents a set of headers, a status, and a command / status line.
 * User: josh
 * Date: Jan 25, 2004
 * Time: 10:41:42 AM
 */
public class MessageHeader extends GenericMessageHeader implements StatusCodes, HTTPConstants
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(MessageHeader.class);

    /** The headers did not indicate a way of determining the length of the message. */
    public static final int TRANSFER_MODE_UNKNOWN = 0;
    /** The length of the body is determined by the 'Content-length' header value. */
    public static final int TRANSFER_MODE_LENGTH = 1;
    /** The body will be sent in 'chunks'. */
    public static final int TRANSFER_MODE_CHUNKED = 2;
    /** The other endpoint will close the connection after the last octet of the body has been sent. */
    public static final int TRANSFER_MODE_CLOSE = 3;

    /** The type of the message: True if this is a response, false if it is a request. **/
    private boolean response;
    /** The HTTP status. **/
    private int status;
    /** The HTTP version. **/
    private String httpVersion;

    /** The transfer mode of the message body, as determined from the headers. **/
    private int transferMode;
    /** The content length, if specified.  Otherwise -1. **/
    private int contentLength;
    /** The host name. **/
    private String host;
    /** The port number (if specified). **/
    private int port;
    /** The content type. **/
    private String contentType;
    /** The protocol. **/
    private String protocol;

    // --- Response only fields ---

    /** The reason reasonPhrase (only if this is a response). **/
    private String reasonPhrase;

    // --- Request only fields ---

    /** The request URI (only if this is a request). **/
    private String requestURI;
    /** The request method (only if this is a request). **/
    private String method;

    public MessageHeader()
    {
    }

    public String getHost()
    {
        return host;
    }

    protected void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    protected void setPort(int port)
    {
        this.port = port;
    }

    public int getStatus()
    {
        return status;
    }

    public boolean isResponse()
    {
        return response;
    }

    public String getContentType()
    {
        return contentType;
    }

    protected void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getProtocol()
    {
        return protocol;
    }

    protected void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    /**
     * Returns true if the response was a server continue response.
     */
    public boolean isContinue()
    {
        return (status == CONTINUE);
    }

    /**
     * Returns true if the response is any 'okay' response.
     */
    public boolean isOkay()
    {
        return ((status / 100) == 2);
    }

    public String getHttpVersion()
    {
        return httpVersion;
    }

    public String getReasonPhrase()
    {
        return reasonPhrase;
    }

    public String getRequestURI()
    {
        return requestURI;
    }

    public String getMethod()
    {
        return method;
    }

    public String getHeaderValue(String name)
    {
        HTTPHeader httpHeader = getHeader(name);
        return httpHeader == null ? null : httpHeader.getValue();
    }

    public int getTransferMode()
    {
        return transferMode;
    }

    public int getContentLength()
    {
        return contentLength;
    }

    public boolean isKeepAlive()
    {
        if (!"HTTP/1.1".equals(getHttpVersion()))
            return false;
//                if (request.get.startsWith("connection:"))
//                    keepalive = serverAttributes.getKeepAlive() && lineLower.indexOf("keep-alive") > -1;
        HTTPHeader h = getHeader("connection");
        if (h == null)
            return false;
        String v = h.getValueLowerCase();
        if (v == null)
            return false;
        return v.indexOf("keep-alive") > -1;
    }

    protected void read(HTTPInputStream in, boolean response) throws IOException
    {
/*
        generic-message = start-line
                          *(message-header CRLF)
                          CRLF
                          [ message-body ]
        start-line      = Request-Line | Status-Line
        Status-Line     = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        Request-Line    = Method SP Request-URI SP HTTP-Version CRLF
*/
        init(response); // Initialize all fields.

        // Read the status / command line.
        String line = in.readLine();

        if (log.isDebugEnabled())
            log.debug("read() : '" + line + "'");

        if (line.length() == 0)
            throw new IOException("Zero length " + messageType() + " line!");

        // Parse the start line.
        parseStartLine(line);
        setStartLine(line);

        // CRLF separates headers from the body.
        while (in.readCRLF() == null)    // If there is no 'CRLF' by itself...
            putHeader(in.readHeader());

        // The CRLF CRLF was reached.  The content will be next.
        // Determine the transfer mode based on the headers.
        requireTransferMode();
    }

    private void parseStartLine(String line) throws IOException
    {
        // Spaces separate the tokens.
        StringTokenizer t = new StringTokenizer(line, " ");

        String token = requireToken(line, t);
        if (isResponse())   // The first token in a response line is the HTTP version.
            httpVersion = requireHTTPVersion(line, token);
        else            // The first token in a request line is the HTTP method.
            method = token;

        token = requireToken(line, t);
        if (isResponse())   // The second token in a response is the status code.
            status = requireStatusCode(line,token);
        else
            requestURI = token;

        token = requireToken(line, t);
        if (isResponse())   // The third token in a response is the reason phrase.
            reasonPhrase = readReasonPhrase(t);
        else            // The third token in a request is the HTTP version.
            httpVersion = requireHTTPVersion(line, token);
    }

    protected int requireStatusCode(String line, String statusCode) throws IOException
    {
        try
        {
            return Integer.parseInt(statusCode);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Unparseable status code in '" + line + "' : " + e.getMessage());
        }
    }

    protected String requireHTTPVersion(String line, String token) throws IOException
    {
        if (!token.startsWith("HTTP"))
        {
            throw new IOException("Unrecognized HTTP version: '" + token + "'");
        }
        return token;
    }

    protected void setMethod(String method)
    {
        String m = method.toUpperCase().trim();

        if (!(  m.equals(METHOD_PUT)    ||
                m.equals(METHOD_POST)   ||
                m.equals(METHOD_GET)    ))
            throw new IllegalArgumentException("Unknown method: " + method);
        this.method = m;
    }

    protected void setHttpVersion(String version)
    {
        this.httpVersion = version;
    }

    protected void setRequestURI(String requestURI)
    {
        this.requestURI = (StringUtil.isEmpty(requestURI)) ? SERVER_ROOT : requestURI;
    }

    private void requireTransferMode() throws IOException
    {
        // Based on the Content-Length, Transfer-Encoding, and Connection headers, determine
        // the 'transfer mode' of the content.

        String transferEncoding = getHeaderValue(HeaderConstants.TRANSFER_ENCODING);
        String contentLength = getHeaderValue(HeaderConstants.CONTENT_LENGTH);
        String connection = getHeaderValue(HeaderConstants.CONNECTION);

        // If transfer encoding was specified, and it has any value besides
        // 'identity', then read the body with the 'chunked' reader.
        if (transferEncoding != null)
        {
            HTTPInputStream tokenizer = new HTTPInputStream(transferEncoding);
            String value = tokenizer.readToken();
            if (!value.equalsIgnoreCase("identity"))
                transferMode = TRANSFER_MODE_CHUNKED;
        }
        // If the content length was specified, read the specified number
        // of bytes.
        else if (contentLength != null)
        {
            transferMode = TRANSFER_MODE_LENGTH;
            requireContentLength(contentLength);
        }
        // Otherwise, if the connection was specified as 'close', just
        // read until the server closes the stream.
        else if (connection != null)
        {
            HTTPInputStream tokenizer = new HTTPInputStream(connection);
            String value = tokenizer.readToken();
            if (value.equalsIgnoreCase("close"))
                transferMode = TRANSFER_MODE_CLOSE;
        }

        // TODO: Is 'CLOSE' the right default mode? Is this an error?
        if (transferMode == TRANSFER_MODE_UNKNOWN)
            transferMode = TRANSFER_MODE_CLOSE;
    }

    private void requireContentLength(String contentLength) throws IOException
    {
        try
        {
            this.contentLength = Integer.parseInt(contentLength);
        }
        catch (NumberFormatException e)
        {
            // It's too late to push back all of the headers.
            throw new IOException("Unparseable content length '" + contentLength + "' : " + e.getMessage());
        }
    }

    private String messageType()
    {
        return ((isResponse()) ? "status" : "request");
    }

    private String readReasonPhrase(StringTokenizer t)
    {
        // Put the rest of the tokens into the comment.
        StringBuffer buf = new StringBuffer();
        while (t.hasMoreTokens())
        {
            buf.append(t.nextToken());
            if (t.hasMoreTokens())
                buf.append(" ");
        }
        return buf.toString();
    }

    private void init(boolean response)
    {
        this.response = response;   // Remember the message type.
        status = UNKNOWN;           // Status is unknown.
        httpVersion = method = reasonPhrase = requestURI = null;
        transferMode = TRANSFER_MODE_UNKNOWN;   // Don't know the transfer mode yet.
        contentLength = -1;                     // Don't know the content length yet.
        port = -1;
        protocol = null;
        contentType = null;
        init();
    }

    private String requireToken(String line, StringTokenizer t) throws IOException
    {
        if (!t.hasMoreTokens())
            throw new IOException("Mot enough tokens in " + messageType() + " line: '" + line + "'");

        return t.nextToken();
    }
}
