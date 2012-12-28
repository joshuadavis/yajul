package org.yajul.io;

import java.io.Serializable;
import java.io.IOException;

/**
 * A wrapper around serializable objects, used to modify the serialized form
 * of an object.
 * <br>
 * User: josh
 * Date: Sep 10, 2009
 * Time: 6:16:16 PM
 */
public interface SerializableWrapper<T extends Serializable> extends Serializable {
    /**
     * @return the inner, unwrapped object
     * @throws java.io.IOException if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    T unwrap() throws IOException, ClassNotFoundException;
}
