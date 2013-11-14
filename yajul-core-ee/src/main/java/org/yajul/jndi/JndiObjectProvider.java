package org.yajul.jndi;

import org.yajul.util.ObjectProvider;

import javax.naming.InitialContext;

/**
 * Provides objects from the JNDI tree using a JndiLookup delegate.
 * This looks up the object every time.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 12:08 PM
 */
public class JndiObjectProvider<T> implements ObjectProvider<T> {
    private JndiLookup jndiLookup;
    private Class<? extends T> clazz;
    private String name;

    public JndiObjectProvider(JndiLookup jndiLookup, Class<? extends T> clazz, String name)
    {
        this.jndiLookup = jndiLookup;
        this.clazz = clazz;
        this.name = name;
    }

    public JndiObjectProvider(InitialContext ic, Class<? extends T> clazz, String name)
    {
        this(new DefaultJndiLookup(ic),clazz,name);
    }

    public T getObject() {
        if (jndiLookup != null)
            return jndiLookup.lookup(clazz, name);
        else
            return null;
    }
}
