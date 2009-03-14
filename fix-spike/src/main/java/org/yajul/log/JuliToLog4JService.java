package org.yajul.log;

import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.List;
import java.util.ArrayList;

/**
 * JMX MBean that plugs in the JULI->Log4j handler on start and unplugs it on stop.
 * Here is an example of a JBoss JMX MBean declaration:
 * <pre>
 *   &lt;!-- Redirect java.util.logging to Log4J. -->
 *   &lt;mbean code="org.yajul.log.JuliToLog4JService"
 *           name="org.yajul:service=JuliToLog4J">
 *       &lt;attribute name="HandlerLevel">DEBUG&lt;/attribute>
 *       &lt;depends>jboss.system:type=Log4jService,service=Logging&lt;/depends>
 *   &lt;/mbean>
 * </pre>
 * Note that because of a small glitch in the JMX specification, the attribute name
 * is 'HandlerLevel', and not 'handlerLevel' as you might expect.
 * See http://madplanet.com/jboss-docu-wiki/Wiki.jsp?page=40.JMX.MBean
 * <br>
 * User: josh
 * Date: Jun 4, 2008
 * Time: 3:41:44 PM
 */
public class JuliToLog4JService implements JuliToLog4JServiceMBean {
    private Handler activeHandler;
    private List<Handler> oldHandlers = new ArrayList<Handler>();
    private Level handlerLevel = Level.ALL;
    private Level rootLevel = Level.INFO;

    public void start() throws Exception {
        try {
            JuliToLog4jHandler.getTargetLogger(JuliToLog4JService.class).info(
                    "start(): Redirecting java.util.logging to Log4J...");
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            // remove old handlers
            for (Handler handler : rootLogger.getHandlers()) {
                oldHandlers.add(handler);
                rootLogger.removeHandler(handler);
            }
            // add our own
            activeHandler = new JuliToLog4jHandler();
            activeHandler.setLevel(handlerLevel);
            rootLogger.addHandler(activeHandler);
            rootLogger.setLevel(rootLevel);
            // done, let's check it right away!!!

            Logger.getLogger(JuliToLog4JService.class.getName()).info("started: sending JDK log messages to Log4J");
        } catch (Exception exc) {
            JuliToLog4jHandler.getTargetLogger(JuliToLog4JService.class).error("start() failed", exc);
        }
    }

    public void stop() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(activeHandler);
        // Put all the old handlers back.
        for (Handler oldHandler : oldHandlers) {
            rootLogger.addHandler(oldHandler);
        }
        Logger.getLogger(JuliToLog4jHandler.class.getName()).info("stopped");
    }

    public String getHandlerLevel() {
        return handlerLevel.getName();
    }

    public void setHandlerLevel(String level) {
        final String parseThis = level.toUpperCase();
        if ("DEBUG".equalsIgnoreCase(parseThis))
            handlerLevel = Level.FINE;
        else
            handlerLevel = Level.parse(parseThis);
    }
}
