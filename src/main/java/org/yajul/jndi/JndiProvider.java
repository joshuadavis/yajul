package org.yajul.jndi;

import com.google.inject.Provider;

import javax.naming.InitialContext;

/**
 * A Guice provider that looks things up in JNDI using the JndiLookup impelementation given.
 * <br>
 * User: josh
 * Date: Sep 4, 2009
 * Time: 1:11:14 PM
 */
public class JndiProvider<T> implements Provider<T>
{
    private JndiLookup jndiLookup;
    private Class<? extends T> clazz;
    private String name;

    public JndiProvider(JndiLookup jndiLookup, Class<? extends T> clazz, String name)
    {
        this.jndiLookup = jndiLookup;
        this.clazz = clazz;
        this.name = name;
    }

    public JndiProvider(InitialContext ic, Class<? extends T> clazz, String name)
    {
        this(new DefaultJndiLookup(ic),clazz,name);    
    }

    public JndiProvider()
    {
    }

    public T get()
    {
        if (jndiLookup != null)
            return jndiLookup.lookup(clazz, name);
        else
            return null;
    }
}
