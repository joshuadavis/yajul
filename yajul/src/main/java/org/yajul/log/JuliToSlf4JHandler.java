package org.yajul.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * A JULI (java.util.logging) handler that redirects java.util.logging messages to Slf4J
 * http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 * <br>
 * User: josh
 * Date: Jun 4, 2008
 * Time: 3:31:21 PM
 */
public class JuliToSlf4JHandler extends Handler {

    private static final Logger log = LoggerFactory.getLogger(JuliToSlf4JHandler.class);

    private static boolean REDIRECTED = false;

    /**
     * Globally redirects JULI to SLF4J.  This does nothing if it has already been
     * called once.
     */
    public static void redirect() {
        synchronized (JuliToSlf4JHandler.class) {
            if (REDIRECTED)
                return; // Already done.
            LogManager manager = LogManager.getLogManager();
            java.util.logging.Logger rootLogger = manager.getLogger("");
            redirect(rootLogger);
            REDIRECTED = true;
            log.info("Java logging redirected to SLF4J"); 
        }
    }

    public static void redirect(java.util.logging.Logger juliLogger) {
        JuliToSlf4JHandler newHandler = new JuliToSlf4JHandler();
        redirect(juliLogger, newHandler);
    }

    public static void redirect(java.util.logging.Logger juliLogger, Handler newHandler) {
        Handler[] handlers = juliLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                ConsoleHandler consoleHandler = (ConsoleHandler) handler;
                juliLogger.removeHandler(consoleHandler);
            }
            if (handler instanceof JuliToSlf4JHandler) {
                return; // Nothing to do.
            }
        }
        juliLogger.addHandler(newHandler);
    }

    public void publish(LogRecord record) {
        Logger slf4j = LoggerFactory.getLogger(record.getLoggerName());
        LogLevel level = LogLevel.toLogLevel(record.getLevel());
        publishToSlf4j(record, level, slf4j);
    }

    protected void publishToSlf4j(LogRecord record, LogLevel level, Logger slf4j) {
        switch (level) {
            case ERROR:
                if (slf4j.isErrorEnabled())
                    slf4j.error(toMessage(record));
                break;
            case WARN:
                if (slf4j.isWarnEnabled())
                    slf4j.warn(toMessage(record));
                break;
            case INFO:
                if (slf4j.isInfoEnabled())
                    slf4j.info(toMessage(record));
                break;
            case DEBUG:
                if (slf4j.isDebugEnabled())
                    slf4j.debug(toMessage(record));
                break;
            case TRACE:
                if (slf4j.isTraceEnabled())
                    slf4j.trace(toMessage(record));
                break;
            case OFF:
                break; // Do nothing, OFF means no logging.
        }
    }

    private String toMessage(LogRecord record) {
        String message = record.getMessage();
        // Format message
        try {
            Object parameters[] = record.getParameters();
            if (parameters != null && parameters.length != 0) {
                // Check for the first few parameters ?
                if (message.indexOf("{0}") >= 0 ||
                        message.indexOf("{1}") >= 0 ||
                        message.indexOf("{2}") >= 0 ||
                        message.indexOf("{3}") >= 0) {
                    message = MessageFormat.format(message, parameters);
                }
            }
        }
        catch (Exception ex) {
            // ignore Exception
        }
        return message;
    }

    @Override
    public void flush() {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }
}