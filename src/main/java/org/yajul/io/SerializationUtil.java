package org.yajul.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * General serialization helper methods.
 * <br>User: Joshua Davis
 * Date: Nov 23, 2007
 * Time: 6:08:06 PM
 */
public class SerializationUtil {
    private final static Logger log = LoggerFactory.getLogger(SerializationUtil.class);
    private static final int DEFAULT_INITIAL_SIZE = 512;

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

    public static byte[] toByteArray(Serializable obj, int initialSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(initialSize);
        serialize(obj, baos);
        return baos.toByteArray();
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

    public static Object fromCompressedByteArray(byte[] objectData, int bufferSize) throws ClassNotFoundException, IOException {
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
     * Returns the size of the object if it was serialize all by itself.
     *
     * @param object the serializable object
     * @return the number of bytes
     * @throws IOException if something goes wrong
     */
    public static int sizeOf(Serializable object) throws IOException {
        ByteCountingOutputStream counter = new ByteCountingOutputStream(
                new NullOutputStream());
        serialize(object, counter);
        return counter.getByteCount();
    }

    /**
     * Counts the number of objects of each class inside the serializable object, and also
     * counts the number of bytes that the object would have in serialized form.
     * @param obj the object
     * @return the statistics.
     */
    public static Stats getStats(Serializable obj) {
        try {
            ByteCountingOutputStream counter = new ByteCountingOutputStream(new NullOutputStream());
            CountingObjectOutputStream oos = new CountingObjectOutputStream(counter);
            oos.writeObject(obj);
            return new Stats(counter.getByteCount(), oos);
        }
        catch (IOException e) {
            log.warn("Unable to compute size of " + obj.getClass().getSimpleName() + " due to : " + e);
            return null;
        }
    }

    /**
     * The total size, and the number of instances of each class in an object.
     */
    public static class Stats {
        private int totalSize;
        private CountingObjectOutputStream oos;

        public Stats(int byteCount, CountingObjectOutputStream oos) {
            this.totalSize = byteCount;
            this.oos = oos;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public Collection<CountingObjectOutputStream.Counter> getCounters() {
            return oos.getCounters();
        }

        public CountingObjectOutputStream.Counter getCounter(String name) {
            return oos.getCounter(name);
        }
    }

    /**
     * Automatically unwrap the object if it implements SerializableWrapper.
     *
     * @param obj The object, or a SerializableWrapper around an object.
     * @return The wrapped object if the argument implements SerializableWrapper, or the argument
     *         object if it doesn't implement SerializableWrapper.
     * @throws java.io.IOException    if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    public static Serializable autoUnwrap(Serializable obj) throws IOException, ClassNotFoundException {
        if (obj instanceof SerializableWrapper) {
            SerializableWrapper wrapper = (SerializableWrapper) obj;
            return wrapper.unwrap();
        } else
            return obj;

    }
}
