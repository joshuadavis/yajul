package org.yajul.micro;

import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * One singleton to rule them all.  This is actually a single level hierarchy of micro-containers.
 * The parent contains all the YAJUL singletons.   The
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 10:37:22 AM
 */
public class SingletonManager {

    private Logger log = LoggerFactory.getLogger(SingletonManager.class);

    /**
     * The resource that the bootstrapper looks for.
     */
    public static final String BOOTSTRAP_RESOURCE_NAME = "singleton-bootstrap.properties";

    private static final String DEFAULT = "_DEFAULT";

    private static SingletonManager ourInstance;
    private MicroContainer parent;

    public static SingletonManager getInstance() {
        synchronized (SingletonManager.class) {
            if (ourInstance == null)
                ourInstance = new SingletonManager();
            return ourInstance;
        }
    }

    public static PicoContainer getSingletonContainer(String context) {
        return getInstance().getContainer(context);
    }

    public static <T> T getDefaultSingleton(Class<T> componentType) {
        return getInstance().findOrCreateComponent(componentType);
    }

    public static PicoContainer defaultContainer() {
        return getInstance().getDefaultContainer();
    }

    private <T> T findOrCreateComponent(Class<T> componentType) {
        MutablePicoContainer container = getDefaultContainer();
        synchronized (container)
        {
            if (container.getComponentAdapter(componentType) == null)
                container.addComponent(componentType);
            return container.getComponent(componentType);
        }
    }


    private MutablePicoContainer getContainer(String context) {
        synchronized (this)
        {
            if (parent == null) {
                log.info("Creating parent container...");
                parent = new MicroContainer();
                try {
                    log.info("Bootstrapping...");
                    parent.addComponent(this);
                    parent.bootstrap(BOOTSTRAP_RESOURCE_NAME,Thread.currentThread().getContextClassLoader());
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to bootstrap due to " + e,e);
                }
            }
            Object component = parent.getComponent(context);
            if (component == null)
            {
                log.info("Creating context container " + context + " ...");
                MutablePicoContainer child = new DefaultPicoContainer(parent);
                child.change(Characteristics.SINGLE);
                parent.addComponent(context,child);
                return child;
            }
            else
                return (MutablePicoContainer) component;
        }
    }

    private SingletonManager() {
    }

    public MutablePicoContainer getDefaultContainer() {
        return getContainer(DEFAULT);
    }

}
