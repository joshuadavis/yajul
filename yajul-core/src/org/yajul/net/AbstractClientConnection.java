package org.yajul.net;

/**
 * TODO: Add javadoc
 * User: jdavis
 * Date: Dec 11, 2003
 * Time: 11:46:47 AM
 * @author jdavis
 */
public abstract class AbstractClientConnection
{
    private AbstractServerSocketListener listener;

    public void initialize(AbstractServerSocketListener listener)
    {
        this.listener = listener;
    }

    public void shutdown()
    {
        onClose();
    }

    public void onClose()
    {
        listener.clientClosed(this);
    }
}
