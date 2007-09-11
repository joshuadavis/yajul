package org.yajul.micro;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Wrapper around PicoContainer.
 * <br>User: Joshua Davis
 * <br>Date: Oct 9, 2005 Time: 8:56:44 AM
 */
public class MicroContainer
{
    private static Logger log = LoggerFactory.getLogger(MicroContainer.class);

    private MutablePicoContainer pico;
    private ComponentMonitor monitor;
    private LifecycleStrategy lifecycleStrategy;

    public MicroContainer()
    {
        this(null);
    }

    public MicroContainer(MicroContainer parent)
    {
        log.info("Initializing...");
        MutablePicoContainer parentPico = (parent == null) ? null : parent.pico;
        monitor = new Slf4jComponentMonitor();
        lifecycleStrategy = new SimpleLifecycleStrategy();
        ComponentAdapterFactory adapterFactory = new DefaultComponentAdapterFactory(monitor, lifecycleStrategy);
        pico = new DefaultPicoContainer(adapterFactory, lifecycleStrategy, parentPico);
        log.info("Initialized.");
    }

    public Object getComponentInstance(Object key)
    {
        return pico.getComponentInstance(key);
    }

    public List instances()
    {
        return pico.getComponentInstances();
    }

    public Class loadImplementation(String className) throws ClassNotFoundException
    {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public void registerSingleton(Object key, Class implementation)
    {
        registerSingleton(key, implementation, null);
    }

    public void registerSingleton(Object key, Class implementation, Map properties)
    {
        // No properties? Just do the normal registration.
        if (properties == null || properties.size() == 0)
        {
            // Use the default component adapter factory to create an adapter for this implementation.
            pico.registerComponentImplementation(key, implementation);
        }
        else
        {
            // Otherwise, add the Bean adapter to inject all the properties
            // into the object using setters after it is constructed.
            ConstructorInjectionComponentAdapter componentAdapter =
                    new ConstructorInjectionComponentAdapter(
                            key, implementation, null, false, monitor, lifecycleStrategy);
            // The bean property component adapter needs to wrap the CI component adapter
            // because the bean properties should be injected after the component is created.
            BeanPropertyComponentAdapter beanAdaptor =
                    new BeanPropertyComponentAdapter(componentAdapter);
            beanAdaptor.setProperties(properties);
            pico.registerComponent(
                    new CachingComponentAdapter(
                            beanAdaptor));
        }
    }

    public void registerComponentInstance(Object object)
    {
        // Regsisters an instance where it's class is the key.
        pico.registerComponentInstance(object);
    }

    public void registerSingleton(Object key, String implementationName) throws ClassNotFoundException
    {
        Class implementationClass = loadImplementation(implementationName);
        registerSingleton(key, implementationClass);
    }

    public void registerEmptySingleton(Object key)
    {
        pico.registerComponent(new EmptyComponentAdapter(key));
    }

    public static void initializeComponent(Object component)
    {
        if (component instanceof LifecycleAware)
        {
            LifecycleAware lifecycleAware = (LifecycleAware) component;
            lifecycleAware.initialize();
        }
    }

    public static void destroyComponent(Object component)
    {
        if (component instanceof LifecycleAware)
        {
            LifecycleAware lifecycleAware = (LifecycleAware) component;
            lifecycleAware.terminate();
        }
    }

    public void dispose()
    {
        pico.dispose();
    }

    public void start()
    {
        pico.start();
    }

    public void stop()
    {
        pico.stop();
    }

    private class EmptyComponentAdapter extends InstanceComponentAdapter
    {

        public EmptyComponentAdapter(Object componentKey) throws AssignabilityRegistrationException, NotConcreteRegistrationException
        {
            super(componentKey, "");
        }

        public Object getComponentInstance(PicoContainer container)
        {
            return null;
        }
    }
}
