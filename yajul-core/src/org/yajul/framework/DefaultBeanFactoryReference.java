// $Id$
package org.yajul.framework;

import org.apache.log4j.Logger;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.DefaultLocatorFactory;

/**
 * A bean factory reference that automatically releases when it is finalized.
 * @author josh Mar 31, 2004 7:33:52 AM
 */
public class DefaultBeanFactoryReference implements BeanFactoryReference
{
    /**
     * A logger for this class.
     */
   private static final Logger log = Logger.getLogger(DefaultBeanFactoryReference.class);

    public BeanFactoryReference delegate;

    public DefaultBeanFactoryReference(String factoryName)
    {
        setBeanFactoryName(factoryName);
    }

    public void setBeanFactoryName(String factoryName)
    {
        release();
        BeanFactoryLocator factoryLocator = DefaultLocatorFactory.getInstance();
        delegate = factoryLocator.useBeanFactory(factoryName);
    }

    public BeanFactory getFactory()
    {
        if (delegate == null)
            throw new IllegalStateException("This bean factory reference has already been released!");
        return delegate.getFactory();
    }

    public void release() throws FatalBeanException
    {
        if (log.isDebugEnabled())
            log.debug("release()");
        if (delegate != null)
            delegate.release();
        delegate = null;
    }

    protected void finalize() throws Throwable
    {
        if (log.isDebugEnabled())
            log.debug("finalize() : Releasing...");
        release();
    }
}
