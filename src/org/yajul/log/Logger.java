/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.log;

import java.io.PrintWriter;
import java.io.Writer;

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
    
    /** The delegate log object. */
    private Category log;
    /** A print writer that is piped to this log. */
    private PrintWriter printWriter;
    
    /** Flag that indicates whether Log4J has been initialized or not. */
    private static boolean configured = false;

    /**
     * Returns an instance of a logger class.
     * @param c - The class that wants to log.
     */
    public static Logger getLogger(Class c)
    {
        return getLogger(c.getName());
    }
    
    /**
     * Returns an instance of a logger, given the category name. 
     * @param categoryName - The name of the category.
     */
    public static synchronized Logger getLogger(String categoryName)
    {
        if (!configured)
        {
            PatternLayout layout = new PatternLayout(LAYOUT);
            ConsoleAppender console = new ConsoleAppender(layout);
            BasicConfigurator.configure(console);
            configured = true;
        }
        return new Logger(categoryName);
    }
    
    /**
     * Creates a new logger with a delegate that logs to the Log4J category
     * 'categoryName'
     * @param catagoryName - The name of the category.
     */
    private Logger(String categoryName)
    {
        log = Category.getInstance(categoryName);
        printWriter = new PrintWriter(this);
    }
    
    // --- java.io.Writer implementation ---
    
    /**
     * java.io.Writer implementation - Write the characters to the log.
     * @param buf - The array of characters to write.
     * @param off - The offset in array where the writable characters start.
     * @param len - The number of characters to write.
     */
    public void write(char[] buf,int off,int len)
    {
        // TODO: Find newlines and only write when there is a newline.
        String str = new String(buf,off,len);
        log.info(str);
    }
    
    /**
     * java.io.Writer implementation - Flush any buffered output.
     */
    public void flush()
    {
    }
 
    /**
     * java.io.Writer implementation - Close the writer.
     */
    public void close()
    {
    }
    
    /**
     * Returns this logger as if it was a print writer.
     */
    public PrintWriter getPrintWriter()
    {
        return printWriter;
    }
    
    /** Returns true if TRACE level is enabled for this logger. */
    public boolean isDebugEnabled()
    {
        Priority p = Priority.DEBUG;
        if( log.isEnabledFor(p) == false )
            return false;
        return p.isGreaterOrEqual(log.getChainedPriority());
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
}