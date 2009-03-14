package org.yajul.micro;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.AbstractScanner;

import java.util.Map;

/**
 * A Guice-based micro container.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 12:10:39 PM
 */
public class MicroContainer {
    private final static Logger log = LoggerFactory.getLogger(MicroContainer.class);

    private Injector injector;

    public MicroContainer(Injector injector)
    {
        this.injector = injector;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName()).append("{");
        Map<Key<?>,Binding<?>> bindings = injector.getBindings();
        for (Map.Entry<Key<?>, Binding<?>> keyBindingEntry : bindings.entrySet())
        {
            sb.append("\n ").append(keyBindingEntry.getKey().toString()).append(" -> ")
                    .append(keyBindingEntry.getValue().toString());
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
            String n = name.contains("/") ? AbstractScanner.filenameToClassname(name) : name;
            return classLoader.loadClass(n);
        } catch (ClassNotFoundException e) {
            if (log.isTraceEnabled())
                log.trace(name + " is not a class, leaving it as a string");
            return name;
        }
    }

    public Injector getInjector() {
        return injector;
    }

    public <T> T getComponent(Class<T> componentType) {
        return getInjector().getInstance(componentType);
    }

    public <T> T getComponent(Class<T> componentType,String name) {
        return getInjector().getInstance(Key.get(componentType,Names.named(name)));
    }
}