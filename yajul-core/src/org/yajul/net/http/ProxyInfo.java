
package org.yajul.net.http;

/**
 * Encapsulates HTTPConstants proxy information.
 */
public class ProxyInfo
{
	private String  host;
	private int     port;
	private String  user;
	private String  password;

	public ProxyInfo(String host,int port,String user,String password)
	{
	    this.host = host;
	    this.port = port;
	    this.user = user;
	    this.password = password;
	}

	public ProxyInfo(ProxyInfo info)
	{
	    this(info.host,info.port,info.user,info.password);
	}

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
