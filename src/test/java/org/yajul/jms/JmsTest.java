package org.yajul.jms;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.yajul.embedded.EmbeddedJBossTestCase;
import org.yajul.jndi.JndiReference;

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

        ConnectionFactoryReference factoryReference = new ConnectionFactoryReference(ic, "java:JmsXA");
        final JndiReference<Topic> testTopic = new JndiReference<Topic>(ic, "topic/testTopic");

        JmsTemplate runner = new JmsTemplate(factoryReference);
        runner.doAction(new JmsTemplate.JmsAction<Boolean>() {
            public Boolean run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicSubscriber sub = ctx.createSubscriber(ses, testTopic.getObject(), null);
                TopicPublisher pub = ctx.createPublisher(ses, testTopic.getObject());
                con.start();    // Don't forget to start the connection.
                TextMessage msg = ses.createTextMessage("hello there");
                pub.send(msg);
                Message received = sub.receive();
                TextMessage textMessage = (TextMessage) received;
                String text = textMessage.getText();
                System.out.println("text=" + text);
                assertEquals("hello there", text);
                return null;
            }
        });
    }

    public static Test suite() {
        return new TestSuite(JmsTest.class);
    }

}
