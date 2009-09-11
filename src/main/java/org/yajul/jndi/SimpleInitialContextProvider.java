package org.yajul.jndi;

import javax.naming.InitialContext;

/**
 * Use this when you already have an InitialContext.
 * <br>
 * User: josh
 * Date: Sep 11, 2009
 * Time: 10:43:41 AM
 */
public class SimpleInitialContextProvider implements InitialContextProvider
{
    private InitialContext ic;

    public SimpleInitialContextProvider(InitialContext ic)
    {
        this.ic = ic;
    }

    public InitialContext getInitialContext()
    {
        return ic;
    }
}
