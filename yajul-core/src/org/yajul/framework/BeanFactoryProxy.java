// $Id$
package org.yajul.framework;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * A proxy that implements BeanFactory by delegating to a BeanFactory implementation that is created by the sub-class.
 *
 * @author josh Mar 28, 2004 8:31:23 PM
 */
abstract class BeanFactoryProxy implements BeanFactory
{
    private static Logger log = Logger.getLogger(BeanFactoryProxy.class);

    private BeanFactory delegate;

    protected abstract BeanFactory createDelegate();

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

    protected BeanFactory getDelegate()
    {
        synchronized (this)
        {
            if (delegate == null)
            {
                log.info("Creating delegate bean factory...");
                delegate = createDelegate();
            }
            return delegate;
        }
    }

    protected boolean delegateExists()
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

}
