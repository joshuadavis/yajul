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
 *
 * @author jdavis
 */
public abstract class AbstractClientConnection {
    /**
     * The default buffer size for input and output streams.
     */
    public final static int DEFAULT_BUFFER_SIZE = 1024;

    private AbstractSocketListener listener;
    private Socket socket;

    public AbstractClientConnection(AbstractSocketListener listener,
                                    Socket socket) throws IOException {
        this.listener = listener;
        this.socket = socket;

        socket.setSoTimeout(listener.getConnectionTimeout());
    }


    public void initialize(AbstractSocketListener listener) {
        this.listener = listener;
    }

    /**
     * Implementors will obtain threads and start running.
     */
    public abstract void start();

    /**
     * Notifies the server that this connection has been closed.
     */
    public void onClose() {
        listener.clientClosed(this);
    }

    public void close() {
        OutputStream out = getOutputStreamNoThrow();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                listener.unexpected(e);
            }
        }
        InputStream in = getInputStreamNoThrow();
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                listener.unexpected(e);
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                listener.unexpected(e);
            }
            socket = null;
        }
        onClose();
    }

    private InputStream getInputStreamNoThrow() {
        try {
            return getInputStream();
        } catch (IOException e) {
            listener.unexpected(e);
            return null;
        }
    }

    private OutputStream getOutputStreamNoThrow() {
        try {
            return getOutputStream();
        } catch (IOException e) {
            listener.unexpected(e);
            return null;
        }
    }

    protected Socket usurpSocket() {
        Socket rv = socket;
        if (rv == null)
            throw new IllegalStateException("There is no socket to usurp!");
        // Set the socket and the input streams to null, so they won't get closed.
        socket = null;
        // Close this connection.
        close();
        // Return the socket.
        return rv;
    }

    protected InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    protected OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Returns the incoming client socket.
     *
     * @return The socket.
     */
    protected Socket getSocket() {
        return socket;
    }

    /**
     * Returns the server listener that is managing this client connection.
     *
     * @return The server listener.
     */
    protected AbstractSocketListener getListener() {
        return listener;
    }

    /**
     * Stops and closes the client connection.
     */
    public void shutdown() {
        onClose();
    }
}
