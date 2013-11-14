package org.yajul.net;

import java.util.List;

/**
 * Creates the ClientTask objects for a given client connection.
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 2:02 PM
 */
public interface ClientTaskFactory {

    /**
     * Create the tasks that will process the client socket streams.
     *
     * @param clientConnection the client connection (with the socket)
     * @return List of client tasks.
     */
    List<ClientTask> createClientTasks(ClientConnection clientConnection);
}
