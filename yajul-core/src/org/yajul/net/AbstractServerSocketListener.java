package org.yajul.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Manages a set of client connections accepted via a server socket.  Provides
 * a base class for TCP/IP servers of various types.
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:08:10 AM
 * @author jdavis
 */
public abstract class AbstractServerSocketListener implements Runnable
{
    private static Logger log = Logger.getLogger(AbstractServerSocketListener.class);

    public static final int STATE_INITIALIZED = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 2;

    /** The port the server will listen on. **/
    private int port;

    /** The server socket that the proxy will listen on. **/
    private ServerSocket socket;

    /** Active connections to clients. **/
    private ArrayList clientConnections;

    private boolean shutdownRequested = false;
    private int state;
    private int connectionsAccepted = 0;
    private int clientLimit = Integer.MAX_VALUE;
    public static final int WAIT_TIMEOUT = 5000;

    public AbstractServerSocketListener(int port) throws IOException
    {
        this.port = port;
        socket = new ServerSocket(port);
        clientConnections = new ArrayList();
        state = STATE_INITIALIZED;
        log.info("Listener on port " + port + " initialized.");
    }

    public int getPort()
    {
        return port;
    }

    public int getClientLimit()
    {
        return clientLimit;
    }

    public void setClientLimit(int clientLimit)
    {
        this.clientLimit = clientLimit;
    }

    public ServerSocket getSocket()
    {
        return socket;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     Thread#run()
     */
    public void run()
    {
        state = STATE_RUNNING;
        while (!shutdownRequested)
        {
            try
            {
                synchronized (clientConnections)
                {
                    while ((clientConnections.size() > clientLimit)
                            && (!shutdownRequested))
                    {
                        log.info("Client connection limit reached, waiting...");
                        try
                        {
                            clientConnections.wait(WAIT_TIMEOUT);
                        }
                        catch (InterruptedException e)
                        {
                            unexpected(e);
                        }
                    }
                    if (shutdownRequested)
                        continue;
                }
                Socket incoming = socket.accept();
                connectionsAccepted++;
                AbstractClientConnection client = acceptClient(incoming);
                client.initialize(this);
                addClient(client);
            }
            catch (SocketException e)
            {
                if (socket.isClosed() && shutdownRequested)
                    log.info("Listener on port " + port + ", server socket closed.");
                else
                    unexpected(e);
                break;

            }
            catch (IOException e)
            {
                unexpected(e);
                break;
            }
        }
        shutdownRequested = false;
        state = STATE_STOPPED;
    }

    public int getConnectionsAccepted()
    {
        return connectionsAccepted;
    }

    public int getState()
    {
        return state;
    }

    public void shutdown()
    {
        synchronized (clientConnections)
        {
            for (Iterator iterator = clientConnections.iterator(); iterator.hasNext();)
            {
                AbstractClientConnection client = (AbstractClientConnection) iterator.next();
                client.shutdown();
            }
        }

        clearAllClients();

        shutdownRequested = true;
        try
        {
            getSocket().close();
        }
        catch (IOException ioex)
        {
            unexpected(ioex);
        }
    }

    private void clearAllClients()
    {
        synchronized (clientConnections)
        {
            clientConnections.clear();
            clientConnections.notifyAll();
        }
    }

    private boolean removeClient(AbstractClientConnection client)
    {
        synchronized (clientConnections)
        {
            boolean found = clientConnections.remove(client);
            clientConnections.notifyAll();
            return found;
        }
    }

    private void addClient(AbstractClientConnection client)
    {
        synchronized (clientConnections)
        {
            clientConnections.add(client);
            clientConnections.notifyAll();
        }
    }

    /**
     * Clients are required to call this method when they shut down.
     * @param client The client that has stopped.
     */
    public void clientClosed(AbstractClientConnection client)
    {
        removeClient(client);
    }

    protected abstract AbstractClientConnection acceptClient(Socket incoming) throws IOException;

    protected abstract void serverClosed();

    protected abstract void unexpected(Throwable t);
}
