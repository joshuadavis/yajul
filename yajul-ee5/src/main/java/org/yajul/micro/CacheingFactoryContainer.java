package org.yajul.micro;

import org.picocontainer.*;

/**
 * TODO: Class level comments
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 12:10:39 PM
 */
public class CacheingFactoryContainer extends DefaultPicoContainer {
    public CacheingFactoryContainer(ComponentFactory componentFactory, PicoContainer parent) {
        super(componentFactory, parent);
    }

    public CacheingFactoryContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        super(componentFactory, lifecycleStrategy, parent);
    }

    public CacheingFactoryContainer(ComponentFactory componentFactory, LifecycleStrategy lifecycleStrategy, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(componentFactory, lifecycleStrategy, parent, componentMonitor);
    }

    public CacheingFactoryContainer(ComponentMonitor monitor, PicoContainer parent) {
        super(monitor, parent);
    }

    public CacheingFactoryContainer(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        super(monitor, lifecycleStrategy, parent);
    }

    public CacheingFactoryContainer(LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        super(lifecycleStrategy, parent);
    }

    public CacheingFactoryContainer(ComponentFactory componentFactory) {
        super(componentFactory);
    }

    public CacheingFactoryContainer(ComponentMonitor monitor) {
        super(monitor);
    }

    public CacheingFactoryContainer(PicoContainer parent) {
        super(parent);
    }

}
