package org.yajul.jndi;

import org.yajul.util.CachedObjectProvider;
import org.yajul.util.ObjectProvider;

import javax.naming.InitialContext;

/**
 * Caches the result of a JNDI lookup.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 12:11 PM
 */
public class CachedJndiObjectProvider<T> extends CachedObjectProvider<T> {
    public CachedJndiObjectProvider(ObjectProvider<T> delegate) {
        super(delegate);
    }

    public CachedJndiObjectProvider(JndiLookup jndiLookup, Class<? extends T> clazz, String name) {
        super(new JndiObjectProvider<T>(jndiLookup, clazz, name));
    }

    public CachedJndiObjectProvider(InitialContext ic, Class<? extends T> clazz, String name) {
        super(new JndiObjectProvider<T>(ic, clazz, name));
    }
}
