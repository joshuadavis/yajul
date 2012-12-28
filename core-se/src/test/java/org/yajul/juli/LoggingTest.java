package org.yajul.juli;

import org.junit.Assert;
import org.junit.Test;
import org.yajul.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.util.logging.*;

import static org.junit.Assert.assertTrue;

/**
 * Test JULI logging classes.
 * <br>
 * User: josh
 * Date: 12/28/12
 * Time: 4:02 PM
 */
public class LoggingTest {

    @Test
    public void testTTCCFormatter() throws Exception {

        Logger test = Logger.getLogger("test");
        Handler[] oldHandlers = LogHelper.grabAllHandlers(test);
        test.setUseParentHandlers(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamHandler handler = new StreamHandler(baos,new TTCCFormatter());
        test.addHandler(handler);
        test.log(Level.INFO, "Hello.");

        handler.flush();
        test.setUseParentHandlers(true);
        LogHelper.addAllHandlers(test,oldHandlers);

        String s = baos.toString();

        System.out.println("s=" + s);
        assertTrue(s.endsWith("Hello." + StringUtil.LF));

    }

    @Test
    public void testConfigureFromResource() throws Exception {
        LogHelper.configureFromResource("test-logging.properties");
        Logger logger = Logger.getLogger("another-test");
        logger.info("This is a test.");
        logger.info("Resetting configuration...");
        LogManager.getLogManager().readConfiguration();
        logger.info("After reset.");
    }
}
