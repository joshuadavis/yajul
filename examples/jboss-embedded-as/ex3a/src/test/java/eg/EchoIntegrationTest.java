package eg;

import org.jboss.embedded.api.server.JBossASEmbeddedServer;
import org.jboss.embedded.api.server.JBossASEmbeddedServerFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;

public class EchoIntegrationTest
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final Logger log = LoggerFactory.getLogger(EchoIntegrationTest.class);

   private static JBossASEmbeddedServer server;

   private static Context jndiContext;

   private static JavaArchive createDeployment()
   {
      return ShrinkWrap.create("slsb.jar", JavaArchive.class).addClasses(EchoBean.class, Echo.class);
   }

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private JavaArchive deployment;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @BeforeClass
   public static void createAndStartServer() throws Exception
   {
      // Will pick up configuration of $JBOSS_HOME from sysprops
      server = JBossASEmbeddedServerFactory.createServer();

      // Start
      server.start();

      // Set JNDI Context
      jndiContext = new InitialContext();
   }

   @Before
   public void deploy() throws Exception
   {
      // Create the deployment
      deployment = createDeployment();

      // Log out the contents of the test JAR
      log.info(deployment.toString(true));

      // Deploy
      server.deploy(deployment);
   }


   @After
   public void undeploy() throws Exception
   {
      // If we're up and running, undeploy
      if (server != null && server.getState().equals(LifecycleState.STARTED))
      {
         server.undeploy(deployment);
      }
   }
   @AfterClass
   public static void stopServer() throws Exception
   {
      if (server != null && server.getState().equals(LifecycleState.STARTED))
      {
         server.shutdown();
      }
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   public void testSlsb() throws Exception
   {
      // Get a proxy
      final EchoLocalBusiness slsb = (EchoLocalBusiness) jndiContext.lookup(EchoLocalBusiness.JNDI_NAME);

      // Invoke
      final String expected = "Testing Embedded APIs for JBoss Application Server";
      final String received = slsb.echo(expected);

      // Ensure equal
      Assert.assertEquals("Expected result was not as received", expected, received);
      Assert.assertTrue("Expected result was not the same reference as received", expected == received);
   }
}
