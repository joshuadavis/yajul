package org.yajul.micro;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.net.URL;

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
public class PropertiesResourceModule extends AbstractResourceModule
{
    private final static Logger log = LoggerFactory.getLogger(PropertiesResourceModule.class);

    private Scope scope = Scopes.SINGLETON;

    public PropertiesResourceModule(String resourceName, ClassLoader classLoader, Scope scope)
    {
        super(resourceName, classLoader);
        this.scope = scope;
    }

    public PropertiesResourceModule(String resourceName)
    {
        this(resourceName, ReflectionUtil.getCurrentClassLoader(), Scopes.SINGLETON);
    }

    protected void configureFromResource(InputStream stream, URL url)
            throws IOException
    {
        Properties props = new Properties();
        props.load(stream);
        addComponentsFromProperties(props, getClassLoader());
    }

    /**
     * Add components from a properties file, where the property names are interfaces/keys, and the
     * property values are implementation class names.
     *
     * @param props       the properties
     * @param classLoader the class loader to use for looking up class/interface names.
     */
    public void addComponentsFromProperties(Properties props, ClassLoader classLoader)
    {
        Enumeration keyNames = props.propertyNames();
        if (log.isDebugEnabled())
            log.debug("addComponentsFromProperties() : " + props.size() + " properties.");
        while (keyNames.hasMoreElements())
        {
            String keyName = (String) keyNames.nextElement();
            String valueName = props.getProperty(keyName);
            // If the key is a class (interface), then use it.
            Object key = MicroContainer.processName(keyName, classLoader);
            Class<?> impl = (Class<?>) MicroContainer.processName(valueName, classLoader);
            // If this is a module, use it to configure other components.
            if (ModuleHelper.isModule(impl))
            {
                ModuleHelper.bindModuleClass(binder(),impl);
            }
            else if (key instanceof Class)
            {
                Class keyClass = (Class) key;
                if (keyClass.equals(impl))
                {
                    bind(impl).in(scope);
                }
                else
                {
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
