package org.yajul.jms;

import javax.jms.*;
import java.util.Enumeration;

/**
 * Utility methods for JMS.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 10:41:52 AM
 */
public class JmsHelper {
    /**
     * Clean up JMS producer objects.  Typically used in a finally block.
     *
     * @param sender  the sender
     * @param session the session (may be null)
     * @param conn    the connection (may be null)
     */
    public static void close(MessageProducer sender, Session session, Connection conn) {
        if (sender != null) {
            //noinspection EmptyCatchBlock
            try {
                sender.close();
            } catch (Exception ignore) {
            }
        }
        if (session != null) {
            //noinspection EmptyCatchBlock
            try {
                session.close();
            } catch (Exception ignore) {
            }
        }
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Clean up JMS consumer objects.    Typically used in a finally block.
     *
     * @param consumer the consumer
     * @param session  the session (may be null)
     * @param conn     the connection (may be null)
     */
    public static void close(MessageConsumer consumer, Session session, Connection conn) {
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.stop();
            } catch (Exception ignore) {
            }
        }

        if (consumer != null) {
            //noinspection EmptyCatchBlock
            try {
                consumer.close();
            } catch (Exception ignore) {
            }
        }
        if (session != null) {
            //noinspection EmptyCatchBlock
            try {
                session.close();
            } catch (Exception ignore) {
            }
        }
        if (conn != null) {
            //noinspection EmptyCatchBlock
            try {
                conn.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Returns the object in the JMS message if it's an object message.
     *
     * @param message the JMS message
     * @return the object in the JMS message if it's an object message, null otherwise.
     */
    public static Object getObject(Message message) {
        if (message == null)
            return null;
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                return objectMessage.getObject();
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else
            return null;
    }


    public static Long getNullableLongProperty(Message m, String property) throws JMSException {
        return (Long) (m == null ? null : m.getObjectProperty(property));
    }

    public static String getStringProperty(Message m, String property) throws JMSException {
        return m == null ? null : m.getStringProperty(property);
    }

    public static Message peek(Session session, Destination destination) throws JMSException {
        QueueSession ses = (QueueSession) session;
        Queue queue = (Queue) destination;
        QueueBrowser browser = ses.createBrowser(queue);
        Enumeration enumeration = browser.getEnumeration();
        return enumeration.hasMoreElements() ? (Message) enumeration.nextElement() : null;
    }

    public static boolean messagePropertyNullOrEqualTo(Message message, String propertyName, Long aLong) {
        try {
            boolean exists = message.propertyExists(propertyName);
            if (exists) {
                long val = message.getLongProperty(propertyName);
                return val == aLong;
            } else
                return true;
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}
