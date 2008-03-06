package org.yajul.micro;

import org.picocontainer.MutablePicoContainer;
import org.yajul.util.ReflectionUtil;

/**
 * Default microcontainer (singleton) bootstrap for Yajul.
 * <br>User: Joshua Davis
 * Date: Mar 6, 2008
 * Time: 7:25:52 AM
 */
public class DefaultBootstrap implements Bootstrap {
    public void addComponentsTo(MutablePicoContainer container) {
        addSingletonComponents(container);
    }

    public static void addSingletonComponents(MutablePicoContainer container)
    {
        addComponentClassName(container,"org.yajul.ee5.jmx.JmxBridge");
    }

    public static void addComponentClassName(MutablePicoContainer container, String className)
    {
        Class clazz = null;
        try {
            clazz = ReflectionUtil.getCurrentClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found!",e);
        }
        container.addComponent(clazz);
    }
}
