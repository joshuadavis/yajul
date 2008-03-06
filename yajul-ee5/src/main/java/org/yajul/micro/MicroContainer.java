package org.yajul.micro;

import org.picocontainer.*;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.yajul.util.ReflectionUtil;

import java.lang.annotation.Annotation;

/**
 * A picocontainer that does cacheing and auto registration of components specified as classes.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 12:10:39 PM
 */
public class MicroContainer extends DefaultPicoContainer {

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

    public void bootstrap(String className) {
        Bootstrap boot = ReflectionUtil.createInstance(className,Bootstrap.class);
        boot.addComponentsTo(this);
    }

    public void bootstrapFromSystemProperties() {
        String className = System.getProperty("org.yajul.microcontainer.bootstrap",DefaultBootstrap.class.getName());
    }
}