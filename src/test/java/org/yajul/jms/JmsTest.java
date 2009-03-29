package org.yajul.jms;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.yajul.embedded.EmbeddedJBossTestCase;
import org.yajul.embedded.UnitTestJndiConstants;
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

        ConnectionFactoryReference factoryReference = new ConnectionFactoryReference(ic, UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        final DestinationReference testTopic = new DestinationReference(ic, "topic/testTopic");

        JmsTemplate runner = new JmsTemplate(factoryReference);
        runner.doAction(new JmsTemplate.JmsAction<Boolean>() {
            public Boolean run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicSubscriber sub = ctx.createSubscriber(ses, testTopic.getTopic(), null);
                TopicPublisher pub = ctx.createPublisher(ses, testTopic.getTopic());
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

    public void testResponder() throws Exception {
        InitialContext ic = new InitialContext();
        ConnectionFactoryReference factoryReference = new ConnectionFactoryReference(ic,
                UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        final DestinationReference destinationReference = new DestinationReference(ic,"topic/testTopic");
        MessageReceiver receiver = new MessageReceiver(factoryReference,destinationReference,new MessageListener() {

            public void onMessage(Message message) {
                System.out.println("message = " + message);
            }
        }, null, 2000);
        receiver.start(null,null);

        JmsTemplate runner = new JmsTemplate(factoryReference);
        runner.doAction(new JmsTemplate.JmsAction<Boolean>() {
            public Boolean run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicPublisher pub = ctx.createPublisher(ses, destinationReference.getTopic());
                con.start();    // Don't forget to start the connection.
                TextMessage msg = ses.createTextMessage("hello there");
                pub.send(msg);
                return null;
            }
        });
    }

    public static class QueueListener {
        private ConnectionFactoryReference factoryReference;


    }
    
    public static class Responder implements MessageListener {
        public void onMessage(Message message) {
        }
    }
    public static Test suite() {
        return new TestSuite(JmsTest.class);
    }

}
