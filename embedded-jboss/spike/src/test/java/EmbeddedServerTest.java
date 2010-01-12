import org.jboss.embedded.api.server.JBossASEmbeddedServer;
import org.jboss.embedded.api.server.JBossASEmbeddedServerFactory;
import org.junit.Test;

/**
 * Start/stop the Embedded JBoss AS server.
 * <br>
 * User: Josh
 * Date: Dec 29, 2009
 * Time: 6:55:13 AM
 */
public class EmbeddedServerTest
{
    @Test
    public void startAndStopServer() throws Exception
    {
        JBossASEmbeddedServer server = JBossASEmbeddedServerFactory.createServer();
        final String jbossHome = "./jboss-as/build/output/jboss-6.0.0.M1";
        System.out.println("jbossHome=" + jbossHome);
        server.getConfiguration().jbossHome(jbossHome);
        System.out.println("Starting...");
        server.start();
        System.out.println("Stopping...");
        server.stop();
    }
}
