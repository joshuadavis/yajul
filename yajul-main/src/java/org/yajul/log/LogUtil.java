/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Provides static utility methods for the Log4J library.
 * @author josh
 */
public class LogUtil
{
    // NOTE: Don't put a YAJUL Logger in here!! (infinite recursion)

    /** The Log4J layout for the default logging configuration. */
    public static final String LAYOUT = "%-6r [%8t] %-5p %15.15c{1} - %m\n";

    /** Configuration state. **/
    private static boolean configured = false;

    /**
     * Configure the log4J system to log to the console.
     */
    public static final void configure()
    {
        synchronized (LogUtil.class)
        {
            if (!configured)
            {
                PatternLayout layout = new PatternLayout(LAYOUT);
                ConsoleAppender console = new ConsoleAppender(layout);
                BasicConfigurator.configure(console);
                configured = true;
            }
        }
    }

    /**
     * Sets the root logger level from a string.
     * <ul>
     * <li>ERROR - Error level.</li>
     * <li>WARNING - Warning level.</li>
     * <li>INFO - Info level.</li>
     * <li>DEBUG - Debug level.</li>
     * </ul>
     * @param levelString
     */
    public static final void setRootLevel(String levelString)
    {
        Level level = Level.ERROR;

        if ("ERROR".equalsIgnoreCase(levelString))
            level = Level.ERROR;
        else if ("WARNING".equalsIgnoreCase(levelString))
            level = Level.WARN;
        else if ("INFO".equalsIgnoreCase(levelString))
            level = Level.INFO;
        else if ("DEBUG".equalsIgnoreCase(levelString))
            level = Level.DEBUG;
        Logger.getRootLogger().setLevel(level);
    }
}
