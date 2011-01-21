package org.yajul.weldse.example1;

import org.jboss.weld.environment.se.ShutdownManager;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * A simple Weld SE app.
 * <br>
 * User: Josh
 * Date: 1/20/11
 * Time: 11:19 PM
 */
@Singleton
public class SimpleApp
{
    private static final Logger log = LoggerFactory.getLogger(SimpleApp.class);

    public SimpleApp()
    {
        log.info("Constructor.");
    }

    private void sayHello()
    {
        log.info("Hello world!");
    }

    public void onStartup(@Observes ContainerInitialized event,@Parameters List<String> commandLine)
    {
        log.info("onStartup()");
    }

    public static void main(String[] args)
    {
        try
        {
            WeldContainer container = new StartMain(args).go();
            final SimpleApp app = container.instance().select(SimpleApp.class).get();
            app.sayHello();
            app.log.info("Shutting down Weld...");
            container.instance().select(ShutdownManager.class).get().shutdown();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
