package org.yajul.net;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * Represents a connection accepted by an AbstractServerSocketListener
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:46:47 AM
 * @author jdavis
 */
public abstract class AbstractClientConnection
{
    /**
     * The default buffer size for input and output streams.
     */
    public final static int DEFAULT_BUFFER_SIZE = 1024;

    private AbstractServerSocketListener listener;
    private Socket socket;
    /** The response stream. */
    private OutputStream out;       // Response stream server->client.
    /** The request stream. **/
    private InputStream in;         // Request stream client->server.
    /** True if the input and output streams are being buffered. **/
    private boolean buffered;

    public AbstractClientConnection(AbstractServerSocketListener listener,
                                    Socket socket) throws IOException
    {
        this.listener = listener;
        this.socket = socket;

        socket.setSoTimeout(listener.getConnectionTimeout());

        // Get the streams from the socket while we're still on the listener
        // thread.
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }


    public void initialize(AbstractServerSocketListener listener)
    {
        this.listener = listener;
    }

    /**
     * Notifies the server that this connection has been closed.
     */
    public void onClose()
    {
        listener.clientClosed(this);
    }

    public void close()
    {
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                listener.unexpected(e);
            }
            out = null;
        }
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                listener.unexpected(e);
            }
            in = null;
        }
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                listener.unexpected(e);
            }
            socket = null;
        }
        onClose();
    }


    protected InputStream getInputStream()
    {
        return in;
    }

    protected OutputStream getOutputStream()
    {
        return out;
    }
    /**
     * Create simple buffers on the input and output streams.  Invoke this from
     * either the constructor or the 'run()' method to add buffering (recommended).
     */
    protected void addStreamBuffers()
    {
        if (!buffered)  // Don't buffer twice.
        {
            in = new BufferedInputStream(in, DEFAULT_BUFFER_SIZE);
            out = new BufferedOutputStream(out, DEFAULT_BUFFER_SIZE);
            buffered = true;
        }
    }


    /**
     * Returns the incoming client socket.
     * @return The socket.
     */
    protected Socket getSocket()
    {
        return socket;
    }

    /**
     * Returns the server listener that is managing this client connection.
     * @return The server listener.
     */
    protected AbstractServerSocketListener getListener()
    {
        return listener;
    }

    /**
     * Stops and closes the client connection.
     */
    public void shutdown()
    {
        onClose();
    }
}
