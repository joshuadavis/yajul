/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 11:13:20 AM
 */
package org.yajul.io;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Provides utility methods that are usefull when dealing with Java Object
 * Serialization.
 * @author Joshua Davis
 */
public class ObjectStreamHelper
{
    /**
     * Serializes an object into an array of bytes.
     * @param obj       The object to be serialized.
     * @return  byte[]  The array of bytes that is the object in serialized
     * form.
     * @throws IOException  if there is a problem serializing the object.
     */
    public static final byte[] serialize(Object obj)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * De-serializes (reads) an object from a byte array in Java Serialization
     * Format.
     * @param byte[]    The byte array that contains the object.
     * @return Object   The object, deserialized from the byte array.
     * @throws IOException if there was a problem deserializing the object.
     * @throws ClassNotFoundException An object in the stream references a
     * class that is not in the current classpath or class loader.
     */
    public static final Object deserialize(byte[] bytes)
        throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
