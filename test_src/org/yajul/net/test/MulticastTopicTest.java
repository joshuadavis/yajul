/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 10:53:23 AM
 */
package org.yajul.net.test;

import junit.framework.TestCase;
import org.yajul.net.MulticastTopic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

public class MulticastTopicTest  extends TestCase
{
    public MulticastTopicTest(String name)
    {
        super(name);
    }

    public void testStartAndStop() throws UnknownHostException, IOException
    {
        MulticastTopic topic = new MulticastTopic(
                InetAddress.getByName("235.235.235.235"),6060);
        topic.start();
        topic.stop();
    }

    public void testSendAndReceive() throws UnknownHostException, IOException,
        ClassNotFoundException
    {
        MulticastTopic topic = new MulticastTopic(
                InetAddress.getByName("235.235.235.235"),6060);
        topic.start();
        String x = "hello there (from the topic)!";
        topic.send(x);
        Object y = topic.receive();
        assertEquals(x,y);

        for(int i = 0; i < 1000; i++)
        {
            y = topic.receive();
            System.out.println("received: " + y);
        }

        topic.stop();
    }
}

