
package org.yajul.net.http.client;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.yajul.net.http.HTTPConstants;
import org.yajul.net.http.HeaderConstants;
import org.yajul.net.http.Message;
import org.yajul.net.http.ProxyInfo;
import org.yajul.net.http.RequestHeader;

/**
 * This class allows the creation of HTTP connections and contains overall
 * settings that affect the HTTPConnection object.
 */
public class HTTPClient implements HTTPConstants
{
    /** Proxy information to be used by the client for all connections. **/
    private ProxyInfo proxyInfo;
    /** Number of milliseconds to wait on any connection created by this client. **/
    private int       socketTimeout;

    /**
     * Create a new HTTP Client.
     */
    public HTTPClient()
    {
    }

	/**
	 * Initialize proxy support.  If this is set, the proxy will be used for all subsequent
	 * HTTP/HTTPS communication.
	 */
	public void setProxy(
	    String host,int port,String user,String password)
	    throws Exception
	{
	    proxyInfo = new ProxyInfo(host,port,user,password);
	}

	/**
	 * Returns a copy of the proxy information, or null if none
	 * was specified.
	 */
	public ProxyInfo getProxyInfo()
	{
	    return proxyInfo;
	}
    /**
     * Sets the socket timeout for the client.
     */
    public void setSocketTimeout(int ms) { socketTimeout = ms; }

    /**
     * Returns the socket timeout for the client.
     */
    public int getSocketTimeout() { return socketTimeout; }

    /**
     * Issue and HTTP request on a new connection and receive the response.
     */
    public Message processRequest(RequestHeader req,InputStream content)
        throws Exception
    {
        // Get a connection to the host
        // TODO: If we are allowing keepalive, find the connection based on the protocol/host/port string.

        HTTPConnection con = new HTTPConnection(this,req.getProtocol(),req.getHost(),req.getPort());

        // Send the request, wait for the response, and then parse the headers.
        Message res = con.send(req,content);

        // The content can now be read from the response.

        return res;
    }

    public Message get(String url)
        throws Exception
    {
        // Create the GET request.
        RequestHeader req = new RequestHeader(METHOD_GET,url);
        req.setHeaderValue(HeaderConstants.CONNECTION,"close");
        return processRequest(req,null);
    }

    /**
     * HTTP post the content, and use the length of the content byte
     * array as the content-length header field.
     */
	public Message post(String url,byte[] content)
	    throws Exception
	{
        RequestHeader req = new RequestHeader(METHOD_POST,url);
        return post(req, content);
    }

    public Message post(RequestHeader req, byte[] content)
            throws Exception
    {
        req.setHeaderValue(HeaderConstants.CONTENT_LENGTH,Integer.toString(content.length));
        return processRequest(req,new ByteArrayInputStream(content));
    }
}
