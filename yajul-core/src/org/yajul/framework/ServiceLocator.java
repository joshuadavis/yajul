package org.yajul.framework;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.apache.log4j.Logger;

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

    private static ServiceLocator ourInstance = null;

    /** The Spring framework bean factory that is being encapsulated by this class. **/
    private BeanFactory beanFactory = null;
    /** The name of the resource used to initialize the Spring framework. **/
    private String resource = null;

    public synchronized static ServiceLocator getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new ServiceLocator();
        }
        return ourInstance;
    }

    private ServiceLocator()
    {
    }

    private void initialize()
    {
        // Initialize the Spring framework with the configuration specified
        // by the application.context system property, or 'application-context.xml'
        // if the system property is not specified.
        String resource = System.getProperty("application.context","application-context.xml");
        initialize(resource);
    }

    private BeanFactory getBeanFactory()
    {
        synchronized (this)
        {
            if (beanFactory == null)
                initialize();
        }
        return beanFactory;
    }

    /**
     * Explicitly initializes the service locator from the specified resource.
     * The resource must be in the class path, and it must be a Spring framework
     * XML 'application context' definition.
     * @param resource The name of the resource.
     */
    public void initialize(String resource)
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
            this.resource = resource;
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
}
