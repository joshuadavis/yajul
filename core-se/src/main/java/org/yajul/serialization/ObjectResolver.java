package org.yajul.serialization;

/**
 * Resolves objects during deserialization.
 * <br>
 * User: josh
 * Date: Sep 9, 2009
 * Time: 12:06:47 PM
 */
public interface ObjectResolver {
    /**
     * Replace the deserialized object with a different object.
     * @param object the deserialized object
     * @return another object, or the same object
     */
    Object resolveObject(Object object);
}
