package org.yajul.micro;

import com.google.inject.AbstractModule;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.ReflectionUtil;

/**
 * Module that loads bindings and other modules from a resource.
 * <br>
 * User: josh
 * Date: Nov 10, 2009
 * Time: 11:58:55 AM
 */
public abstract class AbstractResourceModule extends AbstractModule
{
    private static final Logger log = LoggerFactory.getLogger(AbstractResourceModule.class);

    private String resourceName;
    private ClassLoader classLoader;

    public AbstractResourceModule(String resourceName, ClassLoader classLoader)
    {
        this.classLoader = classLoader;
        this.resourceName = resourceName;
    }

    public AbstractResourceModule(String resourceName)
    {
        this(resourceName, ReflectionUtil.getCurrentClassLoader());
    }

    public String getResourceName()
    {
        return resourceName;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    @SuppressWarnings({"ConstantConditions"})
    protected final void configure() {
        // Look for the resource in the class loader.   Load each properties file and register all
        // of the components.
        Enumeration<URL> resources;
        try {
            resources = getClassLoader().getResources(getResourceName());
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
                configureFromResource(stream, url);
                resourceCount++;
            } catch (Exception e) {
                final String msg = "Error configuring from " + url + ": " + e.getMessage();
                log.error(msg,e);
                binder().addError(msg,e);
            }
        }
        log.info("Added components from " + resourceCount + " resources.");
    }

    protected abstract void configureFromResource(InputStream stream, URL url) 
            throws Exception;
}
