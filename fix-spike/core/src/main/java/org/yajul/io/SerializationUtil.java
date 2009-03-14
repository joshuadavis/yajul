package org.yajul.io;

import java.io.*;

/**
 * General serialization helper methods.
 * <br>User: Joshua Davis
 * Date: Nov 23, 2007
 * Time: 6:08:06 PM
 */
public class SerializationUtil {
    /**
     * Deep clones an object using serialization.
     * @param object  the object to clone
     * @return the cloned object
     * @throws java.io.IOException if something goes wrong
     * @throws ClassNotFoundException if a class cannot be found
     */
    public static Object clone(Serializable object) throws IOException, ClassNotFoundException {
        return fromByteArray(toByteArray(object));
    }

    /**
     * Serialize an object to an output stream.
     * @param obj  the object to serialize to bytes, may be null
     * @param outputStream  the stream to write to, must not be null
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
     * @param obj  the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     * @throws java.io.IOException if something goes wrong
     */
    public static byte[] toByteArray(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    /**
     * Deserializes an object from the specified stream.
     * @param inputStream  the serialized object input stream, must not be null
     * @return the deserialized object
     * @throws java.io.IOException if something goes wrong
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
     * Deserializes a single object from an array of bytes.
     *
     * @param objectData  the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     * @throws java.io.IOException if something goes wrong
     * @throws ClassNotFoundException if a class cannot be found
     */
    public static Object fromByteArray(byte[] objectData) throws ClassNotFoundException, IOException {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }

}
