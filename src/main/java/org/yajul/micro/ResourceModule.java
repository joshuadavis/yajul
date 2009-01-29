package org.yajul.micro;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.ReflectionUtil;

/**
 * Guice module that uses properties resources to define components.
 * Names are key class names (usually interface names), values are implementation class names.
 * If a value is a class name that implements Module, the class will be instantiated and
 * the module will be installed into the injector immediately.
 * <br>
 * User: josh
 * Date: Dec 30, 2008
 * Time: 4:09:31 PM
 */
public class ResourceModule extends AbstractModule {
    private final static Logger log = LoggerFactory.getLogger(ResourceModule.class);

    private String resourceName;
    private ClassLoader classLoader;
    private Scope scope = Scopes.SINGLETON;

    public ResourceModule(String resourceName, ClassLoader classLoader,Scope scope) {
        this.resourceName = resourceName;
        this.classLoader = classLoader;
        this.scope = scope;
    }

    public ResourceModule(String resourceName) {
        this(resourceName,ReflectionUtil.getCurrentClassLoader(),Scopes.SINGLETON);
    }

    protected void configure() {
        // Look for the resource in the class loader.   Load each properties file and register all
        // of the components.
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(resourceName);
        } catch (IOException e) {
            binder().addError(e.getMessage(),e);
            return;
        }
        int resourceCount = 0;
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            log.debug(url + " ...");
            try {
                InputStream stream = url.openStream();
                Properties props = new Properties();
                props.load(stream);
                resourceCount++;
                addComponentsFromProperties(props, classLoader);
            } catch (IOException e) {
                binder().addError(e.getMessage(),e);
            }
        }
        log.info("Added components from " + resourceCount + " resources.");
    }

    /**
     * Add components from a properties file, where the property names are interfaces/keys, and the
     * property values are implementation class names.
     *
     * @param props       the properties
     * @param classLoader the class loader to use for looking up class/interface names.
     */
    public void addComponentsFromProperties(Properties props, ClassLoader classLoader) {
        Enumeration keyNames = props.propertyNames();
        if (log.isDebugEnabled())
           log.debug("addComponentsFromProperties() : " + props.size() + " properties.");
        while (keyNames.hasMoreElements()) {
            String keyName = (String) keyNames.nextElement();
            String valueName = props.getProperty(keyName);
            // If the key is a class (interface), then use it.
            Object key = MicroContainer.processName(keyName, classLoader);
            Class<?> impl = (Class<?>) MicroContainer.processName(valueName, classLoader);
            // If this is a module, use it to configure other components.
            if (Module.class.isAssignableFrom(impl)) {
                Module module = (Module) ReflectionUtil.createInstanceNoThrow(impl);
                if (module != null) {
                    log.info("Configuring with " + module);
                    binder().install(module);
                }
            }
            else if (key instanceof Class) {
                Class keyClass = (Class) key;
                if (keyClass.equals(impl)) {
                    bind(impl).in(scope);
                }
                else {
                    //noinspection unchecked
                    bind(keyClass).to(impl).in(scope);
                }
            }
            else
            {
                // The key is a string.
                if (log.isDebugEnabled())
                   log.debug("addComponentsFromProperties() : ignoring [" + keyName + "=" + valueName + "]");
            }
        }
    }
}
