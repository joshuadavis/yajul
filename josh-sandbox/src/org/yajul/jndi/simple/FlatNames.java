/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 12, 2002
 * Time: 5:19:30 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jndi.simple;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Enumeration;

// Class for enumerating name/class pairs
class FlatNames extends AbstractNamingEnumeration
{

    FlatNames(SimpleCtx ctx, Enumeration names)
    {
        super(ctx,names);
    }

    public Object nextElement()
    {
        String name = (String) names.nextElement();
        String className = ctx.getBindingsInternal().get(name).getClass().getName();
        return new NameClassPair(name, className);
    }

}
