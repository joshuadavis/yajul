package org.yajul.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.DestroyFailedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages a set of client connections accepted via a server socket.  Provides
 * a base class for TCP/IP servers of various types.
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:08:10 AM
 *
 * @author jdavis
 */
public class SocketListener {
    private static Logger log = LoggerFactory.getLogger(SocketListener.class);

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

    public static final int WAIT_TIMEOUT = 5000;

    /**
     * The server socket to listen on.
     */
    private final ServerSocket socket;

    /**
     * The thread pool to use for incoming connections and the main listener.
     */
    private final ExecutorService executor;

    /**
     * Active connections to clients.
     */
    private final List<ClientConnection> clientConnections = new ArrayList<ClientConnection>();

    /**
     * True if a shutdown has been requested.
     */
    private boolean shutdownRequested = false;
    /**
     * The maximum number of connections the server will accept.
     */
    private int maxConnections = UNLIMITED_CONNECTIONS;

    /**
     * True if clients should be automatically disconnected when
     * the maximum number of client connections is reached.  False
     * will cause the server to wait until  there is an available connection
     * (a.k.a. 'rude mode').
     */
    private boolean rejectIfUnavailable = false;
    /**
     * The socket timeout for client connections.
     */
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private ClientTaskFactory clientTaskFactory;

    private Lock lock = new ReentrantLock();
    private Condition listening = lock.newCondition();

    /**
     * Creates a new server socket listener on the specified port.
     *
     *
     * @param port The IP port to listen on.
     * @throws IOException if something goes wrong.
     */
    public SocketListener(InetAddress bindAddress, int port,
                          ExecutorService executorService,
                          ClientTaskFactory clientTaskFactory,
                          int backlog) throws IOException {
        this.executor = executorService;
        this.clientTaskFactory = clientTaskFactory;
        socket = new ServerSocket(port,backlog,bindAddress);
    }

    /**
     * Creates a new server socket listener on the specified port.
     *
     *
     * @param port The IP port to listen on.
     * @throws IOException if something goes wrong.
     */
    public SocketListener(InetAddress bindAddress, int port,
                          ExecutorService executorService,
                          ClientTaskFactory clientTaskFactory) throws IOException {
        this(bindAddress,port,executorService,clientTaskFactory, DEFAULT_BACKLOG);
    }

    /**
     * Returns the port number this server is listening on.
     *
     * @return The port number this server is listening on.
     */
    public int getPort() {
        return socket.getLocalPort();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void start() {
        synchronized (this) {
            if (shutdownRequested)
                throw new IllegalStateException("Shutdown requested!  Cannot start!");
            executor.submit(new Runnable() {
                public void run() {
                    listenerLoop();
                }
            });
        }

        lock.lock();
        try {
            listening.await();
            log.info("Started.");
        } catch (InterruptedException e) {
            log.warn("Interrupted: " + e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the server, and any active clients.
     */
    public void shutdown() {
        synchronized (clientConnections) {
            for (ClientConnection connection : clientConnections) {
                try {
                    connection.shutdown();
                } catch (Throwable e) {
                    unexpected(e);
                }
            }
            clientConnections.clear();
            clientConnections.notifyAll();
        }

        shutdownRequested = true;
        try {
            socket.close();
            log.info("Socket closed.");
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
    void clientClosed(ClientConnection client) {
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
     * Handle an unexpected exception.
     *
     * @param t The unexpected exception.
     */
    protected void unexpected(Throwable t) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error("Unexpected: " + t,t);
    }

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
        final ClientConnection client = new ClientConnection(this,incoming);
        synchronized (clientConnections) {
            clientConnections.add(client);
            log.info("Client connection " + client + " added.");
            clientConnections.notifyAll();
        }

        try {
            // NOTE: The client *must not* start any threads until this method is called!
            client.start();
            if (log.isDebugEnabled())
                log.debug("doAccept() : Client connection handler started.");
        } catch (Throwable t) {
            log.error("Unable to start client due to: " + t, t);
            try {
                client.close();
            } catch (Throwable e) {
                log.error("Unable to close client due to: " + e, e);
            }
        }
    }

    private boolean removeClient(ClientConnection client) {
        synchronized (clientConnections) {
            boolean found = clientConnections.remove(client);
            if (found)
                log.info("Client connection " + client + " removed.");
            clientConnections.notifyAll();
            return found;
        }
    }

    private synchronized void setShutdownRequested(boolean shutdownRequested) {
        this.shutdownRequested = shutdownRequested;
    }

    private synchronized boolean isShutdownRequested() {
        return shutdownRequested;
    }

    ClientTaskFactory getClientTaskFactory() {
        return clientTaskFactory;
    }

    private void listenerLoop() {
        log.info("BEGIN - Listener on port " + getPort());
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
                    log.debug("listenerLoop() : Waiting for a connection...");

                // Accept a socket connection...
                lock.lock();
                try {
                    listening.signalAll();
                } finally {
                    lock.unlock();
                }
                Socket incoming = socket.accept();

                if (log.isDebugEnabled())
                    log.debug("listenerLoop() : Client " + incoming.getInetAddress());

                // If we're not already rejecting this connection, filter it...
                if (!reject) {
                    reject = !shouldAccept(incoming);
                }

                if (reject) {
                    incoming.close();
                    log.error("Socket connection from " + incoming.getInetAddress() + " rejected!");
                    continue;   // Continue accepting other connections.
                }

                // Accept this client.
                doAccept(incoming);
            } catch (SocketException e) {
                log.warn("SocketException: " + e);
/* 2004-02-25 [jsd] In JDK1.3, Socket doesn't have the isClosed() method.
                if (socket.isClosed() && shutdownRequested)
                    log.info("Listener on port " + port + ", server socket closed.");
                else
                    unexpected(e);
*/
                if (isShutdownRequested())
                    log.info("Listener on port " + getPort() + ", server socket closed.");
                else
                    unexpected(e);
                break;

            } catch (Throwable e) {
                unexpected(e);
                break;
            }
        } // while
        setShutdownRequested(false);
        log.info("END - Listener on port " + getPort());
    }
}
