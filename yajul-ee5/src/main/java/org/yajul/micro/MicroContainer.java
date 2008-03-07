package org.yajul.micro;

import org.picocontainer.*;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * A picocontainer that does cacheing and auto registration of components specified as classes.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 12:10:39 PM
 */
public class MicroContainer extends DefaultPicoContainer {

    private Logger log = LoggerFactory.getLogger(MicroContainer.class);

    public MicroContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(componentFactory, lifecycleStrategy, parent, componentMonitor);
        change(Characteristics.SINGLE);
    }

    public MicroContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(componentFactory, lifecycleStrategy, parent, new NullComponentMonitor() );
    }

    public MicroContainer(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(new AdaptingBehavior(), lifecycleStrategy, parent, monitor);
    }

    public MicroContainer(LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(new NullComponentMonitor(), lifecycleStrategy, parent);
    }
    
    public MicroContainer(ComponentFactory componentFactory, PicoContainer parent) {
        this(componentFactory, new StartableLifecycleStrategy(new NullComponentMonitor()), parent, new NullComponentMonitor());
    }

    public MicroContainer(ComponentMonitor monitor, PicoContainer parent) {
        this(new AdaptingBehavior(), new StartableLifecycleStrategy(monitor), parent, monitor);
    }

    public MicroContainer(ComponentFactory componentFactory) {
        this(componentFactory, null);
    }

    public MicroContainer(ComponentMonitor monitor) {
        this(monitor, new StartableLifecycleStrategy(monitor), null);
    }

    public MicroContainer(PicoContainer parent) {
        this(new AdaptingBehavior(), parent);
    }

    public MicroContainer() {
        this(new AdaptingBehavior(), null);
    }

    @Override
    public Object getComponent(Object componentKeyOrType, Class<? extends Annotation> annotation) {
        // Automatically add the component it the key is the implementation class and the
        // annotation isn't present, and there is no existing component adapter for the component.
        synchronized (this) {
            if (componentKeyOrType instanceof Class &&
                    annotation == null &&
                    getComponentAdapter(componentKeyOrType) == null) {
                addComponent(componentKeyOrType);
            }
        }
        return super.getComponent(componentKeyOrType, annotation);
    }

    /**
     * Set up component definitions from properties resources in the classpath.  The properties file will
     * have a class name (interface name) as the key, and the implementation class as the value.
     * @param resourceName properties resource name
     * @param classLoader the class loader to use
     * @throws IOException if something goes wrong.
     */
    public void bootstrap(String resourceName,ClassLoader classLoader) throws IOException {
        // Look for the resource in the class loader.   Load each properties file and register all
        // of the components.
        Enumeration<URL> resources = classLoader.getResources(resourceName);
        int componentCount = 0;
        int resourceCount = 0;
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            log.debug("Loading " + url + " ...");
            InputStream stream = url.openStream();
            Properties props = new Properties();
            props.load(stream);
            resourceCount++;
            Enumeration keyNames = props.propertyNames();
            while (keyNames.hasMoreElements()) {
                String keyName = (String) keyNames.nextElement();
                String valueName = props.getProperty(keyName);
                // If the key is a class (interface), then use it.
                Object key = processName(keyName,classLoader);
                Object component = processName(valueName,classLoader);
                log.debug("Adding " + key + " : " + component + " ...");
                addComponent(key,component);
                componentCount++;
            }            
        }
        log.info("Added " + componentCount + " components from " + resourceCount + " resources.");
    }

    private Object processName(String name, ClassLoader classLoader) {

        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            log.info(name + " is not a class");
            return name;
        }
    }
}