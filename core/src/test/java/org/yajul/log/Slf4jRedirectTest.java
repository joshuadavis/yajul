package org.yajul.log;

import junit.framework.TestCase;
import org.slf4j.Logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Test JULI->SLF4J redirect.
 * <br>
 * User: josh
 * Date: Jun 10, 2010
 * Time: 9:28:11 AM
 */
public class Slf4jRedirectTest extends TestCase {

    public void testLogLevels() {
        assertEquals(LogLevel.INFO,LogLevel.toLogLevel(Level.INFO));
        assertEquals(LogLevel.ERROR,LogLevel.toLogLevel(Level.SEVERE));
        assertEquals(LogLevel.DEBUG,LogLevel.toLogLevel(Level.FINE));
        assertEquals(LogLevel.WARN,LogLevel.toLogLevel(Level.WARNING));
        assertEquals(LogLevel.TRACE,LogLevel.toLogLevel(Level.FINER));
        assertEquals(LogLevel.TRACE,LogLevel.toLogLevel(Level.FINEST));
        assertEquals(LogLevel.OFF,LogLevel.toLogLevel(Level.OFF));
    }

    public void testLogHandler() {
        String category = "test-category";          
        JuliToSlf4JHandler.redirect();
        java.util.logging.Logger.getLogger(category).info("test");
    }
}
