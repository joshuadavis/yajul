package org.yajul.embedded;

import junit.framework.TestCase;
import org.jboss.embedded.Bootstrap;
import org.jboss.virtual.plugins.context.vfs.AssembledDirectory;
import org.jboss.virtual.plugins.context.vfs.AssembledContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.jmx.JmxUtil;
import org.yajul.jndi.EarJndiLookup;
import org.yajul.jndi.JndiHelper;
import org.yajul.jndi.JndiObjectProvider;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import java.util.List;

/**
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 3:10:22 PM
 */
public class EmbeddedJBossTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedJBossTest.class);

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
            JndiObjectProvider<Echo> p = new JndiObjectProvider<Echo>(new EarJndiLookup(ic,"hello"),Echo.class,"EchoBean/local");
            Echo echo = p.getObject();
            String rv  = echo.echo("Hello world!");
            assertEquals("msg=Hello world!",rv);
            // Test JMX
            MBeanServer server = JmxUtil.locateServerWithDomain(null,"jboss");
            log.info("*** mbeanCount=" + server.getMBeanCount());
            List<ObjectName> names = JmxUtil.sortByDomain(server.queryNames(null,null));
            int i = 0;
            for (ObjectName name : names) {
                log.info("[" + (++i) + "] domain=" + name.getDomain() + " keyPropertyList=" + name.getCanonicalKeyPropertyListString());
            }
            EmbeddedJBossHelper.undeploy(ear);
        }
        finally {
            EmbeddedJBossHelper.shutdown();
        }

    }
}
