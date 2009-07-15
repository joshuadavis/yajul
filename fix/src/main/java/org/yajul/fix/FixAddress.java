package org.yajul.fix;

import java.net.SocketAddress;
import java.net.InetSocketAddress;

/**
 * The address of a FIX server.
 * <br>
 * User: josh
 * Date: Jul 15, 2009
 * Time: 1:44:58 AM
 */
public class FixAddress {
    private String host;
    private int port;
    private String beginString;

    public FixAddress(String host, int port, String beginString) {
        this.host = host;
        this.port = port;
        this.beginString = beginString;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public SocketAddress getSocketAddress() {
        return new InetSocketAddress(host,port);
    }
}
