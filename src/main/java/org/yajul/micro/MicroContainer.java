package org.yajul.micro;

import org.picocontainer.*;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * A picocontainer that does cacheing and auto registration of components specified as classes.  It can also bootstrap
 * itself from properties files found in the classpath.   The names in the properties files are used as keys, and the
 * values are assumed to be implementation classes.   If the names are not interface names, they will be simple
 * string keys.   If the implementation class implements Configuration, then it will be immediately instantiated and
 * the addComponents() method will be called to add more components to the container.  This way you can have
 * one class in the properties file that bootstraps all of your component definitions in a typesafe manner.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 12:10:39 PM
 */
public class MicroContainer extends DefaultPicoContainer {

    private static Logger log = LoggerFactory.getLogger(MicroContainer.class);

    private String name;

    public MicroContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(componentFactory, lifecycleStrategy, parent, componentMonitor);
        change(Characteristics.SINGLE);
    }

    public MicroContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(componentFactory, lifecycleStrategy, parent, new NullComponentMonitor());
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

    public MicroContainer(String name, MicroContainer parent) {
        this(parent);
        this.name = name;
    }

    public MicroContainer(String name) {
        this(name, null);
    }


    @Override
    public Object getComponent(Object componentKeyOrType, Class<? extends Annotation> annotation) {
        // Automatically add the component it the key is the implementation class and the
        // annotation isn't present, and there is no existing component adapter for the component.
        synchronized (this) {
            if (componentKeyOrType instanceof Class &&
                    annotation == null &&
                    exists(componentKeyOrType)) {
                if (log.isDebugEnabled())
                    log.debug("Adding " + componentKeyOrType);
                addComponent(componentKeyOrType);
            }
        }
        return super.getComponent(componentKeyOrType, annotation);
    }

    /**
     * Add a component using the names of interfaces/classes and a class loader.
     * @param keyName the name of the key interface, or just a string if it's not in the ClassLoader
     * @param valueName the name of the implementation class
     * @param classLoader the class loader to use
     * @return the component adapter for the component
     */
    public ComponentAdapter<?> addComponentByReflection(String keyName,String valueName,ClassLoader classLoader)
    {
        // If the key is a class (interface), then use it.
        Object key = processName(keyName, classLoader);
        Object component = processName(valueName, classLoader);
        log.debug("Adding " + key + " : " + component + " ...");
        addComponent(key, component);
        return getComponentAdapter(key);
    }

    /**
     * Set up component definitions from properties resources in the classpath.  The properties file will
     * have a class name (interface name) as the key, and the implementation class as the value.
     *
     * @param resourceName properties resource name
     * @param classLoader  the class loader to use
     * @throws IOException if something goes wrong.
     */
    public void bootstrap(String resourceName, ClassLoader classLoader) throws IOException {
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
            componentCount += addComponentsFromProperties(props, classLoader);
        }
        log.info("Added " + componentCount + " components from " + resourceCount + " resources.");
    }

    /**
     * Add components from a properties file, where the property names are interfaces/keys, and the
     * property values are implementation class names.
     * @param props the properties
     * @param classLoader the class loader to use for looking up class/interface names.
     * @return the number of components added.
     */
    public int addComponentsFromProperties(Properties props, ClassLoader classLoader) {
        int componentCount = 0;
        Enumeration keyNames = props.propertyNames();
        while (keyNames.hasMoreElements()) {
            String keyName = (String) keyNames.nextElement();
            String valueName = props.getProperty(keyName);
            ComponentAdapter<?> adapter = addComponentByReflection(keyName,valueName,classLoader);
            // If the key is a class (interface), then use it.
            Object key = adapter.getComponentKey();
            Class<?> impl = adapter.getComponentImplementation();
            Configuration config = null;
            // If the component is an implementation class and that class implements the Configuration interface
            // then we get an instance of it now and run it.
            if (Configuration.class.isAssignableFrom(impl)) {
                // Instantiate the component.
                config = (Configuration) getComponent(key);
            }
            if (config != null) {
                log.info("Configuring with " + config);
                int before = getComponentAdapters().size();
                config.addComponents(this);
                int added = getComponentAdapters().size() - before;
                log.info("Added " + added + " components from " + config);
                componentCount += added;
            }
            componentCount++;
        }
        return componentCount;
    }

    /**
     * Returns true if the component for the key already exists.
     * @param key the component key
     * @return true if it exists, false if not
     */
    public boolean exists(Object key) {
        return getComponentAdapter(key) == null;
    }

    private Configuration getAsConfig(Object key, Object component) {
        Configuration config = null;
        // If the component is an implementation class and that class implements the Configuration interface
        // then we get an instance of it now and run it.
        if (component instanceof Class) {
            Class aClass = (Class) component;
            if (Configuration.class.isAssignableFrom(aClass)) {
                //noinspection UnusedAssignment
                config = (Configuration) getComponent(key);
            }
        } else if (component instanceof Configuration) {
            //noinspection UnusedAssignment
            config = (Configuration) component;
        }
        return config;
    }

    /**
     * Add components to the container using the configuration.
     * @param config the configuration object
     * @return the number of components added
     */
    public int configure(Configuration config) {

        log.info("Configuring with " + config);
        int before = getComponentAdapters().size();
        config.addComponents(this);
        int added = getComponentAdapters().size() - before;
        log.info("Added " + added + " components from " + config);
        return added;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName()).append("{");
        StringUtil.appendIfNotEmpty(name, sb);
        Collection<ComponentAdapter<?>> adapters = getComponentAdapters();
        for (ComponentAdapter<?> adapter : adapters) {
            sb.append("\n ").append(adapter.getComponentKey().toString()).append(" -> ")
                    .append(adapter.getComponentImplementation().getName());
        }
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Returns the key for a given name.   If 'name' can be loaded with the class loader, the
     * class is returned.   If 'name' is not a class, then only 'name' is returned.
     *
     * @param name        the name, might be a class
     * @param classLoader the classloader to use
     * @return the name or the loaded class
     */
    public static Object processName(String name, ClassLoader classLoader) {

        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            log.debug(name + " is not a class, leaving it as a string");
            return name;
        }
    }
}