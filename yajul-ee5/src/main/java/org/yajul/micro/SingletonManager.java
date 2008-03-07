package org.yajul.micro;

import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.io.IOException;

/**
 * One singleton to rule them all.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 10:37:22 AM
 */
public class SingletonManager {

    private static SingletonManager ourInstance;
    private MicroContainer parent;
    private static final String DEFAULT = "_DEFAULT";

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
                parent = new MicroContainer();
                try {
                    parent.bootstrap("singleton-bootstrap.properties",Thread.currentThread().getContextClassLoader());
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to bootstrap due to " + e,e);
                }
            }
            Object component = parent.getComponent(context);
            if (component == null)
            {
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
