package org.yajul.micro;

import com.google.inject.Provider;

/**
 * A Guice provider that caches the reference, creating the object only once.
 * Useful for lazy loading.  NOTE: Inject the Provider and not the object
 * it provides, otherwise this is useless.
 * <br>
 * User: josh
 * Date: Dec 10, 2009
 * Time: 11:31:14 AM
 */
public abstract class AbstractCachingProvider<T> implements Provider<T> {
    private T object;

    public final T get() {
        if (object == null)
            object = create();
        return object;
    }

    protected abstract T create();
}
