package org.yajul.net;

import org.yajul.util.StringUtil;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the components of a URL without all of the 'fancy' capabilities of
 * java.net.URL.
 */
public class GenericURL implements java.io.Serializable
{
    private String protocol;
    private int port;
    private String host;
    private String file;
    /**
     * The url in string form.  Lazily created. *
     */
    private String url;

    private static Map DEFAULT_PORT_BY_PROTOCOL = new HashMap();

    static
    {
        DEFAULT_PORT_BY_PROTOCOL.put("http", new Integer(80));
        DEFAULT_PORT_BY_PROTOCOL.put("https", new Integer(443));
        DEFAULT_PORT_BY_PROTOCOL.put("ftp", new Integer(21));
    }

    public GenericURL(String stringURL)
            throws MalformedURLException
    {
        this(stringURL,true);
    }

    public GenericURL(String stringURL, boolean protocolRequired)
            throws MalformedURLException
    {
        if (protocolRequired)
            URLParser.parse(stringURL, this);
        else
            URLParser.parseRelative(stringURL, this);
    }

    public GenericURL(String host, int port, String protocol, String file)
            throws MalformedURLException
    {
        setHost(host);
        setPort(port);
        setProtocol(protocol);
        setFile(file);
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
        url = null;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        url = null;
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    /**
     * Returns true if the port was specified in the url.
     *
     * @return true if the port was specified in the url.
     */
    public boolean isPortSpecified()
    {
        return !(port == URLParser.NO_PORT);
    }

    /**
     * Returns the default port for the protocol, if the port was not specified
     * in the url (e.g. port 80 for http).
     *
     * @return the default port for the protocol, if the port was not specified
     *         in the url.
     */
    public int getDefaultPort()
    {
        if (isPortSpecified())
        {
            return port;
        }
        else
        {
            Integer p = (Integer) DEFAULT_PORT_BY_PROTOCOL.get(protocol);
            if (p != null)
                return p.intValue();
            else
                return port;
        }
    }

    public void setPort(int port)
    {
        this.port = port;
        url = null;
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
        url = null;
    }

    public String toString()
    {
        if (url == null)
        {
            StringBuffer buf = new StringBuffer();
            if (!StringUtil.isEmpty(protocol))
            {
                buf.append(protocol);
                buf.append(URLParser.PROTOCOL_SEPARATOR);
                StringUtil.appendIfNotEmpty(host, buf);
                if (isPortSpecified())
                    buf.append(":").append(Integer.toString(port));
            }
            StringUtil.appendIfNotEmpty(file, buf);
            url = buf.toString();
        }
        return url;
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof GenericURL))
            return false;
        else
            return o.toString().equals(toString());
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public String toExternalForm()
    {
        return toString();
    }
}
