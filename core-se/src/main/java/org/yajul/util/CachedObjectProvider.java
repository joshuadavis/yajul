package org.yajul.util;

/**
 * Caches an object.  The delegate ObjectProvider is only called once.
 * Thread safe.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 11:53 AM
 */
public class CachedObjectProvider<T> implements ObjectProvider<T> {

    private T cached;
    private ObjectProvider<T> delegate;

    public CachedObjectProvider(ObjectProvider<T> delegate) {
        this.delegate = delegate;
    }

    public T getObject() {
        synchronized (this) {
            if (cached == null)
                cached = create();
            return cached;
        }
    }

    /**
     * Sub-classes can override this to do any initialization after looking up the object.
     *
     * @return the newly looked-up object
     */
    protected T create() {
        return delegate.getObject();
    }
}
