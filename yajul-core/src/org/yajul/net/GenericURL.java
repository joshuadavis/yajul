package org.yajul.net;

import java.net.MalformedURLException;

import org.yajul.util.StringUtil;

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
    /** The url in string form.  Lazily created. **/
    private String url;

    public GenericURL(String stringURL)
            throws MalformedURLException
    {
        URLParser.parse(stringURL, this);
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
            StringUtil.appendIfNotEmpty(protocol, buf);
            buf.append(URLParser.PROTOCOL_SEPARATOR);
            StringUtil.appendIfNotEmpty(host, buf);
            if (port >= 0)
                buf.append(":").append(Integer.toString(port));
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
}
