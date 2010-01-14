package org.yajul.jndi;

import com.google.inject.Provider;
import org.yajul.util.StringUtil;

import javax.naming.InitialContext;

/**
 * Looks up something in an EAR context, e.g. an EJB.
 * <br>
 * User: josh
 * Date: Sep 11, 2009
 * Time: 10:12:05 AM
 */
public class EarJndiLookup extends DefaultJndiLookup
{
    private String earName;

    public EarJndiLookup(Provider<InitialContext> icp,String earName)
    {
        super(icp);
        this.earName = earName;
    }

    public EarJndiLookup(InitialContext ic, String earName)
    {
        super(ic);
        this.earName = earName;
    }

    @Override
    public <T> T lookup(Class<T> clazz, String name)
    {
        if (!StringUtil.isEmpty(earName) && !name.startsWith(earName))
            name = earName + "/" + name;
        return super.lookup(clazz, name);
    }
}
