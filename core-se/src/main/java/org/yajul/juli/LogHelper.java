package org.yajul.juli;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Helper methods for JULI.
 * <br>
 * User: josh
 * Date: 12/28/12
 * Time: 3:12 PM
 */
public class LogHelper {
    /**
     * Logs an unexpected exception as at 'SEVERE' level.
     * @param logger JULI logger
     * @param t the exception.
     */
    public static void unexpected(Logger logger, Throwable t) {
        logger.log(Level.SEVERE, "Unexpected: " +  t, t);
    }

    /**
     * Removes all handlers from the given logger and returns them.
     * @param logger the logger
     * @return all of the handlers from the given logger
     */
    public static Handler[] grabAllHandlers(Logger logger) {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
        return handlers;
    }

    /**
     * Adds all of the handlers to the logger.  Can be used to undo what is done
     * with grabAllHandlers().
     * @param logger the logger
     * @param handlers the handlers
     */
    public static void addAllHandlers(Logger logger,Handler[] handlers) {
        for (Handler handler : handlers) {
            logger.addHandler(handler);
        }
    }

    /**
     * Configures JULI from the specified resource.
     * @param resourceName the resource name
     * @throws IOException if something goes wrong
     */
    public static void configureFromResource(String resourceName) throws IOException {
        // Note: We don't use ResourceUtil here because that would cause a circular dependency.
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream stream = cl.getResourceAsStream(resourceName);
        LogManager.getLogManager().readConfiguration(stream);
    }
}
