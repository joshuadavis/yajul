package org.yajul.jndi;

import javax.naming.InitialContext;
import javax.jms.ConnectionFactory;

/**
 * Cached lazy lookup, typesafe JNDI reference.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 3:05:25 PM
 */
public class JndiReference<T> extends JndiProvider<T> {
    private T object;

    public JndiReference(JndiLookup jndiLookup, Class<? extends T> clazz, String name)
    {
        super(jndiLookup, clazz, name);
    }

    public JndiReference(InitialContext ic, Class<? extends T> clazz, String name)
    {
        super(ic, clazz, name);
    }

    public JndiReference(T object)
    {
        super();
        this.object = object;
    }

    @Override
    public T get()
    {
        if (object == null)
            object = super.get();
        return object;
    }

    public T getObject() {
        return get();
    }
}
