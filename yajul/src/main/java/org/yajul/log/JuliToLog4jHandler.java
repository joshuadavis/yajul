package org.yajul.log;

import org.apache.log4j.Priority;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A JULI (java.util.logging) handler that redirects java.util.logging messages to Log4J
 * http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 * <br>
 * User: josh
 * Date: Jun 4, 2008
 * Time: 3:31:21 PM
 */
public class JuliToLog4jHandler extends Handler {

    public void publish(LogRecord record) {
        org.apache.log4j.Logger log4j = getTargetLogger(record.getLoggerName());
        Priority priority = toLog4j(record.getLevel());
        log4j.log(priority, toLog4jMessage(record), record.getThrown());
    }

    static Logger getTargetLogger(String loggerName) {
        return Logger.getLogger(loggerName);
    }

    public static Logger getTargetLogger(Class clazz) {
        return getTargetLogger(clazz.getName());
    }
    
    private String toLog4jMessage(LogRecord record) {
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

    private org.apache.log4j.Level toLog4j(Level level) {
        LogLevel logLevel = LogLevel.toLogLevel(level);
        switch(logLevel)
        {
            case ERROR:
                return org.apache.log4j.Level.ERROR;
            case WARN:
                return org.apache.log4j.Level.WARN;
            case INFO:
                return org.apache.log4j.Level.INFO;
            case DEBUG:
                return org.apache.log4j.Level.DEBUG;
            case TRACE:
                return org.apache.log4j.Level.TRACE;
            case OFF:
            default:
                return org.apache.log4j.Level.OFF;
        }
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
