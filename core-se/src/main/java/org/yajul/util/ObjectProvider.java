package org.yajul.util;

/**
 * Provides an object of a given type.
 * <br>
 * User: josh
 * Date: 6/28/11
 * Time: 11:32 AM
 */
public interface ObjectProvider<T> {
    /**
     * Provides an object of a given type.
     * @return the object
     */
    T getObject();
}
