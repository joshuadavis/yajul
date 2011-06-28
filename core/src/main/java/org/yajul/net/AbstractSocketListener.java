package org.yajul.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Manages a set of client connections accepted via a server socket.  Provides
 * a base class for TCP/IP servers of various types.
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:08:10 AM
 *
 * @author jdavis
 */
public abstract class AbstractSocketListener {
    private static Logger log = LoggerFactory.getLogger(AbstractSocketListener.class);

    /**
     * Set max connections to this value to provide unlimited connections.  Note:
     * if this is set, the VM may encounter problems when too many threads are
     * started.
     */
    public final static int UNLIMITED_CONNECTIONS = Integer.MAX_VALUE;

    /**
     * The default log size is 20.
     */
    public final static int DEFAULT_BACKLOG = 20;

    /**
     * The default connection timeout.
     */
    public final static int DEFAULT_CONNECTION_TIMEOUT = 900000;

    /**
     * The default connection waiting period.
     */
    public final static int DEFAULT_CONNECTION_WAIT = 3000;

    public static final int WAIT_TIMEOUT = 5000;

    /**
     * The port the server will listen on. *
     */
    private final int port;

    /**
     * The server socket that the proxy will listen on. *
     */
    private final ServerSocket socket;

    /**
     * The thread pool to use for incoming connections and the main listener. *
     */
    private final ExecutorService executor;

    /**
     * Active connections to clients. *
     */
    private final List<AbstractClientConnection> clientConnections = new ArrayList<AbstractClientConnection>();

    /**
     * True if a shutdown has been requested. *
     */
    private boolean shutdownRequested = false;
    /**
     * The maximum number of connections the server will accept. *
     */
    private int maxConnections = UNLIMITED_CONNECTIONS;

    /**
     * True if clients should be automatically disconnected when
     * the maximum number of client connections is reached.  False
     * will cause the server to wait until  there is an available connection
     * (a.k.a. 'rude mode').*
     */
    private boolean rejectIfUnavailable = false;
    /**
     * The socket timeout for client connections. *
     */
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    // -- Server statistics --
    private int connectionsAccepted = 0;
    private int connectionsRejected = 0;

    /**
     * Creates a new server socket listener on the specified port.
     *
     * @param port The IP port to listen on.
     * @throws IOException if something goes wrong.
     */
    public AbstractSocketListener(int port, ExecutorService executorService) throws IOException {
        this.executor = executorService;
        this.socket = new ServerSocket(port);
        this.port = port;
        log.info("Listener on port " + this.port + " initialized.");
    }

    /**
     * Returns the port number this server is listening on.
     *
     * @return The port number this server is listening on.
     */
    public int getPort() {
        return port;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Returns the maximum number of connections allowed by this server.
     *
     * @return the maximum number of connections
     */
    public int getMaxConnections() {
        synchronized (this) {
            return maxConnections;
        }
    }

    /**
     * Sets the maximum number of active client connections the server
     * will handle.
     *
     * @param maxConnections The maximum number of active client connections.
     */
    public void setMaxConnections(int maxConnections) {
        synchronized (this) {
            this.maxConnections = maxConnections;
        }
    }

    /**
     * Returns the number of connections accepted so far.
     *
     * @return The number of connections accepted by the server.
     */
    public int getConnectionsAccepted() {
        synchronized (this) {
            return connectionsAccepted;
        }
    }

    /**
     * Returns the number of connections rejected so far.
     *
     * @return The number of connections rejected by the server.
     */
    public int getConnectionsRejected() {
        synchronized (this) {
            return connectionsRejected;
        }
    }

    public void start() {
        synchronized (this) {
            if (shutdownRequested)
                throw new IllegalStateException("Shutdown requested!  Cannot start!");
            executor.submit(new ListenerRunnable());
        }
    }

    /**
     * Stops the server, and any active clients.
     */
    public void shutdown() {
        synchronized (clientConnections) {
            AbstractClientConnection[] clients = clientConnections.toArray(new AbstractClientConnection[clientConnections.size()]);
            for (int i = 0; i < clients.length; i++) {

                try {
                    clients[i].shutdown();
                } catch (Throwable e) {
                    unexpected(e);
                }
            }
        }

        clearAllClients();

        shutdownRequested = true;
        try {
            socket.close();
        } catch (IOException ioex) {
            unexpected(ioex);
        }
    }

    /**
     * Returns true if client connections should be closed if the maximum
     * number of clients has been reached.
     *
     * @return true if client connections should be closed if the maximum
     *         number of clients has been reached.
     */
    public boolean isRejectIfUnavailable() {
        synchronized (this) {
            return rejectIfUnavailable;
        }
    }

    /**
     * Enables or disables 'rude' treatment of clients when the maximum number
     * of clients has been reached.  If enabled, incoming client connections
     * will be closed when the maximum number of clients has been reached.
     *
     * @param rejectIfUnavailable true if incoming clients are to be rejected when the server is busy
     */
    public void setRejectIfUnavailable(boolean rejectIfUnavailable) {
        synchronized (this) {
            this.rejectIfUnavailable = rejectIfUnavailable;
        }
    }

    /**
     * Clients are required to call this method when they shut down.
     *
     * @param client The client that has stopped.
     */
    public void clientClosed(AbstractClientConnection client) {
        removeClient(client);
    }

    /**
     * Returns the client socket connection timeout.
     *
     * @return The client socket connection timeout.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
    }

    /**
     * Accept the incoming connection and create a client connection object.
     *
     * @param incoming The incoming socket.
     * @return A new client connection object.
     * @throws IOException if something goes wrong.
     */
    protected abstract AbstractClientConnection acceptClient(Socket incoming) throws IOException;

    /**
     * Handle an unexpected exception.
     *
     * @param t The unexpected exception.
     */
    protected abstract void unexpected(Throwable t);

    /**
     * Returns true if the listener should accept the client, false if not.
     * Subclasses can override this to add their own behavior.
     * @param incoming the incoming client socket
     * @return true if the listener should accept the client, false if not.
     */
    @SuppressWarnings({"UnusedParameters"})
    protected boolean shouldAccept(Socket incoming) {
        return true;
    }

    private void doAccept(Socket incoming) throws IOException {
        final AbstractClientConnection client = acceptClient(incoming);
        if (client == null) {
            incoming.close();
            log.info("Client rejected: acceptClient() returned null.");
        }
        synchronized (this) {
            connectionsAccepted++;
        }

        // Add the client to the list before initializing.
        addClient(client);

        try {
            // NOTE: The client *must not* start any threads until this method is called!
            assert client != null;
            client.initialize(this);
            client.start(); // GO!
            if (log.isDebugEnabled())
                log.debug("doAccept() : Client connection handler started.");
        } catch (Throwable t) {
            logException(t);
            try {
                removeClient(client);
                assert client != null;
                client.close();
            } catch (Throwable e) {
                logException(e);
            }
        }
    }

    private void logException(Throwable t) {
        log.error("Unexpected: " + t, t);
    }

    private void clearAllClients() {
        synchronized (clientConnections) {
            clientConnections.clear();
            clientConnections.notifyAll();
        }
    }

    private boolean removeClient(AbstractClientConnection client) {
        synchronized (clientConnections) {
            boolean found = clientConnections.remove(client);
            if (found)
                log.info("Client connection " + client + " removed.");
            clientConnections.notifyAll();
            return found;
        }
    }

    private void addClient(AbstractClientConnection client) {
        synchronized (clientConnections) {
            clientConnections.add(client);
            log.info("Client connection " + client + " added.");
            clientConnections.notifyAll();
        }
    }

    private synchronized void setShutdownRequested(boolean shutdownRequested) {
        this.shutdownRequested = shutdownRequested;
    }

    private synchronized boolean isShutdownRequested() {
        return shutdownRequested;
    }

    private void incrementRejected() {
        synchronized (this) {
            connectionsRejected++;
        }
    }

    private class ListenerRunnable implements Runnable {
        public void run() {
            while (!isShutdownRequested()) {
                try {
                    boolean reject = false;
                    synchronized (clientConnections) {
                        while ((clientConnections.size() > maxConnections)
                                && (!isShutdownRequested())) {
                            // If 'rude mode' is enabled, kick the client.
                            // Don't bother looping either.
                            if (isRejectIfUnavailable()) {
                                reject = true;
                                break;
                            }
                            // Otherwise, wait...
                            else {
                                log.info("Client connection limit reached, waiting...");
                                try {
                                    clientConnections.wait(WAIT_TIMEOUT);
                                } catch (InterruptedException e) {
                                    unexpected(e);
                                }
                            }
                        } // while
                    } // synchronized

                    if (log.isDebugEnabled())
                        log.debug("run() : Listening...");

                    // Accept a socket connection...
                    Socket incoming = socket.accept();

                    if (log.isDebugEnabled())
                        log.debug("run() : Client " + incoming.getInetAddress());

                    // If we're not already rejecting this connection, filter it...
                    if (!reject) {
                        reject = !shouldAccept(incoming);
                    }

                    if (reject) {
                        incoming.close();
                        incrementRejected();
                        log.error("Socket connection from " + incoming.getInetAddress() + " rejected!");
                        continue;   // Continue accepting other connections.
                    }

                    // Accept this client.
                    doAccept(incoming);
                } catch (SocketException e) {
/* 2004-02-25 [jsd] In JDK1.3, Socket doesn't have the isClosed() method.
                if (socket.isClosed() && shutdownRequested)
                    log.info("Listener on port " + port + ", server socket closed.");
                else
                    unexpected(e);
*/
                    if (isShutdownRequested())
                        log.info("Listener on port " + port + ", server socket closed.");
                    else
                        unexpected(e);
                    break;

                } catch (IOException e) {
                    unexpected(e);
                    break;
                }
            }
            setShutdownRequested(false);
        }
    }

}
