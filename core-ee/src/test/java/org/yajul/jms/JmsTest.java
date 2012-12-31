package org.yajul.jms;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yajul.jndi.UnitTestJndiConstants;
import org.yajul.io.ByteArrayWrapper;
import org.yajul.io.SerializableWrapper;

import static org.junit.Assert.*;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Test JMS code.
 * <br>User: Josh
 * Date: Mar 3, 2009
 * Time: 6:54:38 AM
 */
@RunWith(Arquillian.class)
public class JmsTest {

    @Test
    public void testBasicJmsJndiObjects() throws Exception {
        InitialContext ic = new InitialContext();
        ConnectionFactoryProvider factoryProvider = new ConnectionFactoryProvider(ic, UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        ConnectionFactory factory = factoryProvider.getTopicConnectionFactory();
        assertNotNull(factory);
    }

    @Test
    public void testJmsAttributes() throws NamingException, JMSException {
        InitialContext ic = new InitialContext();

        ConnectionFactoryProvider factoryReference = new ConnectionFactoryProvider(ic, UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        final DestinationProvider testTopic = new DestinationProvider(ic, "jms/testTopic");

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

    public void testJmsObject() throws NamingException, JMSException {
        InitialContext ic = new InitialContext();

        ConnectionFactoryProvider factoryReference = new ConnectionFactoryProvider(ic, UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        final DestinationProvider testTopic = new DestinationProvider(ic, "jms/testTopic");


        final Thing thing = createThing();

        JmsTemplate runner = new JmsTemplate(factoryReference);
        Thing t = runner.doAction(new JmsTemplate.JmsAction<Thing>() {
            public Thing run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicSubscriber sub = ctx.createSubscriber(ses, testTopic.getTopic(), null);
                TopicPublisher pub = ctx.createPublisher(ses, testTopic.getTopic());
                con.start();    // Don't forget to start the connection.
                ObjectMessage msg = ses.createObjectMessage(thing);
                pub.send(msg);
                Message received = sub.receive();
                return JmsHelper.getObject(received, Thing.class, true);
            }
        });

        final SerializableWrapper<Thing> wrapper = new ByteArrayWrapper<Thing>(thing);
        Thing t2 = runner.doAction(new JmsTemplate.JmsAction<Thing>() {
            public Thing run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicSubscriber sub = ctx.createSubscriber(ses, testTopic.getTopic(), null);
                TopicPublisher pub = ctx.createPublisher(ses, testTopic.getTopic());
                con.start();    // Don't forget to start the connection.
                ObjectMessage msg = ses.createObjectMessage(wrapper);
                pub.send(msg);
                Message received = sub.receive();
                return JmsHelper.getObject(received, Thing.class, true);
            }
        });

        assertEquals(thing, t2);
    }

    public void testResponder() throws Exception {
        InitialContext ic = new InitialContext();
        final ConnectionFactoryProvider factoryReference = new ConnectionFactoryProvider(ic,
                UnitTestJndiConstants.JMS_CONNECTION_FACTORY);
        final DestinationProvider destinationReference = new DestinationProvider(ic, "jms/testTopic");
        MessageReceiver receiver = new MessageReceiver(factoryReference, destinationReference, new MessageListener() {
            public void onMessage(Message message) {
                MessageSender.sendReply(null, UnitTestJndiConstants.JMS_CONNECTION_FACTORY, message, "poot!");
            }
        }, null, 2000);
        receiver.start(null, null);

        JmsTemplate runner = new JmsTemplate(factoryReference);
        runner.doAction(new JmsTemplate.JmsAction<Boolean>() {
            public Boolean run(JmsContext ctx) throws JMSException {
                TopicConnection con = ctx.createTopicConnection();
                TopicSession ses = ctx.createTopicSession(con);
                TopicPublisher pub = ctx.createPublisher(ses, destinationReference.getTopic());
                con.start();    // Don't forget to start the connection.
                Topic replyTo = ses.createTemporaryTopic();
                TextMessage msg = ses.createTextMessage("hello there");
                msg.setJMSReplyTo(replyTo);
                TopicSubscriber sub = ses.createSubscriber(replyTo);
                pub.send(msg);
                Message reply = sub.receive();

                System.out.println("reply: " + reply);
                return null;
            }
        });
    }


    private Thing createThing() {
        Thing t = new Thing();
        List<Foo> list = t.getFoos();
        addFoos(list);
        t.setBar(new Bar(3.14159, t));
        return t;
    }

    private static void addFoos(List<Foo> list) {
        for (int i = 0; i < 10; i++) {
            list.add(new Foo("foo-" + i, i));
        }
    }

    public static class Bar implements Serializable {
        private double factor;
        private Thing parent;

        public Bar(double factor, Thing parent) {
            this.factor = factor;
            this.parent = parent;
        }

        public double getFactor() {
            return factor;
        }

        public Thing getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Bar)) return false;

            Bar bar = (Bar) o;

            if (Double.compare(bar.factor, factor) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = factor != +0.0d ? Double.doubleToLongBits(factor) : 0L;
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            return result;
        }
    }

    public static class Thing implements Serializable {
        private List<Foo> foos = new ArrayList<Foo>();
        private Bar bar;

        public Bar getBar() {
            return bar;
        }

        public void setBar(Bar bar) {
            this.bar = bar;
        }

        public List<Foo> getFoos() {
            return foos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Thing)) return false;

            Thing thing = (Thing) o;

            if (bar != null ? !bar.equals(thing.bar) : thing.bar != null) return false;
            if (foos != null ? !foos.equals(thing.foos) : thing.foos != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = foos != null ? foos.hashCode() : 0;
            result = 31 * result + (bar != null ? bar.hashCode() : 0);
            return result;
        }
    }


    public static class Foo implements Serializable {
        protected String name;
        protected int number;

        public Foo() {
        }

        public Foo(String name, int number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Foo)) return false;

            Foo foo = (Foo) o;

            if (number != foo.number) return false;
            if (name != null ? !name.equals(foo.name) : foo.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + number;
            return result;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "name='" + name + '\'' +
                    ", number=" + number +
                    '}';
        }

    }

}
