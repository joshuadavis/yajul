/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Nov 4, 2002
 * Time: 8:26:17 AM
 */
package org.yajul.io;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Provides utility functions for serializaing objects.
 */
public class ObjectStreamHelper
{
    /**
     * Turns an object into an array of bytes using Java Serialization Format
     * @param obj The object to serialize.
     * @return byte[] - The array of bytes containing the serialized object.
     * @throws IOException If there was a problem serializing.
     */
    public static final byte[] serialize(Object obj) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * Reconstitutes an object from an array of bytes.
     * @param bytes The array of bytes.
     * @return Object - The object.
     * @throws IOException If there was a problem deserializing
     * @throws ClassNotFoundException If there was a class mentioned that does
     * not exist in the class loader for the calling thread.
     */
    public static final Object deserialize(byte[] bytes) throws IOException,
        ClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
