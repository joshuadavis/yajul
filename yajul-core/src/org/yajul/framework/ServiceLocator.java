// $Id$
package org.yajul.framework;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.yajul.util.DetailedRuntimeException;
import org.yajul.util.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Provides service implementations (DAO instances, etc.) for the application. The Spring framework is used to allow
 * different configurations of the service resources to be declared without writing any Java code.  These configurations
 * should at least cover the DAO unit testing context, as well as the 'normal' application server context (i.e. normally
 * running application). Encapsulates the Spring tramework so that the business logic layer is not explicitly aware of
 * it. <br> Requires: <ul> <li>The spring framework (lib/spring.jar)</li> <li>Log4j (lib/log4j.jar)</li> <li>Jakarta
 * commons-logging (lib/commons-logging.jar)<br> <i>Note: This is only required by Spring.  YAJUL-core depends on Log4J
 * directly.</i></li> </ul> <br> User: jdavis Date: Feb 19, 2004 Time: 10:58:33 AM
 *
 * @author jdavis
 */
public class ServiceLocator extends BeanFactoryProxy implements BeanFactory
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(ServiceLocator.class.getName());

    /**
     * The system property name that will contain the name of the default application context resource. *
     */
    public static final String CONTEXT_PROPERTY_NAME = "application.context";
    /**
     * The default application context resource name. *
     */
    public static final String DEFAULT_CONTEXT_RESOURCE = "application-context.xml";

    /**
     * The default beanId of the ServiceLocator singleton in the boot context. *
     */
    public static final String BEAN_ID = "serviceLocator";

    /**
     * The name of the resource used to initialize the bean factory. *
     */
    private String resource = null;
    /**
     * The name of the properties resource to load.
     */
    private String propertiesResource = null;
    /**
     * The bean factory reference to this service locator.
     */
    private BeanFactoryReference reference;

    /**
     * Returns the current instance of the singleton.
     *
     * @return ServiceLocator - The current service locator
     */
    public static ServiceLocator getInstance()
    {
        return getInstance(null);
    }

    /**
     * Returns the current instance of the singleton in the given boot context bean definition file.
     *
     * @return ServiceLocator - The current service locator
     */
    public static ServiceLocator getInstance(String bootContext)
    {
        return getInstance(bootContext, BEAN_ID);
    }

    /**
     * Returns a the specified service locator instance in the specified boot context.  The boot context is a Spring
     * bean definition resource (in the classpath) that must have the specified bean id in it.
     *
     * @param bootContext The name of the 'boot' context resource.  A bean factory of bean factories.
     * @param beanId      The bean id (name) of a bean that is an instance of ServiceLocator or some subclass.
     * @return The ServiceLocator.
     */
    public static ServiceLocator getInstance(String bootContext, String beanId)
    {
        // Look ma, no statics!  Here we use the singleton bean factory locator to get
        // the instance of ServiceLocator.
        // NOTE: THIS RELIES ON A RESOURCE NAMED beanRefContext.xml if bootContext is not specified!!!
        BeanFactoryLocator locator = (bootContext == null)
                ? ContextSingletonBeanFactoryLocator.getInstance()
                : ContextSingletonBeanFactoryLocator.getInstance(bootContext);
        BeanFactoryReference reference = locator.useBeanFactory(beanId);
        ServiceLocator serviceLocator = (ServiceLocator) reference.getFactory();
        serviceLocator.setBeanFactoryReference(reference);
        return serviceLocator;
    }

    private void setBeanFactoryReference(BeanFactoryReference reference)
    {
        // The service locator keeps a reference to itself, so that the BeanFactoryLocator will not unload it.
        this.reference = reference;
    }

    /**
     * Empty (a.k.a. default) constructor.
     */
    public ServiceLocator()
    {
        log.info("<ctor>");
        resource = System.getProperty(CONTEXT_PROPERTY_NAME, DEFAULT_CONTEXT_RESOURCE);
    }

    /**
     * Explicitly initializes the service locator from the specified resource. The resource must be in the class path,
     * and it must be a Spring framework XML 'application context' definition.
     *
     * @param resource The name of the XML bean descriptor resource.
     */
    public void initialize(String resource)
    {
        initialize(resource, null);
    }

    /**
     * Explicitly initializes the service locator from the specified resource. The resource must be in the class path,
     * and it must be a Spring framework XML 'application context' definition.
     *
     * @param resource           The name of the XML bean descriptor resource.
     * @param propertiesResource The name of the properties resource (can be null).
     */
    public void initialize(String resource, String propertiesResource)
    {
        synchronized (this)
        {
            if (resource == null || resource.length() == 0)
                throw new IllegalArgumentException("Resource name cannot be null or zero length!");

            if (delegateExists())
            {
                // If the new resource name is the same as the old one, don't reconfigure.
                if (resource.equals(this.resource))
                {
                    if (log.isDebugEnabled())
                        log.debug("initialize() : Resource name identical, not reconfiguring.");
                    return;
                }
                log.info("Destroying existing bean factory...");
                destroy();
            }
            this.resource = resource;
            this.propertiesResource = propertiesResource;
        }
    }

    protected BeanFactory createDelegate()
    {
        log.info("Creating BeanFactory from resource: " + resource);
        DefaultListableBeanFactory beanFactory = loadBeanDefinitions();

        log.info("Processing with placeholder configurer...");
        PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
        Properties props = getProperties();
        if (props != null)
        {
            log.info("Using properties provided by sub-class.");
            cfg.setProperties(props);
        }
        else if (!StringUtil.isEmpty(propertiesResource))
        {
            log.info("Initializing properties from resource: " + propertiesResource);
            ClassPathResource classPathResource = new ClassPathResource(propertiesResource);
            if (!classPathResource.exists())
                log.warn("Resource " + propertiesResource + " not found in the classpath!");
            cfg.setLocation(classPathResource);
        }
        cfg.postProcessBeanFactory(beanFactory);
        log.info("BeanFactory created.");
        return beanFactory;
    }

    private DefaultListableBeanFactory loadBeanDefinitions()
    {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        int count = 0;
        try
        {
            // If there are multiple resources, load 'em!
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Enumeration enum = loader.getResources(resource);
            while (enum.hasMoreElements())
            {
                URL url = (URL) enum.nextElement();
                reader.loadBeanDefinitions(new UrlResource(url.toExternalForm()));
                count++;
            }
        }
        catch (IOException e)
        {
            throw new DetailedRuntimeException("Unable to create bean factory from resource '" + resource + "' due to: " + e, e);
        }
        // If the class loader was not able to return a list of resources, try a single resource.
        if (count == 0)
            reader.loadBeanDefinitions(new ClassPathResource(resource));
        return beanFactory;
    }

    /**
     * Returns a bean that implements some service.  The framework will have automatically initialized the service, and
     * any dependent services.
     *
     * @param beanId The bean id (or beanId).
     * @return The bean instance, or null if it was not found.
     * @throws BeanNotFoundException if the bean could not be found.
     */
    public Object requireBean(String beanId) throws BeanNotFoundException
    {
        Object bean = getBean(beanId);
        if (bean == null)
        {
            throw new BeanNotFoundException("Bean '" + beanId + "' was not found.");
        }
        return bean;
    }

    /**
     * Returns the name of the resource used to configure this service locator.
     *
     * @return the name of the resource used to configure this service locator.
     */
    public String getResourceName()
    {
        return resource;
    }

    /**
     * Sub classes may override this to provide configuration properties.
     *
     * @return configuration properties
     */
    protected Properties getProperties()
    {
        return null;
    }

    public void release()
    {
        if (reference != null)
        {
            reference.release();
            reference = null;
        }
    }

    protected void finalize() throws Throwable
    {
        destroy();
        release();
    }

}
