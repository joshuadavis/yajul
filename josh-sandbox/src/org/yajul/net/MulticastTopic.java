/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 10:46:15 AM
 */
package org.yajul.net;

import org.yajul.io.ObjectStreamHelper;
import org.yajul.log.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastTopic
{
    private static Logger log = Logger.getLogger(MulticastTopic.class);

    private InetAddress group;
    private int port;
    private MulticastSocket socket;

    public MulticastTopic(InetAddress group, int port)
    {
        this.group = group;
        this.port = port;
    }

    /**
     * Starts up the topic connection.
     * @throws IOException if there was an error while creating the socket
     * or while joining the IP multicast group.
     */
    public void start() throws IOException
    {
        socket = new MulticastSocket(port);
        socket.joinGroup(group);
    }


    public void stop()
    {
        socket.close();
        socket = null;
    }

    public void send(Object obj)
            throws IOException
    {
        byte[] bytes = ObjectStreamHelper.serialize(obj);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, port);
        if (socket == null)
            start();
        socket.send(packet);
    }

    public Object receive()
            throws IOException, ClassNotFoundException
    {
        byte[] bytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        if (socket == null)
            start();
        socket.receive(packet);
        if (log.isDebugEnabled())
        {
            log.debug("Received : " + packet.getLength() +
                    " bytes from " + packet.getAddress() +
                    ":" + packet.getPort() +
                    " " + packet.getSocketAddress());
        }
        return ObjectStreamHelper.deserialize(bytes);
    }
}
