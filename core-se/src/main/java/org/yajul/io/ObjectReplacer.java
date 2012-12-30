package org.yajul.io;

/**
 * Callback interface for replacing objects during serialization.
 * <br>
 * User: josh
 * Date: Sep 9, 2009
 * Time: 12:00:52 PM
 */
public interface ObjectReplacer {
    /**
     * Replace an object being written with another object during serialization.
     * @param obj the object being serialized
     * @return the object's replacement (or the same object).
     */
    Object replaceObject(Object obj);
}
