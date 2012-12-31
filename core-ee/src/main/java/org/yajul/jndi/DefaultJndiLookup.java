package org.yajul.jndi;


import org.yajul.util.InstanceProvider;
import org.yajul.util.ObjectProvider;

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
    private ObjectProvider<InitialContext> icp;

    public DefaultJndiLookup() {
        this(new DefaultInitialContextProvider());
    }

    public DefaultJndiLookup(ObjectProvider<InitialContext> icp)
    {
        this.icp = icp;
    }

    public DefaultJndiLookup(InitialContext ic)
    {
        this(new InstanceProvider<InitialContext>(ic));
    }

    public <T> T lookup(Class<T> clazz, String name)
    {
        return JndiHelper.lookup(icp.getObject(),clazz,name);
    }
}
