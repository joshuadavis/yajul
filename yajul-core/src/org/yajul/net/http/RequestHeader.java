
package org.yajul.net.http;

import org.yajul.net.URLParser;
import org.yajul.util.StringUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.net.MalformedURLException;

/**
 * A single HTTP request.
 */
public class RequestHeader extends MessageHeader implements HTTPConstants
{
    /**
     * Creates an HTTP request.
     */
    public RequestHeader(String method,String protocol,String host,int port,String path)
        throws MalformedURLException
    {
        setProtocol(protocol);
        setHost(host);
        setPort(port);
        setMethod(method);
        setRequestURI(path);
        setHttpVersion(new String(VERSION));
        setStartLine(getMethod() + " " + getRequestURI() + " " + getHttpVersion());
    }

    public RequestHeader()
    {
    }

    /**
     * Creates an HTTP request.
     */
    public RequestHeader(String method,String url)
        throws MalformedURLException
    {
        this(
            method,
            URLParser.parseProtocol(url),
            URLParser.parseHost(url),
            URLParser.parsePort(url),
            URLParser.parseFile(url));
    }

    public void read(HTTPInputStream in) throws IOException
    {
        read(in,false); // Always read a request, of course.
    }

    /**
     * INTERNAL: Writes the request line and the headers to the output stream.
     */
    public void writeRequestLine(PrintWriter out)
        throws Exception
    {
		// Write the HTTP command.
		out.print(getMethod());
		out.write(SP);
		out.print(getRequestURI());
		out.write(SP);
		out.write(VERSION);
		out.write(CRLF);
    }

    /**
     * INTERNAL: Adds any missing request headers and writes all of the headers to the stream.
     */
    public void writeStandardHeaders(PrintWriter out)
        throws IOException
    {
        // Add the standard headers.
	    if (!containsHeader(HeaderConstants.HOST))
	    {
	        String port = (getPort() >= 0) ? ":"+getPort() : "";
	        setHeaderValue(HeaderConstants.HOST,getHost()+port);
	    }
	    if (!containsHeader(HeaderConstants.ACCEPT))
	    {
	        setHeaderValue(HeaderConstants.ACCEPT,HeaderConstants.DEFAULT_ACCEPT);
	    }
	    if (!containsHeader(HeaderConstants.USER_AGENT))
	    {
	        setHeaderValue(HeaderConstants.USER_AGENT,HeaderConstants.DEFAULT_USER_AGENT);
	    }
	    // NOTE: The default connection value is 'close'.
	    if (!containsHeader(HeaderConstants.CONNECTION))
	    {
	        setHeaderValue(HeaderConstants.CONNECTION,HeaderConstants.DEFAULT_CONNECTION);
	    }
		// Write the headers (except for content type and content length).
		Iterator i = iterateHeaders();
		while (i.hasNext())
		{
		    HTTPHeader h = (HTTPHeader)i.next();
		    if (    (h.getName().equals(HeaderConstants.CONTENT_LENGTH)) ||
		            (h.getName().equals(HeaderConstants.CONTENT_TYPE)))
		        continue;
		    // Write out the header and CRLF...
		    h.write(out);
		    out.write(CRLF);
		}
    }

    /**
     * INTERNAL - Writes the remaining headers.
     */
    public void finishHeaders(PrintWriter out)
    {
        // Write the content type and the content length last.
        // Some web servers need these to be last (like IIS).
        // TODO: Use constants for the header names.
        out.print("Content-Type: ");
        out.print(getContentType());
        out.write(CRLF);
        out.print("Content-Length: "+getContentLength());
        out.write(CRLF);

        // Write the blank line before the content.
        out.write(CRLF);
        out.flush();
    }
}
