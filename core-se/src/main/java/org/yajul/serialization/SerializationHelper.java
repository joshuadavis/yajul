package org.yajul.serialization;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Generic serialization helper methods.
 * <br>
 * User: josh
 * Date: 1/2/13
 * Time: 3:11 PM
 */
public class SerializationHelper {

    private static int DEFAULT_INITIAL_SIZE = 128;

    /**
     * Serialize an object to an output stream.
     *
     * @param obj          the object to serialize to bytes, may be null
     * @param outputStream the stream to write to, must not be null
     * @throws java.io.IOException if something goes wrong
     */
    public static void serialize(Serializable obj, OutputStream outputStream) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    /**
     * Serializes an object to a byte array.
     *
     * @param obj the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     * @throws java.io.IOException if something goes wrong
     */
    public static byte[] toByteArray(Serializable obj) throws IOException {
        return toByteArray(obj, DEFAULT_INITIAL_SIZE);
    }

    /**
     * Serializes an object to a byte array.
     *
     * @param obj the object to serialize to bytes
     * @param initialSize the initial buffer size
     * @return a byte[] with the converted Serializable
     * @throws java.io.IOException if something goes wrong
     */
    public static byte[] toByteArray(Serializable obj, int initialSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(initialSize);
        serialize(obj, baos);
        return baos.toByteArray();
    }


    /**
     * Deserializes an object from the specified stream.
     *
     * @param inputStream the serialized object input stream, must not be null
     * @return the deserialized object
     * @throws java.io.IOException    if something goes wrong
     * @throws ClassNotFoundException if a class cannot be found
     */
    public static Object deserialize(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new ObjectInputStream(inputStream);
            return in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Clones the object using serialization.
     * @param t the object to clone
     * @param <T> the type of the object
     * @return a clone of the object
     * @throws IOException  if something goes wrong.
     * @throws ClassNotFoundException
     */
    public static <T extends Serializable> T serialClone(T t) throws IOException, ClassNotFoundException {
        byte[] b = toByteArray(t);
        return (T)fromByteArray(b);
    }

    /**
     * Deserializes a single object from an array of bytes.
     *
     * @param objectData the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     * @throws java.io.IOException      if something goes wrong
     * @throws ClassNotFoundException   if a class cannot be found
     */
    public static Object fromByteArray(byte[] objectData) throws ClassNotFoundException, IOException {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }

    /**
     * Deserializes a single object from an array of bytes that were compressed with an Inflater.
     *
     * @param objectData the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     * @throws java.io.IOException      if something goes wrong
     * @throws ClassNotFoundException   if a class cannot be found
     */
    public static Object fromCompressedByteArray(byte[] objectData, int bufferSize)
            throws ClassNotFoundException, IOException {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        Inflater inf = new Inflater();
        InflaterInputStream iis = new InflaterInputStream(bais,inf,bufferSize);
        Object o;
        try {
            o = deserialize(iis);
        } finally {
            inf.end();
        }
        return o;
    }

    /**
     * Deep clones an object using serialization.
     *
     * @param object the object to clone
     * @return the cloned object
     * @throws java.io.IOException    if something goes wrong
     * @throws ClassNotFoundException if a class cannot be found
     */
    public static <T extends Serializable> T clone(T object) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        return (T) fromByteArray(toByteArray(object));
    }

    public static byte[] toCompressedByteArray(Serializable obj, int initialSize, int level, int bufferSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(initialSize);
        Deflater def = new Deflater(level);
        DeflaterOutputStream dos = new DeflaterOutputStream(baos,def,bufferSize);
        try {
            serialize(obj, dos);
        } finally {
            def.end();
        }
        return baos.toByteArray();
    }
}
