// $Id$
package org.yajul.framework;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
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
 * it.
 * <br>
 * <b>NOTE:</b> The primary use case for this class is as a bean factory deployed in an EAR.  If you want to use this
 * from a web application as well, it is best to use a separate 'bootstrap' context by using:<br>
 * <code>ServiceLocator.getInstance(webBootContext)</code> from inside a startup Servlet or Struts plug-in.
 * <br>
 * The default behavior loads a resource named 'refBeanContext.xml' from the current classs loader and post
 * processes the context with any system properties, resolving any <code>${systempropertyname}</code>
 * references.  To use a <i>different</i> bootstrap context set the system property
 * <code>org.yajul.framework.ServiceLocator.boot</code> to point to a different resource. In this resource, you can
 * specify a sub-class of ServiceLocator for more fine grained control.  This is the recommended approach for using
 * ServiceLocator to manage DAOs, etc. deployed in an EAR.
 * <hr/>
 * An example bootstrap context with a 'child' bean factory:<br>
 * <i>NOTE:</i> The <code>index</code> attributes in the constructor arguments allow the XmlBeanFactory to resolve
 * overloaded constructor methods.   If these attributes are incorrect or missing, the instance will not be created.
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 *  &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd"&gt;
 *  &lt;beans&gt;
 *      &lt;!-- This bean will be initialized by ServiceLocator.initialize() --&gt;
 *      &lt;bean id="serviceLocator" class="org.yajul.framework.ServiceLocator"/&gt;
 *      &lt;!-- This bean will be initialized by ServiceLocator.initialize() --&gt;
 *      &lt;bean id="testChild" class="org.springframework.beans.factory.xml.XmlBeanFactory"&gt;
 *          &lt;constructor-arg index="0"&gt;
 *              &lt;value&gt;classpath:child-applicationContext.xml&lt;/value&gt;
 *          &lt;/constructor-arg&gt;
 *          &lt;constructor-arg index="1"&gt;
 *              &lt;ref bean="serviceLocator"/&gt;
 *          &lt;/constructor-arg&gt;
 *      &lt;/bean&gt;
 *  &lt;/beans&gt;
 * </pre>
 * <hr/>
 * Requires:
 * <ul>
 * <li>The spring framework (lib/spring.jar)</li>
 * <li>Log4j (lib/log4j.jar)</li>
 * <li>Jakarta commons-logging (lib/commons-logging.jar)<br>
 * <i>Note: This is only required by Spring.  YAJUL-core depends on Log4J directly.</i>
 * </li>
 * </ul><br>
 * User: jdavis Date: Feb 19, 2004 Time: 10:58:33 AM
 * @author jdavis
 */
public class ServiceLocator extends BeanFactoryProxy implements BeanFactory
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(ServiceLocator.class.getName());

    /**
     * The system property name that will contain the name of the bootstrap Spring context resource.
     */
    public static final String BOOT_CONTEXT_PROPERTY_NAME = "org.yajul.framework.ServiceLocator.boot";

    /**
     * The system property name that will contain the bean ID of the ServiceLocator that is
     * declared in the bootstrap context.
     */
    public static final String BEAN_ID_PROPERTY_NAME = "org.yajul.framework.ServiceLocator.bean";

    /**
     * The system property name that will contain the name of the default application context resource.
     */
    public static final String CONTEXT_PROPERTY_NAME = "application.context";

    /**
     * The default application context resource name.
     */
    public static final String DEFAULT_CONTEXT_RESOURCE = "application-context.xml";

    /**
     * The default beanId of the ServiceLocator singleton in the boot context.
     */
    public static final String BEAN_ID = "serviceLocator";

    /**
     * The name of the boot context resource.
     */
    private String bootContext = null;

    /**
     * The name of the resource used to initialize the bean factory.
     */
    private String resource = null;

    /**
     * The name of the properties resource to load.
     */
    private String propertiesResource = null;

    /**
     * A reference to the bean factory that created this ServiceLocator.
     */
    private BeanFactoryReference bootFactoryReference;


    /**
     * A parent bean factory.
     */
    private BeanFactory parentBeanFactory;

    /**
     * The post processor.
     */
    private BeanFactoryPostProcessor beanFactoryPostProcessor;

    /**
     * Returns the current instance of the singleton.
     * @return ServiceLocator - The current service locator
     */
    public static ServiceLocator getInstance()
    {
        return getInstance(null);
    }

    /**
     * Returns the current instance of the singleton in the given boot context bean definition file.
     * @return ServiceLocator - The current service locator
     */
    public static ServiceLocator getInstance(String bootContext)
    {
        return getInstance(bootContext, null);
    }

    /**
     * Returns the Spring BeanFactoryLocator associated with the boot context.
     * @param bootContext
     * @return BeanFactoryLocator - The locator associated with the boot context.
     */
    public static BeanFactoryLocator getBeanFactoryLocator(String bootContext)
    {
        BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(bootContext);
        return locator;
    }

    /**
     * Returns a the specified service locator instance in the specified boot context.  The boot context is a Spring
     * bean definition resource (in the classpath) that must have the specified bean id in it.
     * @param bootContext The name of the 'boot' context resource.  A bean factory of bean factories.  If null,
     * the org.yajul.framework.ServiceLocator.boot system property will be used.   If both are null, the default
     * resource will be used (beanRefContext.xml).
     * @param beanId      The bean id (name) of a bean that is an instance of ServiceLocator or some subclass.
     * @return The ServiceLocator (or a subclass).
     */
    public static ServiceLocator getInstance(String bootContext, String beanId)
    {
        // Look ma, no statics!  Here we use the singleton bean factory locator to get
        // the instance of ServiceLocator.

        // The caller may wish to use a different ServiceLocator (a sub-class).  To do this, the user should
        // specify a different bootstrap context, with the appropriate bean declaration.
        if (bootContext == null)
            bootContext = System.getProperty(BOOT_CONTEXT_PROPERTY_NAME);
        // If the system property wasn't specified either, use the default in ContextSingletonBeanFactory.
        if (bootContext == null)
            bootContext = ContextSingletonBeanFactoryLocator.BEANS_REFS_XML_NAME;
        // Instantiate the locator!
        BeanFactoryLocator locator = getBeanFactoryLocator(bootContext);
        // If the beanId was not specified, try the system property.
        if (beanId == null)
            beanId = System.getProperty(BEAN_ID_PROPERTY_NAME);
        // If the system property was not specified either, use the default.
        BeanFactoryReference reference = locator.useBeanFactory(
                (beanId == null) ? BEAN_ID : beanId);
        ServiceLocator serviceLocator = (ServiceLocator) reference.getFactory();
        serviceLocator.setup(reference, bootContext);
        return serviceLocator;
    }

    private void setup(BeanFactoryReference reference, String bootContext)
    {
        // The service locator keeps a reference to itself, so that the BeanFactoryLocator will not unload it.
        this.bootFactoryReference = reference;
        this.bootContext = bootContext;
        log.info("setup() : bootContext = " + bootContext + " reference = " + reference);
    }

    /**
     * Empty (a.k.a. default) constructor.
     */
    public ServiceLocator()
    {
        resource = System.getProperty(CONTEXT_PROPERTY_NAME, DEFAULT_CONTEXT_RESOURCE);
    }

    /**
     * Returns the name of the bootstrap context used to load the service locator.
     * @return the name of the bootstrap context used to load the service locator.
     */
    public String getBootContext()
    {
        return bootContext;
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
     * Sets the name of the resource used to configure this service locator.
     * @param resource The name of an XML resource that will be used by this service locator.
     */
    public void setResourceName(String resource)
    {
        synchronized (this)
        {
            this.resource = resource;
        }
    }

    /**
     * Sets the name of the properties resource that will be used to post-process
     * the bean factory declaration resource.
     * @param propertiesResource The name of the properties resource used for post-processing.
     */
    public void setPropertiesResource(String propertiesResource)
    {
        synchronized (this)
        {
            this.propertiesResource = propertiesResource;
        }
    }

    /**
     * Sets the bean factory that will be used as the parent of the child bean factory.
     * @param parentBeanFactory
     */
    public void setParentBeanFactory(BeanFactory parentBeanFactory)
    {
        this.parentBeanFactory = parentBeanFactory;
    }

    /**
     * Sets the post processor that will be used after the bean definitions are loaded.
     * @param beanFactoryPostProcessor the post processor that will be used after the bean definitions are loaded.
     */
    public void setBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor)
    {
        this.beanFactoryPostProcessor = beanFactoryPostProcessor;
    }

    /**
     * Explicitly initializes the service locator from the specified resource. The resource must be in the class path,
     * and it must be a Spring framework XML 'application context' definition.
     * @param resource The name of the XML bean descriptor resource.
     */
    public void initialize(String resource)
    {
        initialize(resource, null);
    }

    /**
     * Explicitly initializes the service locator from the specified resource. The resource must be in the class path,
     * and it must be a Spring framework XML 'application context' definition.
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

    /**
     * Instantiate the delegate bean factory, and post-process the definitions.
     * @return BeanFactory - The delegate bean factory that will do all the work.
     */
    protected BeanFactory createDelegate()
    {
        DefaultListableBeanFactory beanFactory = loadBeanDefinitions();
        BeanFactoryPostProcessor factoryPostProcessor = getBeanFactoryPostProcessor();
        log.info("Post processing the bean factory...");
        factoryPostProcessor.postProcessBeanFactory(beanFactory);
        log.info("BeanFactory created and post-processed: " + beanFactory);
        return beanFactory;
    }

    private BeanFactoryPostProcessor getBeanFactoryPostProcessor()
    {
        BeanFactoryPostProcessor factoryPostProcessor = null;
        if (beanFactoryPostProcessor == null)
        {
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            Properties props = getProperties();
            if (props != null)
            {
                log.info("Using properties provided by sub-class (" + props.size() + " properties).");
                cfg.setProperties(props);
            }

            if (!StringUtil.isEmpty(propertiesResource))
            {
                log.info("Initializing properties from resource: " + propertiesResource);
                ClassPathResource classPathResource = new ClassPathResource(propertiesResource);
                if (!classPathResource.exists())
                    log.warn("Resource " + propertiesResource + " not found in the classpath!");
                cfg.setLocation(classPathResource);
            }
            factoryPostProcessor = cfg;
        }
        else
        {
            factoryPostProcessor = beanFactoryPostProcessor;
        }
        return factoryPostProcessor;
    }

    private DefaultListableBeanFactory loadBeanDefinitions()
    {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
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
                String path = url.toExternalForm();
                reader.loadBeanDefinitions(new UrlResource(path));
                count++;
                log.info("#" + count + " : Loaded bean definitions from " + path);
            }
        }
        catch (IOException e)
        {
            log.error(e,e);
            throw new DetailedRuntimeException("Unable to create bean factory from resource '" + resource + "' due to: " + e, e);
        }
        // If the class loader was not able to return a list of resources, try a single resource.
        if (count == 0)
        {
            reader.loadBeanDefinitions(new ClassPathResource(resource));
            log.info("Loaded bean definitions from unique resource " + resource);
        }
        return beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory()
    {
        StaticListableBeanFactory parent = new StaticListableBeanFactory();
        log.info("Including " + this + " as " + getBeanName());
        parent.addBean(getBeanName(),this);

        if (parentBeanFactory != null)
        {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)parentBeanFactory;
            cbf.setParentBeanFactory(parent);
        }
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(parent);
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
     * Sub classes may override this to provide configuration properties.
     * @return configuration properties
     */
    protected Properties getProperties()
    {
        return null;
    }

    /**
     * Releases the internal reference to the bean factory.
     */
    public void release()
    {
        if (bootFactoryReference != null)
        {
            log.info("release() : Releasing reference ...");
            bootFactoryReference.release();
            bootFactoryReference = null;
        }
    }

    protected void finalize() throws Throwable
    {
        destroy();
        release();
    }

}
