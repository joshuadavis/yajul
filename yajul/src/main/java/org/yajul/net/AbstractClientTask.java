package org.yajul.net;

/**
 * Partial implementation of a client socket task.
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 1:57 PM
 */
public abstract class AbstractClientTask implements ClientTask {
    private ClientConnection connection;

    protected AbstractClientTask(ClientConnection connection) {
        this.connection = connection;
    }

    protected ClientConnection getConnection() {
        return connection;
    }
}
