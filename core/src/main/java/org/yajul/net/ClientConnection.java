package org.yajul.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the server side of an accepted client connection.
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:46:47 AM
 *
 * @author jdavis
 */
public class ClientConnection {

    private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);

    private final SocketListener listener;
    private Socket socket;
    private final List<TaskWrapper> taskWrappers;

    public ClientConnection(SocketListener listener,
                            Socket socket) throws IOException {
        this.listener = listener;
        this.socket = socket;
        this.taskWrappers = new ArrayList<TaskWrapper>();
        socket.setSoTimeout(listener.getConnectionTimeout());
    }

    private class TaskWrapper implements Runnable {
        private ClientTask task;

        private TaskWrapper(ClientTask task) {
            this.task = task;
        }

        public void run() {
            try {
                task.runClient();
            } catch (Throwable t) {
                unexpected(t);
            } finally {
                taskCompleted(this);
            }
        }
    }

    private void taskCompleted(TaskWrapper wrapper) {
        boolean shouldClose = false;
        synchronized (taskWrappers) {
            taskWrappers.remove(wrapper);
            shouldClose = taskWrappers.isEmpty();
        }
        if (shouldClose)
            close();
    }

    private TaskWrapper addWrapper(ClientTask task) {
        synchronized (taskWrappers) {
            TaskWrapper wrapper = new TaskWrapper(task);
            taskWrappers.add(wrapper);
            return wrapper;
        }
    }

    protected final void start() {
        List<ClientTask> tasks = getTasks();
        if (log.isDebugEnabled())
           log.debug("start() : tasks = " + tasks);
        for (ClientTask task : tasks) {
            TaskWrapper wrapper = addWrapper(task);
            taskWrappers.add(wrapper);
            if (log.isDebugEnabled())
               log.debug("start() : Launching " + wrapper);
            listener.getExecutor().execute(wrapper);
        }
    }

    protected List<ClientTask> getTasks() {
        return listener.getClientTaskFactory().createClientTasks(this);
    }

    /**
     * Handle an unexpected exception.
     *
     * @param t The unexpected exception.
     */
    protected void unexpected(Throwable t) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error("Unexpected: " + t, t);
    }

    /**
     * Notifies the server that this connection has been closed.
     */
    protected void onClose() {
        listener.clientClosed(this);
    }

    public void close() {
        closeSocket();
        onClose();
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                listener.unexpected(e);
            }
        }
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Returns the incoming client socket.
     *
     * @return The socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Stops and closes the client connection.
     */
    void shutdown() {
        closeSocket();
    }

    @Override
    public String toString() {
        return "ClientConnection{" +
                "socket=" + socket +
                '}';
    }
}
