/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jbossas.examples;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.bootstrap.api.lifecycle.LifecycleState;
import org.jboss.embedded.api.server.JBossASEmbeddedServer;
import org.jboss.embedded.api.server.JBossASEmbeddedServerFactory;
import org.jboss.jbossas.embedded.examples.slsb.EchoBean;
import org.jboss.jbossas.embedded.examples.slsb.EchoLocalBusiness;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test to ensure that the {@link EchoBean}
 * is working as contracted.  Shows usage of the Embedded
 * APIs for JBoss Application Server.  Look to
 * the POM configuration for further setup
 * which may be required for proper test execution; to 
 * run inside the IDE, configure the JVM startup parameters
 * to match those in the Surefire runtime configuration.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class EchoBeanIntegrationTest
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(EchoBeanIntegrationTest.class);

   /**
    * AS instance
    */
   private static JBossASEmbeddedServer server;

   /**
    * JNDI Context
    */
   private static Context jndiContext;

   /**
    * Creates the deployment; much like Arquillian will eventually use
    * @return
    */
   private static JavaArchive createDeployment()
   {
      return ShrinkWrap.create("slsb.jar", JavaArchive.class).addClasses(EchoBean.class, EchoLocalBusiness.class);
   }

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Our test JAR
    */
   private JavaArchive deployment;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates and starts the server, setting the JNDI Context
    */
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

   /**
    * Creates and deploys the EJB JAR
    */
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

   /**
    * Undeploys the EJB
    * @throws Exception
    */
   @After
   public void undeploy() throws Exception
   {
      // If we're up and running, undeploy
      if (server != null && server.getState().equals(LifecycleState.STARTED))
      {
         server.undeploy(deployment);
      }
   }

   /**
    * Brings down the server
    * @throws Exception
    */
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

   /**
    * Ensures the EJB is working as expected
    */
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
