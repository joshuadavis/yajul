package org.yajul.net;

import java.util.Collections;
import java.util.List;

/**
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 2:09 PM
 */
public abstract class SingleClientTaskFactory implements ClientTaskFactory {
    public List<ClientTask> createClientTasks(ClientConnection clientConnection) {
        return Collections.singletonList(createClientTask(clientConnection));
    }

    public abstract ClientTask createClientTask(ClientConnection clientConnection);
}
