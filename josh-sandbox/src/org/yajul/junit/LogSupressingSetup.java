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
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.yajul.log.Logger;

public class LogSupressingSetup extends TestSetup
{
    public LogSupressingSetup(Test test)
    {
        super(test);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        // Logger.getLogger(LogSupressingSetup.class).debug("Setting logging level to 'ERROR'...");
        Logger.getRootLogger().setLevel(Logger.LEVEL_ERROR);
    }
}
