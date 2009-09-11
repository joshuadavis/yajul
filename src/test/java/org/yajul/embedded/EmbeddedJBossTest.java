package org.yajul.embedded;

import junit.framework.TestCase;
import org.jboss.embedded.Bootstrap;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectory;
import org.jboss.virtual.plugins.context.vfs.AssembledContextFactory;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.jndi.JndiHelper;
import org.yajul.jndi.EarJndiLookup;
import org.yajul.jndi.JndiProvider;

import javax.naming.InitialContext;
import java.util.List;

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
        try {
            EmbeddedJBossHelper.startup();
            // No need to modify the bootstrap files much, just deploy your own resources.
            AssembledDirectory resources = AssembledContextFactory.getInstance().create("res");
            resources.addResource("my-ds.xml","my-ds.xml");
            Bootstrap.getInstance().deploy(resources);

            String[] includes = { "**/embedded/Echo*.class" };
            AssembledDirectory ear = AssembledContextFactory.getInstance().create("hello.ear");
            ear.mkdir("hello-ejbs.jar").addResources(Echo.class,includes,null);
            ear.mkdir("META-INF").addResource("hello-application.xml","application.xml");
            EmbeddedJBossHelper.deploy(ear);
            InitialContext ic = new InitialContext();
            String listing = JndiHelper.listBindings(ic,"/");
            System.out.println(listing);
            JndiProvider<Echo> p = new JndiProvider<Echo>(new EarJndiLookup(ic,"hello"),Echo.class,"EchoBean/local");
            Echo echo = p.get();
            String rv  = echo.echo("Hello world!");
            assertEquals("msg=Hello world!",rv);
            EmbeddedJBossHelper.undeploy(ear);
        }
        finally {
            EmbeddedJBossHelper.shutdown();
        }

    }
}
