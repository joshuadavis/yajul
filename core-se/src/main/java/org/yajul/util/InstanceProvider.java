package org.yajul.util;

/**
 * Provides the same instance every time.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 11:57 AM
 */
public class InstanceProvider<T> implements ObjectProvider<T> {
    private final T instance;

    /**
     * @param instance the instance that this provider will return
     */
    public InstanceProvider(T instance) {
        this.instance = instance;
    }

    public T getObject() {
        return instance;
    }
}