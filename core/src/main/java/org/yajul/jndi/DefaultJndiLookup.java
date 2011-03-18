package org.yajul.jndi;

import com.google.inject.Provider;

import javax.naming.InitialContext;

/**
 * Default lookup implementation.  Nothing special.
 * <br>
 * User: josh
 * Date: Sep 11, 2009
 * Time: 10:07:52 AM
 */
public class DefaultJndiLookup implements JndiLookup
{
    private Provider<InitialContext> icp;

    public DefaultJndiLookup(Provider<InitialContext> icp)
    {
        this.icp = icp;
    }

    public DefaultJndiLookup(InitialContext ic)
    {
        this(new SimpleInitialContextProvider(ic));
    }

    public <T> T lookup(Class<T> clazz, String name)
    {
        return JndiHelper.lookup(icp.get(),clazz,name);
    }
}
