package org.yajul.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import java.util.List;

/**
 * Wrapper around PicoContainer.
 * <br>User: Joshua Davis
 * <br>Date: Oct 9, 2005 Time: 8:56:44 AM
 */
public class MicroContainer
{
    private static Logger log = LoggerFactory.getLogger(MicroContainer.class);

    private MutablePicoContainer pico;

    public MicroContainer()
    {
        this(null);
    }

    public MicroContainer(MicroContainer parent)
    {
        log.info("Initializing...");
        MutablePicoContainer parentPico = (parent == null) ? null : parent.pico;
        pico = new DefaultPicoContainer(parentPico);
        pico.change(Characteristics.SINGLE);
        log.info("Initialized.");
    }

    public Object getComponentInstance(Object key)
    {
        return pico.getComponent(key);
    }

    public List instances()
    {
        return pico.getComponents();
    }

    public Class loadImplementation(String className) throws ClassNotFoundException
    {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public void registerSingleton(Object key, Class implementation)
    {
        pico.addComponent(key,implementation);
    }

    public void registerSingleton(Object key, String implementationName) throws ClassNotFoundException
    {
        Class implementationClass = loadImplementation(implementationName);
        registerSingleton(key, implementationClass);
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
}
