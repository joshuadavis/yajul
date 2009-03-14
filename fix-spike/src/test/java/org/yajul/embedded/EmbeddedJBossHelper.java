package org.yajul.embedded;

import org.yajul.log.LogSuppressor;
import org.jboss.embedded.Bootstrap;
import org.jboss.deployers.spi.DeploymentException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Embedded JBoss helper methods
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 3:29:48 PM
 */
public class EmbeddedJBossHelper {
    private final static Logger log = LoggerFactory.getLogger(EmbeddedJBossHelper.class);

    private static EmbeddedJBossHelper INSTANCE = new EmbeddedJBossHelper();

    private boolean started = false;

    private synchronized boolean isStarted() {
        return started;
    }

    private synchronized void setStarted(boolean started) {
        this.started = started;
    }

    public static void startup() throws Exception {
        if (!INSTANCE.isStarted())
        {
            Bootstrap.getInstance().setIgnoreShutdownErrors(true);
            log.info("##### Bootstrapping #####");
            Bootstrap.getInstance().bootstrap();
            INSTANCE.setStarted(true);
            log.info("##### Embedded JBoss BOOTED #####");
        }
    }
    
    public static void shutdown() {
        if (INSTANCE.isStarted()) {
            LogSuppressor suppressor = new LogSuppressor(
                    "org.jboss.aop.deployers.AspectDeployer",
                    "org.jboss.remoting.transport.Connector",
                    "org.jboss.jms.server.messagecounter.MessageCounterManager"
            );
            log.info("##### Shutting Down #####");
            Bootstrap.getInstance().shutdown();
            log.info("##### Embedded JBoss STOPPED #####");
            suppressor.restore();
            INSTANCE.setStarted(false);
        }
    }
}
