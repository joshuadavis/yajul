// $Id$
package org.yajul.net.xmlrpc.client;

import org.apache.log4j.Logger;
import org.yajul.io.Base64Encoder;
import org.yajul.net.http.HeaderConstants;
import org.yajul.net.http.Message;
import org.yajul.net.http.RequestHeader;
import org.yajul.net.http.client.HTTPClient;
import org.yajul.net.xmlrpc.XmlRpcWriter;
import org.yajul.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

/**
 * TODO: Add class javadoc
 * 
 * @author josh Apr 8, 2004 6:58:14 AM
 */
public class XmlRpcClient
{
    /**
     * A logger for this class.
     */
    private static final Logger log = Logger.getLogger(XmlRpcClient.class);

    private static final String DEFAULT_URI = "/RPC2";
    private static final String DEFAULT_PROTOCOL = "http://";

    private String auth;
    private URL url;
    private HTTPClient client;

    /** Construct a XML-RPC client for the specified hostname and port.
     * @param hostname The host name of the XML-RPC server to connect to.
     * @param port The port that the XML-RPC server is listening on.
     * @throws Exception If there is any kind of initialization
     * problem, such as a malformed url, etc.
     */
    public XmlRpcClient (String hostname, int port) throws Exception
    {
        url = new URL (DEFAULT_PROTOCOL+hostname+":"+port+DEFAULT_URI);
        client = new HTTPClient();
    }

    /**
     * Sets Authentication for this client. This will be sent as Basic Authentication header
     * to the server as described in <a href="http://www.ietf.org/rfc/rfc2617.txt">http://www.ietf.org/rfc/rfc2617.txt</a>.
     *
     * @param user     The username for basic authentication.
     * @param password The password for basic authentication.
     */
    public void setBasicAuthentication(String user, String password)
    {
        if (user == null || password == null)
            auth = null;
        else
        {
            String basicAuth = Base64Encoder.encode(user + ":" + password);
            auth = basicAuth.trim();
        }
    }

    public String getBasicAuth()
    {
        return auth;
    }

    public URL getUrl()
    {
        return url;
    }

    public void invoke(String method,List paramList) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlRpcWriter w = new XmlRpcWriter(new OutputStreamWriter(baos));
        w.writePrologue();
        w.writeRequest(method,paramList);
        w.flush();
        byte[] content = baos.toByteArray();
        RequestHeader req = new RequestHeader(RequestHeader.METHOD_POST,getUrl().toExternalForm());
        req.setHeaderValue(HeaderConstants.USER_AGENT,"YajulXMLRPC/0.1");
        req.setHeaderValue(HeaderConstants.HOST,getUrl().getHost());
        req.setHeaderValue(HeaderConstants.CONTENT_TYPE,"text/xml");
        if (!StringUtil.isEmpty(auth))
            req.setHeaderValue(HeaderConstants.AUTHORIZATION," Basic " + auth);
        Message m = client.processRequest(req,new ByteArrayInputStream(content));
        log.info("Response:\n" + new String(m.getContent()));
    }
}
