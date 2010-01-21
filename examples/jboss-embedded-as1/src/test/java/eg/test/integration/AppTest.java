package eg.test.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Unit test for simple App.
 */
@Test
public class AppTest 
{
    private static final Logger log = LoggerFactory.getLogger(AppTest.class);

    public void testApp()
    {
        log.info("testing, testing, 1... 2... 3?");
        UnitTestServer.start();

        UnitTestServer.stop();
    }
}
