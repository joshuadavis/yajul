package org.yajul.embedded;

import org.jboss.embedded.Bootstrap;
import org.jboss.virtual.VirtualFile;
import org.jboss.deployers.spi.DeploymentException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.ArrayList;

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

    private List<VirtualFile> deployments = new ArrayList<VirtualFile>();

    private synchronized boolean isStarted() {
        return Bootstrap.getInstance().isStarted();
    }

    public static void startup() throws Exception {
        if (!INSTANCE.isStarted()) {
            Bootstrap.getInstance().setIgnoreShutdownErrors(true);
            log.info("##### Bootstrapping #####");
            Bootstrap.getInstance().bootstrap();
            assert Bootstrap.getInstance().isStarted();
            log.info("##### Embedded JBoss STARTED #####");
        }
        else {
            log.debug("Embedded JBoss already started.");
        }
    }

    public static void shutdown() {
        INSTANCE.doShutdown();
    }

    private void doShutdown() {
        // Undeploy
        for (VirtualFile deployment : deployments) {
            try {
                Bootstrap.getInstance().undeploy(deployment);
            } catch (DeploymentException e) {
                log.warn("Unable to undeploy due to: " + e, e);
            }
        }
        deployments.clear();
    }

    public static void deploy(VirtualFile deployment) throws DeploymentException {
        INSTANCE.doDeploy(deployment);
    }

    private void doDeploy(VirtualFile deployment) throws DeploymentException {
        Bootstrap.getInstance().deploy(deployment);
        deployments.add(deployment);
    }


}
