
package org.yajul.net;

import java.io.*;
import java.net.Socket;


/**
 * Wraps client and server sockets, providing extra features.
 */
public class SocketWrapper
{
    /**
     * The default buffer size for input and output streams.
     */
    public final static int DEFAULT_BUFFER_SIZE = 1024;

    private Socket socket;             // The underlying socket.
    private InputStream in;            // The input stream from the socket.
    private OutputStream out;          // The output stream of the socket.
    private String protocol;           // The protocol.
    private String host;               // The host name.
    private int port;                  // The port.
    private long connectTime;          // The time when the socket was connected.
    private long disconnectTime;       // The time when the socket was disconnected.

    /** Create a generic socket wrapper around the given socket. */
    public SocketWrapper(Socket s) throws IOException
    {
        setSocket(s);
    }

    /** Create a socket wrapper in a 'closed' state. */
    public SocketWrapper()
    {
        socket = null;
        in = null;
        out = null;
        protocol = null;
        host = null;
        port = 0;
        connectTime = -1;
        disconnectTime = -1;
    }

    /** Create an initialized socket wrapper (client socket). */
    public SocketWrapper(boolean ssl, String protocol, String host, int port)
    {
        this();
        initialize(ssl, protocol, host, port);
    }

    /**
     * Initializes the connection information.
     */
    protected void initialize(boolean ssl, String protocol, String host, int port)
    {
        close();
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    /**
     * Returns true if the connection has been opened.
     * @return true if the connection has been opened.
     */
    public boolean isConnected()
    {
        return socket != null;
    }

    /** Returns the protocol. */
    public String getProtocol()
    {
        return protocol;
    }

    /** Returns the port number. */
    public int getPort()
    {
        return port;
    }

    /** Returns the host name. */
    public String getHost()
    {
        return host;
    }

    /**
     * Returns the protocol host and port in URL form.
     * @return The host url.
     */
    public String getURL()
    {
        return protocol + "://" + host + ":" + Integer.toString(port);
    }

    /**
     * Create simple buffers on the input and output streams.
     */
    public void addStreamBuffers()
    {
        in = new BufferedInputStream(in, DEFAULT_BUFFER_SIZE);
        out = new BufferedOutputStream(out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Returns the input stream.
     */
    public InputStream getInputStream()
    {

        synchronized (this)
        {
            return in;
        }
    }

    /**
     * Returns the output stream.
     */
    public OutputStream getOutputStream()
    {
        synchronized (this)
        {
            return out;
        }
    }

    /**
     * Returns the underlying socket.
     */
    public Socket getSocket()
    {
        return socket;
    }

    /**
     * Returns the number of milliseconds the connection has been open for, or
     * -1 if the connection has never been opened.
     */
    public long getConnectDuration()
    {
        if (isConnected())
            return System.currentTimeMillis() - connectTime;
        else if ((disconnectTime > 0) && (connectTime > 0))
            return disconnectTime - connectTime;
        else
            return -1;
    }

    /**
     * Closes the socket.
     */
    public void close()
    {
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException ignore)
            {
            }
            in = null;
        }
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException ignore)
            {
            }
            out = null;
        }
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException ignore)
            {
            }
            socket = null;
            disconnectTime = System.currentTimeMillis();
        }
    }

    protected void setSocket(Socket s) throws IOException
    {
        connectTime = System.currentTimeMillis();
        disconnectTime = -1;
        socket = s;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }
}