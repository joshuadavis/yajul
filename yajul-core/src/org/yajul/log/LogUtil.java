/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/

package org.yajul.log;

import org.apache.log4j.*;

import java.io.IOException;

/**
 * Provides static utility methods for the Log4J library, including:
 * <ul>
 * <li>Auto configuration: If Log4J has not been configured, using
 * LogUtil.getLogger() will configure it automatically.</li>
 * <li>Simplified logging level setup using strings.</li>
 * <li>Simplified 'rolling log file' setup.</li>
 * </ul>
 *
 * @author josh
 */
public class LogUtil
{
    /**
     * The Log4J layout for the default logging configuration.
     *
     * @see #configure()
     */
    public static final String DEFAULT_LAYOUT =
            "%-6r [%8t] %-5p %15.15c{1} - %m\n";

    /**
     * The name of the system property used to establish the logging level
     * for the default configuration. "org.yajul.log.LogUtil.LEVEL"
     *
     * @see #configure()
     */
    public static final String SYSPROPERTY_LEVEL =
            "org.yajul.log.LogUtil.LEVEL";

    /**
     * The default logging level (DEBUG). *
     */
    public static final Level DEFAULT_LEVEL = Level.DEBUG;

    /**
     * Flag that indicates whether Log4J has been initialized or not.
     */
    private static boolean configured = false;

    /**
     * A logger for this class
     * NOTE: To avoid infinite recursion, LogUtil is not used to do this.
     */
    private static final Logger LOG = Logger.getLogger(LogUtil.class);

    /**
     * Returns true if Log4J has been configured using this class.
     *
     * @return boolean - True if one of the configuration methods was used.
     */
    public static boolean isConfigured()
    {
        synchronized (LogUtil.class)
        {
            return configured;
        }
    }

    /**
     * Decodes a level name into a Logger.LEVEL_xxx value.  The level name is
     * <i>case insensitive</i>
     *
     * @param levelName One of: "FATAL", "ERROR", "WARNING", "INFO", "DEBUG"
     * @return Level - The logging level.
     */
    public static Level parseLevel(String levelName)
    {
        return parseLevel(levelName, null);
    }

    /**
     * Decodes a level name into a Logger.LEVEL_xxx value.  The level name is
     * <i>case insensitive</i>
     *
     * @param levelName    One of: "FATAL", "ERROR", "WARNING", "INFO", "DEBUG"
     * @param defaultLevel The default level.  If null, the method will
     *                     throw an Illegal argument exception if 'levelName' is not valid.
     * @return Level - The logging level.
     */
    public static Level parseLevel(String levelName, Level defaultLevel)
    {

        if ("FATAL".equalsIgnoreCase(levelName))
            return Level.FATAL;
        else if ("ERROR".equalsIgnoreCase(levelName))
            return Level.ERROR;
        else if ("WARNING".equalsIgnoreCase(levelName))
            return Level.WARN;
        else if ("WARN".equalsIgnoreCase(levelName))
            return Level.WARN;
        else if ("INFO".equalsIgnoreCase(levelName))
            return Level.INFO;
        else if ("DEBUG".equalsIgnoreCase(levelName))
            return Level.DEBUG;
        else if (defaultLevel != null)
            return defaultLevel;
        else
            throw new IllegalArgumentException(
                    "Unexpected logging level name: '" + levelName + "'");
    }

    /**
     * Configure the log4J system to log to the console using the default
     * layout, and the default system property to set the root logger level.
     *
     * @see #DEFAULT_LAYOUT
     * @see #SYSPROPERTY_LEVEL
     */
    public static final void configure()
    {
        configure(DEFAULT_LAYOUT, SYSPROPERTY_LEVEL);
    }

    /**
     * Configure Log4J using the specified layout and system property for the
     * root level.
     *
     * @param defaultLayout    The layout string
     * @param syspropertyLevel The system property name to use when setting
     *                         the level of the root logger.
     */
    public static void configure(String defaultLayout, String syspropertyLevel)
    {
        synchronized (LogUtil.class)
        {
            if (!configured)
            {
                // Clear the current configuration.
                LogManager.resetConfiguration();
                PatternLayout layout = new PatternLayout(defaultLayout);
                Appender appender = new ConsoleAppender(layout);
                BasicConfigurator.configure(appender);
                String levelString = System.getProperty(syspropertyLevel);
                setRootLevel(levelString);
// 2003-07-10 [jsd] Eliminated some minor log noise here.
//                LOG.info("Using default Log4J configuration.");
                configured = true;
            }
        }
    }

    /**
     * Configures Log4J to use a simple rolling log file.
     *
     * @param fileName The name of the rolling log file.
     * @param append   True, if the file is to be appended to, false to truncate.
     * @throws IOException If there was a problem setting the file log.
     */
    public static final void configureRollingLogFile(
            String fileName, boolean append) throws IOException
    {
        synchronized (LogUtil.class)
        {
            PatternLayout layout = new PatternLayout(DEFAULT_LAYOUT);
            Appender appender = new RollingFileAppender(
                    layout, fileName, append);

            // Create an async appender, and attach the rolling file to it.
            AsyncAppender async = new AsyncAppender();
            async.addAppender(appender);

            // Use the async appender.
            LogManager.resetConfiguration(); // Clear the current configuration.
            BasicConfigurator.configure(async);
            String levelString = System.getProperty(SYSPROPERTY_LEVEL);
            setRootLevel(levelString);
            configured = true;
            LOG.info("Log4J rolling log file configuration.");
        }
    }

    /**
     * Sets the root logger level from a string.
     * <ul>
     * <li>ERROR - Error level.</li>
     * <li>WARNING - Warning level.</li>
     * <li>INFO - Info level.</li>
     * <li>DEBUG - Debug level.</li>
     * <li>null - Default level (debug).</li>
     * </ul>
     *
     * @param levelString - Null will set the default level.
     */
    public static final void setRootLevel(String levelString)
    {
        Level level = parseLevel(levelString, DEFAULT_LEVEL);
        Logger.getRootLogger().setLevel(level);
    }

    /**
     * Returns the 'root' logger, to which *all* messages are sent.
     *
     * @return Logger       The root logger.
     */
    public static Logger getRootLogger()
    {
        return internalGetLogger(null); // Null indicates the root.
    }

    /**
     * Returns an instance of a logger, given the category name.
     *
     * @param categoryName The name of the category.
     * @return Logger           The logger for the specified category.
     */
    public static Logger getLogger(String categoryName)
    {
        return internalGetLogger(categoryName);
    }

    /**
     * Returns an instance of a logger, given the category class.  The name of
     * the category
     * will be the name of the class
     *
     * @param categoryClass The class of the category.
     * @return Logger           The logger for the specified category.
     */
    public static Logger getLogger(Class categoryClass)
    {
        return
                internalGetLogger((categoryClass != null) ?
                        categoryClass.getName() : null);
    }

    /**
     * Logs an unexpected exception message with error priority.
     *
     * @param logger The logger to use.
     * @param t      The unexpected exception.
     */
    public static void unexpected(Logger logger, Throwable t)
    {
        String message = "Unexpected exception: " + t.getMessage();
        logger.error(message, t);
    }

    /**
     * Internal: Returns a logger for the category name, or the root if the category name is null.
     * Also configures Log4J, if it has not already been configured.
     *
     * @param loggerName - The category requested.
     * @return Logger - The requested logger.
     */
    private static Logger internalGetLogger(String loggerName)
    {
        configure();
        return Logger.getLogger(loggerName);
    }

    public static Appender createAsyncFileAppender(Layout layout, String filename, boolean append) throws IOException
    {
        FileAppender file = new FileAppender(layout, filename, append);
        AsyncAppender async = new AsyncAppender();
        async.addAppender(file);
        return async;
    }
}
