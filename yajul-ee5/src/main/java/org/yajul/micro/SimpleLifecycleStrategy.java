package org.yajul.micro;

import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

/**
 * A simple lifecycle strategy for the Yajul wrapper around Pico.
 * <br>User: Joshua Davis
 * Date: Sep 11, 2007
 * Time: 8:10:58 AM
 */
public class SimpleLifecycleStrategy  implements LifecycleStrategy {
    public void start(Object component)
    {
        if (component != null)
        {
            if (component instanceof Startable)
            {
                Startable s = (Startable) component;
                s.start();
            }
            if (component instanceof LifecycleAware)
            {
                LifecycleAware la = (LifecycleAware) component;
                la.initialize();
            }
        }
    }

    public void stop(Object component)
    {
        if (component != null)
        {
            if (component instanceof Startable)
            {
                Startable s = (Startable) component;
                s.stop();
            }
            if (component instanceof LifecycleAware)
            {
                LifecycleAware la = (LifecycleAware) component;
                la.terminate();
            }
        }
    }

    public void dispose(Object component)
    {
        if (component != null)
        {
            if (component instanceof Disposable)
            {
                Disposable d = (Disposable) component;
                d.dispose();
            }
        }
    }

    public boolean hasLifecycle(Class type)
    {
        return
                Startable.class.isAssignableFrom(type) ||
                Disposable.class.isAssignableFrom(type) ||
                LifecycleAware.class.isAssignableFrom(type);
    }
}
