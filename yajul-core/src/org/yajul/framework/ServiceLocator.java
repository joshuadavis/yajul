package org.yajul.framework;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.apache.log4j.Logger;
import org.yajul.util.StringUtil;
import org.yajul.util.ObjectFactory;

import java.util.Properties;

/**
 * Provides service implementations (DAO instances, etc.) for the application.
 * The Spring framework is used to allow different configurations of the
 * service resources to be declared without writing any Java code.  These configurations
 * should at least cover the DAO unit testing context, as well as the 'normal'
 * application server context (i.e. normally running application).
 * Encapsulates the Spring tramework so that the business logic layer
 * is not explicitly aware of it.
 * <br>
 * Requires:
 * <ul>
 * <li>The spring framework (lib/spring.jar)</li>
 * <li>Log4j (lib/log4j.jar)</li>
 * <li>Jakarta commons-logging (lib/commons-logging.jar)</li>
 * </ul>
 * User: jdavis
 * Date: Feb 19, 2004
 * Time: 10:58:33 AM
 * @author jdavis
 */
public class ServiceLocator
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(ServiceLocator.class.getName());

    /** The system property name that will contain the name of the default application context resource. **/
    public static final String CONTEXT_PROPERTY_NAME = "application.context";
    /** The default application context resource name. **/
    public static final String DEFAULT_CONTEXT_RESOURCE = "application-context.xml";

    /** The system property name that will contain the name of the service locator class. **/
    public static final String LOCATOR_PROPERTY_NAME = "service.locator.class";
    /** The default locator class name. **/
    public static final String DEFAULT_LOCATOR_CLASS_NAME = ServiceLocator.class.getName();

    private static ServiceLocator ourInstance = null;

    /** The Spring framework bean factory that is being encapsulated by this class. **/
    private XmlBeanFactory beanFactory = null;
    /** The name of the resource used to initialize the Spring framework. **/
    private String resource = null;

    /**
     * Returns the current instance of the singleton.<br>
     * NOTE: Since this object manages singletons, it should probably be the *only* singleton
     * in the system.  It serves as a bootstrapper.
     * @return ServiceLocator - The current service locator.
     */
    public synchronized static ServiceLocator getInstance()
    {
        if (ourInstance == null)
        {
            String className = System.getProperty(LOCATOR_PROPERTY_NAME,DEFAULT_LOCATOR_CLASS_NAME);
            log.info("Instantiating service locator class: " + className);
            ourInstance = (ServiceLocator) ObjectFactory.createInstance(className);
        }
        return ourInstance;
    }

    /**
     * Sets the current instance of the singleton.<br>
     */
    protected synchronized static void registerInstance(ServiceLocator locator)
    {
        if (ourInstance != null)
        {
            ourInstance.destroy();
        }
        ourInstance = locator;
    }

    /**
     * Empty (a.k.a. default) constructor.
     */
    public ServiceLocator()
    {
    }

    /**
     * Explicitly initializes the service locator from the specified resource.
     * The resource must be in the class path, and it must be a Spring framework
     * XML 'application context' definition.
     * @param resource The name of the XML bean descriptor resource.
     */
    public void initialize(String resource)
    {
        initialize(resource,null);
    }

    /**
     * Explicitly initializes the service locator from the specified resource.
     * The resource must be in the class path, and it must be a Spring framework
     * XML 'application context' definition.
     * @param resource The name of the XML bean descriptor resource.
     * @param propertiesResource The name of the properties resource (can be null).
     */
    public void initialize(String resource,String propertiesResource)
    {
        synchronized(this)
        {
            if (resource == null || resource.length() == 0)
                throw new IllegalArgumentException("Resource name cannot be null or zero length!");

            if (beanFactory != null)
            {
                // If the new resource name is the same as the old one, don't reconfigure.
                if (resource.equals(this.resource))
                {
                    if (log.isDebugEnabled())
                        log.debug("initialize() : Resource name identical, not reconfiguring.");
                    return;
                }
                log.info("Replacing existing bean factory...");
            }
            log.info("Initializing from resource: " + resource);
            beanFactory = new XmlBeanFactory(new ClassPathResource(resource));

            log.info("Processing with placeholder configurer...");
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            initializeConfigurer(propertiesResource, cfg);
            cfg.postProcessBeanFactory(beanFactory);
            this.resource = resource;
            log.info("Initialization complete.");
        }
    }

    /**
     * Returns a bean that implements some service.  The framework will have
     * automatically initialized the service, and any dependent services.
     * @param name
     * @return The bean instance, or null if it was not found.
     */
    public Object getBean(String name)
    {
        Object bean = getBeanFactory().getBean(name);
        if (bean == null)
        {
            log.warn("Bean '" + name + "' was not found.");
        }
        return bean;
    }

    /**
     * Returns the name of the resource used to configure this service locator.
     * @return the name of the resource used to configure this service locator.
     */
    public String getResourceName()
    {
        return resource;
    }

    /**
     * Cleans up the bean factory and all associated resources.
     */
    public void destroy()
    {
        synchronized(this)
        {
            if (beanFactory != null)
            {
                log.info("Destroying singletons...");
                beanFactory.destroySingletons();
            }
            beanFactory = null;
            // NOTE: Keep the resource name so that this singleton will initialize the next time around.
        }
    }

    /**
     * Sub classes may override this to provide configuration properties.
     * @return configuration properties
     */
    protected Properties getProperties()
    {
        return null;
    }

    protected final BeanFactory getBeanFactory()
    {
        synchronized (this)
        {
            if (beanFactory == null)
                initialize();
        }
        return beanFactory;
    }


    protected void finalize() throws Throwable
    {
        destroy();
    }

    private void initialize()
    {
        // Initialize the Spring framework with the configuration specified
        // by the application.context system property, or 'application-context.xml'
        // if the system property is not specified.
        String resource = System.getProperty(CONTEXT_PROPERTY_NAME,DEFAULT_CONTEXT_RESOURCE);
        initialize(resource);
    }

    /**
     * Sub-classes can override this method to set up the configurer with properties.
     * @param propertiesResource The name of the property resource to use (may be null).
     * @param cfg The configurer.
     */
    private void initializeConfigurer(String propertiesResource, PropertyPlaceholderConfigurer cfg)
    {
        Properties props = getProperties();
        if (props != null)
        {
            log.info("Using properties provided by sub-class.");
            cfg.setProperties(props);
        }
        else if (!StringUtil.isEmpty(propertiesResource))
        {
            log.info("Initializing properties from resource: " + propertiesResource);
            cfg.setLocation(new ClassPathResource(propertiesResource));
        }
    }
}
