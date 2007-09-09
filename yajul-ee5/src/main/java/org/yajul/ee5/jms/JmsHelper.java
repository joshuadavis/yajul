package org.yajul.ee5.jms;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

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
}
