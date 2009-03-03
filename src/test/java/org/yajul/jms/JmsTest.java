package org.yajul.jms;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.yajul.jms.JmsAttributes;
import org.yajul.embedded.EmbeddedJBossTestCase;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.*;

/**
 * Test JMS code.
 * <br>User: Josh
 * Date: Mar 3, 2009
 * Time: 6:54:38 AM
 */
public class JmsTest extends EmbeddedJBossTestCase {

    public void testJmsAttributes() throws NamingException, JMSException {
        InitialContext ic = new InitialContext();
        JmsAttributes testTopic = new JmsAttributes(ic,"java:JmsXA","topic/testTopic");
        ConnectionFactory tcf = testTopic.getConnectionFactory();
        Topic topic = testTopic.getTopic();

        // Subscribe and publish one message.
        TopicConnection con = testTopic.createTopicConnection();
        TopicSession ses = testTopic.createTopicSession(con);
        TopicSubscriber sub = testTopic.createSubscriber(ses);
        TopicPublisher pub = testTopic.createPublisher(ses);
        con.start();    // Don't forget to start the connection.
        TextMessage msg = ses.createTextMessage("hello there");
        pub.send(msg);
        Message received = sub.receive();
        TextMessage textMessage = (TextMessage) received;
        String text = textMessage.getText();
        System.out.println("text=" + text);
        JmsHelper.close(sub,ses,con);
    }
    
    public static Test suite() {
        return new TestSuite(JmsTest.class);
    }
}
