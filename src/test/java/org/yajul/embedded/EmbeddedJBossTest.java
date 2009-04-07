package org.yajul.embedded;

import junit.framework.TestCase;
import org.jboss.embedded.Bootstrap;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectory;
import org.jboss.virtual.plugins.context.vfs.AssembledContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.jndi.JndiHelper;

import javax.naming.InitialContext;

/**
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 3:10:22 PM
 */
public class EmbeddedJBossTest extends TestCase {

    public void testEmbeddedJBossBoot() throws Exception {
        try {
            EmbeddedJBossHelper.startup();
        }
        finally {
            EmbeddedJBossHelper.shutdown();
        }
    }

    public void testDeploy() throws Exception {
//      AssembledDirectory jar = AssembledContextFactory.getInstance().create("tutorial.jar");
//      String[] includes = {"**/beans/*.class"};
//      jar.addResources(Customer.class, includes, null);
//      // Get tutorial-persistence.xml from classloader and alias it within the archive.
//      jar.mkdir("META-INF").addResource("tutorial-persistence.xml", "persistence.xml");
        try {
            EmbeddedJBossHelper.startup();
            // No need to modify the bootstrap files much, just deploy your own resources.
            AssembledDirectory resources = AssembledContextFactory.getInstance().create("res");
            resources.addResource("my-ds.xml","my-ds.xml");
            Bootstrap.getInstance().deploy(resources);

            AssembledDirectory ejbjar = AssembledContextFactory.getInstance().create("ejbjar");
            String[] includes = { "**/embedded/Echo*.class" };
            ejbjar.addResources(Echo.class,includes, null);
            Bootstrap.getInstance().deploy(ejbjar);
            InitialContext ic = new InitialContext();
            String listing = JndiHelper.listBindings(ic,"/");
            System.out.println(listing);

        }
        finally {
            EmbeddedJBossHelper.shutdown();
        }

    }
}
