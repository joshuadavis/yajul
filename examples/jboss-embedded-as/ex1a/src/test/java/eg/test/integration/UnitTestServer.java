package eg.test.integration;

import org.jboss.bootstrap.api.lifecycle.LifecycleState;
import org.jboss.embedded.api.server.JBossASEmbeddedServer;
import org.jboss.embedded.api.server.JBossASEmbeddedServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton that starts JBoss Embedded AS.
 * <br>
 * User: Josh
 * Date: Jan 21, 2010
 * Time: 6:46:01 AM
 */
public class UnitTestServer
{
    private static final Logger log = LoggerFactory.getLogger(UnitTestServer.class);

    private JBossASEmbeddedServer server;

    private static final UnitTestServer INSTANCE = new UnitTestServer();

    private void doStart()
    {
        if (server == null)
        {
            String jbossHome = System.getProperty("jboss.home");
            server = JBossASEmbeddedServerFactory.createServer();
            server.getConfiguration().jbossHome(jbossHome);
            try
            {
                log.info("*** STARTING (jbossHome=" + jbossHome + ") ***");
                server.start();
            }
            catch (Exception e)
            {
                log.error("Start threw an exception: " + e,e);
            }
        }
    }

    private void doStop()
    {
        if (server != null  && server.getState().equals(LifecycleState.STARTED))
        {
            try
            {
                log.info("*** SHUTTING DOWN... ***");
                server.shutdown();
            }
            catch (Exception e)
            {
                log.error("Shutdown threw an exception: " + e,e);
            }
        }
    }
    public static void start()
    {
        INSTANCE.doStart();
    }

    public static void stop()
    {
        INSTANCE.doStop();
    }
}
