// $Id$
package org.yajul.framework;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * A proxy that delegates to a BeanFactory implementation that is created by a sub-class.
 * @author josh Mar 28, 2004 8:31:23 PM
 */
abstract class BeanFactoryProxy implements BeanFactory, BeanNameAware, BeanFactoryAware
{
    private static Logger log = Logger.getLogger(BeanFactoryProxy.class);

    /**
     * The delegate that will actually do the work.
     */
    private BeanFactory delegate;
    /**
     * The name of this instance (if this instance is a bean).
     */
    private String beanName;

    /**
     * The factory that created this factory.
     */
    private BeanFactory metaFactory;

    /**
     * Instantiate the delegate bean factory.
     * @return BeanFactory - The delegate bean factory that will do all the work.
     */
    protected abstract BeanFactory createDelegate();

    /**
     * Returns the delegate bean factory, which will be created on demand.
     * @return BeanFactory - The delegate bean factory.
     */
    protected final BeanFactory getDelegate()
    {
        synchronized (this)
        {
            if (!delegateExists())
            {
                log.info("Creating delegate bean factory...");
                delegate = createDelegate();
            }
            return delegate;
        }
    }

    /**
     * Returns true if the delegate has been created.
     * @return true if the delegate has been created.
     */
    protected final boolean delegateExists()
    {
        return delegate != null;
    }

    /**
     * Cleans up the bean factory and all associated resources.
     */
    public void destroy()
    {
        synchronized (this)
        {
            if ((delegate != null) && (delegate instanceof ConfigurableBeanFactory))
            {
                ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) delegate;
                log.info("Destroying singletons...");
                configurableBeanFactory.destroySingletons();
            }
            delegate = null;
        }
    }

    /**
     * Returns the name of this proxy if the proxy is a bean inside a factory.
     * @return the name of this proxy if the proxy is a bean inside a factory.
     */
    public final String getBeanName()
    {
        return beanName;
    }

    /**
     * Returns the factory that created this factory proxy.
     * @return the factory that created this factory proxy.
     */
    public final BeanFactory getMetaBeanFactory()
    {
        return metaFactory;
    }

    // -- BeanFactory implementation --

    public Object getBean(String name) throws BeansException
    {
        return getDelegate().getBean(name);
    }

    public Object getBean(String name, Class requiredType) throws BeansException
    {
        return getDelegate().getBean(name, requiredType);
    }

    public boolean containsBean(String name)
    {
        return getDelegate().containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return getDelegate().isSingleton(name);
    }

    public String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return getDelegate().getAliases(name);
    }

    // -- BeanNameAware implementation --
    /**
     * Set the name of the bean in the bean factory that created this bean.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * @param name the name of the bean in the factory
     */
    public final void setBeanName(String name)
    {
        beanName = name;
    }

    // -- BeanFactoryAware implementation --
    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * @param beanFactory owning BeanFactory (may not be null).
     * The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeanInitializationException
     */
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.metaFactory = beanFactory;
    }
}
