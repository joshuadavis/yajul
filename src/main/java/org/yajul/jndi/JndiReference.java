package org.yajul.jndi;

import com.google.inject.Provider;

import javax.naming.InitialContext;
import javax.jms.ConnectionFactory;

/**
 * Cached lazy lookup, typesafe JNDI reference.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 3:05:25 PM
 */
public class JndiReference<T> extends JndiProvider<T> implements Provider<T> {
    /**
     * Cached object reference.
     */
    private T object;

    public JndiReference(JndiLookup jndiLookup, Class<? extends T> clazz, String name) {
        super(jndiLookup, clazz, name);
    }

    public JndiReference(InitialContext ic, Class<? extends T> clazz, String name) {
        super(ic, clazz, name);
    }

    public JndiReference(T object) {
        super();
        this.object = object;
    }

    @Override
    public final T get() {
        synchronized (this) {
            if (object == null)
                object = create();
        }
        return object;
    }

    /**
     * Sub-classes can override this to do any initialization after looking up the object.
     *
     * @return the newly looked-up object
     */
    protected T create() {
        return super.get();
    }
}
