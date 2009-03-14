package org.yajul.jndi;

import javax.naming.InitialContext;

/**
 * Lazy lookup, typesafe JNDI reference.
 * <br>
 * User: josh
 * Date: Mar 3, 2009
 * Time: 3:05:25 PM
 */
public class JndiReference<T> {
    private InitialContext ic;
    private String name;
    private T object;

    public JndiReference(InitialContext ic, String name) {
        this.ic = ic;
        this.name = name;
    }

    public JndiReference(T object) {
        this.object = object;
    }

    public T getObject() {
        if (object == null)
            //noinspection unchecked
            object = (T)JndiHelper.lookup(ic,name);
        return object;
    }
}
