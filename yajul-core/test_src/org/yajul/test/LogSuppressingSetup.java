package org.yajul.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Setup that sets the logging level to 'ERROR'.
 * User: jdavis
 * Date: Nov 18, 2003
 * Time: 9:48:26 PM
 * @author jdavis
 */
public class LogSuppressingSetup extends TestSetup
{
    private static int DEFAULT_LEVEL = Level.ERROR_INT;
    private int level;

    /**
     * Create a log supressing setup that uses the default log level (ERROR).
     * @param test The test to perform set up for.
     */
    public LogSuppressingSetup(Test test)
    {
        this(test, DEFAULT_LEVEL);
    }

    /**
     * Create a log supressing setup that uses the specified log level.
     * @param test The test to perform set up for.
     * @param level The log level to set before running any tests.
     */
    public LogSuppressingSetup(Test test, int level)
    {
        super(test);
        this.level = level;
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        // Set the logging level to 'error' for testing purposes.
        Logger.getRootLogger().setLevel(Level.toLevel(level));
    }

}
