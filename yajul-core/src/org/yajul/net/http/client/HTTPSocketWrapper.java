
package org.yajul.net.http.client;

import org.apache.log4j.Logger;
import org.yajul.io.Base64Encoder;
import org.yajul.net.SocketFactory;
import org.yajul.net.SocketWrapper;
import org.yajul.net.URLParser;
import org.yajul.net.http.HTTPConstants;
import org.yajul.net.http.ProxyInfo;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Base class for making HTTPConstants client connections.  Wraps the the socket and the
 * socket factory.
 *
 * @author  joshuad
 * @version 
 */
public class HTTPSocketWrapper extends SocketWrapper implements HTTPConstants
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(HTTPSocketWrapper.class);

    /** Flag that indicates HTTPS. */
    private     boolean                     secure = false;
    /** The per-instance proxy information.  Overrides the global default
     * proxy information. */
    private     ProxyInfo                   proxy = null;
    /** Basic HTTPConstants authorization. */
    protected String                        auth;
    /** Keepalive state of the current connection. */
    protected boolean                       keepalive;

    /** Creates new HTTPSocketWrapper */
    public HTTPSocketWrapper()  { }
    
    public HTTPSocketWrapper(URL url)
    {        
        String protocol = url.getProtocol();
        secure = protocol.equalsIgnoreCase(PROTOCOL_HTTPS);
        String host = url.getHost();
        int port = url.getPort();
        if (port < 1)
            port = (secure) ? HTTPConstants.DEFAULT_HTTPS_PORT : HTTPConstants.DEFAULT_HTTP_PORT;
        initialize(secure,protocol,host,port);
    }
    
    /**
     * Sets the 'local' proxy information, this will override the
     * global defaul setting.
     */
    public void setProxyInfo(ProxyInfo info)
        { proxy = info; }

    /** Returns the 'local' proxy information, or the global default,
     * if this was not specified locally. */
    public ProxyInfo getProxyInfo()
    {
        return proxy;
    }
    
    /**
     * Returns the host name or IP address that the socket will actually be
     * opened on.  This will be the name of the proxy server, if one is specified.
     * @return The host name or IP address.
     */
    public String getSocketHost()
    {
        ProxyInfo info = getProxyInfo();
        return (info == null) ? getHost() : info.getHost();
    }

    /**
     * Returns the port that the socket will actually be
     * opened on.  This will be the port on the proxy server, if one is specified.
     * @return The socket number
     */
    public int getSocketPort()
    {
        ProxyInfo info = getProxyInfo();
        return (info == null) ? getPort() : info.getPort();
    }

    /**
     * Initializes the connection information.
     */
    protected void initialize(String protocol,String host,int port)
    {
        secure = (protocol.equalsIgnoreCase(PROTOCOL_HTTPS));
            
        if (port == URLParser.NO_PORT)
            port = (secure) ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT;
        
        super.initialize(secure,protocol,host,port);
        
    }

    /** Returns the basic proxy authorization string, or null if there is no proxy. */
    public String getBasicProxyAuthorization()
    {
        if (proxy != null)
        {
            String auth = proxy.getUser() + ":" + proxy.getPassword();
            return "Basic " + Base64Encoder.encode(auth);
        }
        else
            return null;
    }
    
    /**
     * Opens the connection using the host information already set.
     * @exception java.io.IOException Thrown if there was a problem creating the socket.
     */
    public void open()
        throws java.io.IOException
    {
        if (isConnected())
            return;
        
        // When using a proxy, get a socket conection to the proxy server
        // instead of the desired HTTPConstants server.
        String host = getSocketHost();
        int    port = getSocketPort();

        try
        {
            setSocket(SocketFactory.getInstance().createSocket(secure,host,port));
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error(e,e);
            throw new IOException("Unable to connect due to: " + e.getMessage());
        }

        // Add stream buffers.
        addStreamBuffers();
    }

    /** Close the socket. */
    public void close()
    {
        super.close();
    }
    
}