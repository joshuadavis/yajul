/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 11, 2002
 * Time: 12:55:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.junit;

import junit.extensions.TestSetup;
import junit.framework.Test;
import org.yajul.log.Logger;

/**
 * A test setup class that supresses log messages.  Use the system property
 * junit.logger.level to set the logging level before starting JUnit.
 * <br>
 * Example:<br>
 * java junit.textui.TestRunner -Djunit.logger.level=DEBUG com.yoydyne.MyTest
 * @author Joshua Davis
 */
public class LogSupressingSetup extends TestSetup
{
    /**
     * The system property that can be used to change the logging level
     * for JUnit tests that use this TestSetup.<br>
     * <code>junit.logger.level</code>
     */
    public static final String SYSTEM_PROPERTY = "junit.logger.level";

    public LogSupressingSetup(Test test)
    {
        super(test);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        int level = Logger.LEVEL_ERROR;
        // Check the system property 'junit.logger.level'
        String levelString = System.getProperty(SYSTEM_PROPERTY);
        if ("ERROR".equalsIgnoreCase(levelString))
            level = Logger.LEVEL_ERROR;
        else if ("WARNING".equalsIgnoreCase(levelString))
            level = Logger.LEVEL_WARNING;
        else if ("INFO".equalsIgnoreCase(levelString))
            level = Logger.LEVEL_INFO;
        else if ("DEBUG".equalsIgnoreCase(levelString))
            level = Logger.LEVEL_DEBUG;
        Logger.getRootLogger().setLevel(level);
    }
}
