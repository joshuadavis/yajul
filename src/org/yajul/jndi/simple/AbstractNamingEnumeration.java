/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 12, 2002
 * Time: 5:41:20 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jndi.simple;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Enumeration;

abstract class AbstractNamingEnumeration implements NamingEnumeration
{
    protected Enumeration names;
    protected SimpleCtx ctx;

    AbstractNamingEnumeration(SimpleCtx ctx, Enumeration names)
    {
        this.names = names;
        this.ctx = ctx;
    }

    public boolean hasMoreElements()
    {
        return names.hasMoreElements();
    }

    public boolean hasMore() throws NamingException
    {
        return hasMoreElements();
    }

    public abstract Object nextElement();

    public Object next() throws NamingException
    {
        return nextElement();
    }

    public void close()
    {
        ctx = null;
        names = null;
    }
}
