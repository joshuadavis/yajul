/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 12, 2002
 * Time: 5:18:52 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jndi.simple;

import javax.naming.Binding;
import java.util.Enumeration;

// Class for enumerating bindings

class FlatBindings extends AbstractNamingEnumeration
{
    FlatBindings(SimpleCtx ctx, Enumeration names)
    {
        super(ctx, names);
    }

    public Object nextElement()
    {
        String name = (String) names.nextElement();
        return new Binding(name, ctx.getBindingsInternal().get(name));
    }
}
