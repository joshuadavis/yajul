/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.log;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

/**
 * Provides an isolation layer between the Log4J code and
 * the org.yajul packages.
 * Extends 'Writer' so that it can be used as a 'DataSource' logger.
 */
public class Logger extends Writer
{
    /** The Log4J layout for the default logging configuration. */
    public static final String LAYOUT = "%-6r [%8t] %-5p %15.15c{1} - %m\n";

    /** Logging level for 'debug' messages. */
    public static final int LEVEL_DEBUG = 4;

    /** Logging level for 'info' messages. */
    public static final int LEVEL_INFO = 3;

    /** Logging level for 'warning' messages. */
    public static final int LEVEL_WARNING = 2;

    /** Logging level for 'error' messages. */
    public static final int LEVEL_ERROR = 1;

    /** Logging level for 'fatal' messages. */
    public static final int LEVEL_FATAL = 0;

    /** The delegate log object. */
    private Category log;

    /** A print writer that is piped to this log. */
    private PrintWriter printWriter;

    /** A string buffer for the Writer implementation. **/
    private StringBuffer lineBuffer;

    /** Flag that indicates whether Log4J has been initialized or not. */
    private static boolean configured = false;

    /** A map of existing logger instances. **/
    private static Map loggerMap = new WeakHashMap();

    /** An array of Log4J priorities to make decoding the LEVEL_xxx values easier. */
    private static Priority[] priorities =
        { Priority.FATAL, Priority.ERROR, Priority.WARN, Priority.INFO, Priority.DEBUG };


    /**
     * Creates a new logger with a delegate that logs to the Log4J category
     * 'categoryName'
     * @param catagoryName      The name of the category.
     */
    private Logger(String categoryName)
    {
        log = (categoryName == null || categoryName.length() == 0) ? Category.getRoot() : Category.getInstance(categoryName);
        printWriter = new PrintWriter(this);
        lineBuffer = new StringBuffer();
    }

    // --- java.io.Writer implementation ---

    /**
     * java.io.Writer implementation - Write the characters to the log.
     * @param buf       The array of characters to write.
     * @param off       The offset in array where the writable characters start.
     * @param len       The number of characters to write.
     */
    public void write(char[] buf,int off,int len)
    {
        // TODO: Find newlines and only write when there is a newline.
        int start = off;
        for(int i = off; i < len ; i++)
        {
            if (buf[i] == '\n')
                flush();
            else
                lineBuffer.append(buf[i]);
        }
    }

    /**
     * java.io.Writer implementation - Flush any buffered output.
     */
    public void flush()
    {
        if (lineBuffer.length() <= 0)
            return;
        log.info(lineBuffer.toString());
        lineBuffer.setLength(0);
    }

    /**
     * java.io.Writer implementation - Close the writer.
     */
    public void close()
    {
    }

    // -- End of Writer implementation --

    // --- JDK1.4 compatible methods ---

    /**
     * Returns the 'root' logger, to which *all* messages are sent.
     * @return Logger       The root logger.
     */
    public static Logger getRootLogger()
    {
        return internalGetLogger(null); // Null indicates the root.
    }

    /**
     * Returns an instance of a logger, given the category name.
     * @param categoryName      The name of the category.
     * @return Logger           The logger for the specified category.
     */
    public static Logger getLogger(String categoryName)
    {
        if (categoryName == null || categoryName.length() == 0)
            throw new IllegalArgumentException(
                "A logging category name was not specified.  Specify a name, or call Logger.getRootLogger()");
        return internalGetLogger(categoryName);
    }

    /**
     * Returns an instance of a logger, given the category class.  The name of the category
     * will be the name of the class
     * @param categoryClass     The class of the category.
     * @return Logger           The logger for the specified category.
     */
    public static Logger getLogger(Class categoryClass)
    {
        return getLogger((categoryClass != null) ? categoryClass.getName() : null);
    }

    /** Returns true if DEBUG level is enabled for this logger.
     * @return boolean      True if 'debug' is enabled.*/
    public boolean isDebugEnabled()
    {
        Priority p = Priority.DEBUG;
        if(! log.isEnabledFor(p) )
            return false;
        return p.isGreaterOrEqual(log.getChainedPriority());
    }

    /**
     * Set the logging level for the logger,  messages sent to this logger that
     * are *less than* this category will not be shown.
     * @param level - The level (LEVEL_xxx) for this log.
     */
    public void setLevel(int level)
    {
        if (level < 0 || level >= priorities.length)
            throw new IllegalArgumentException("The supplied logging level was not valid: " + level);
        log.setPriority(priorities[level]);
    }

    /**
     * Returns this logger as if it was a print writer.
     */
    public PrintWriter getPrintWriter()
    {
        return printWriter;
    }

    /** Log the message with a level of DEBUG. */
    public void debug(Object message)
    {
        log.log(Priority.DEBUG, message);
    }

    /** Log the message and the exception with a level of DEBUG. */
    public void debug(Object message, Throwable t)
    {
        log.log(Priority.DEBUG, message, t);
    }

    /** Returns true if INFO level is enabled for this logger. */
    public boolean isInfoEnabled()
    {
        Priority p = Priority.INFO;
        if( log.isEnabledFor(p) == false )
            return false;
        return p.isGreaterOrEqual(log.getChainedPriority());
    }

    /** Log the message with a level of INFO. */
    public void info(Object message)
    {
        log.log(Priority.INFO, message);
    }

    /** Log the message and the exception with a level of INFO. */
    public void info(Object message, Throwable t)
    {
        log.log(Priority.INFO, message, t);
    }

    /** Log the message with a level of WARN. */
    public void warn(Object message)
    {
        log.log(Priority.WARN, message);
    }

    /** Log the message and the exception with a level of WARN. */
    public void warn(Object message, Throwable t)
    {
        log.log(Priority.WARN, message, t);
    }

    /** Log the message with a level of ERROR. */
    public void error(Object message)
    {
        log.log(Priority.ERROR, message);
    }

    /** Log the message and the exception with a level of ERROR. */
    public void error(Object message, Throwable t)
    {
        log.log(Priority.ERROR, message, t);
    }

    /** Log the message with a level of FATAL. */
    public void fatal(Object message)
    {
        log.log(Priority.FATAL, message);
    }

    /** Log the message and the exception with a level of FATAL. */
    public void fatal(Object message, Throwable t)
    {
        log.log(Priority.FATAL, message, t);
    }

    /**
     * Log a standard 'unexpected exception' message.
     * @param throwable - An exception.
     */
    public void unexpected(Throwable throwable)
    {
        error("Unexpected exception: " + throwable.getMessage(), throwable);
    }

    // --- Implementaiton methods ---

    /**
     * Internal: Returns a logger for the category name, or the root if the category name is null.
     * Also configures Log4J, if it has not already been configured.
     * @param categoryName - The category requested.
     * @return Logger - The requested logger.
     */
    private static Logger internalGetLogger(String categoryName)
    {
        if (!configured)    // If Log4J has not been configured, set up a default configuration.
        {
            synchronized (Logger.class)  // Synchronize thread on the class and check the configuraiton flag again...
            {
                if (!configured)
                {
                    PatternLayout layout = new PatternLayout(LAYOUT);
                    ConsoleAppender console = new ConsoleAppender(layout);
                    BasicConfigurator.configure(console);
                    configured = true;
//                    Category.getInstance(Logger.class).debug("Default Logger configuration.");
//                  UNUSED:  loglogger.setPriority(Priority.INFO);
                } // if
            } // synchronized
        } // if

        // Before making a new logger, look for an existing one.
        synchronized (loggerMap)
        {
            Logger logger = (Logger)loggerMap.get(categoryName);
            if (logger == null)
            {
                logger = new Logger(categoryName);
                loggerMap.put(categoryName,logger);
            }
            return logger;
        }
    }
}