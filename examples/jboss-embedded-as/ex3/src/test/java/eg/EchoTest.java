package eg;

import javax.naming.Context;
import javax.naming.InitialContext;

public class EchoTest
{
/*
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   */
/**
    * Logger
    *//*

   private static final Logger log = Logger.getLogger(EchoBeanIntegrationTest.class);

   */
/**
    * AS instance
    *//*

   private static JBossASEmbeddedServer server;

   */
/**
    * JNDI Context
    *//*

   private static Context jndiContext;

   */
/**
    * Creates the deployment; much like Arquillian will eventually use
    * @return
    *//*

   private static JavaArchive createDeployment()
   {
      return ShrinkWrap.create("slsb.jar", JavaArchive.class).addClasses(EchoBean.class, EchoLocalBusiness.class);
   }

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   */
/**
    * Our test JAR
    *//*

   private JavaArchive deployment;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   */
/**
    * Creates and starts the server, setting the JNDI Context
    *//*

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

   */
/**
    * Creates and deploys the EJB JAR
    *//*

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

   */
/**
    * Undeploys the EJB
    * @throws Exception
    *//*

   @After
   public void undeploy() throws Exception
   {
      // If we're up and running, undeploy
      if (server != null && server.getState().equals(LifecycleState.STARTED))
      {
         server.undeploy(deployment);
      }
   }

   */
/**
    * Brings down the server
    * @throws Exception
    *//*

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

   */
/**
    * Ensures the EJB is working as expected
    *//*

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

*/
}
