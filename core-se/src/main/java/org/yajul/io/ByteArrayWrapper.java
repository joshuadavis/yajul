package org.yajul.io;

import java.io.*;

/**
 * Stores the wrapped object as a byte array when it is serialized, then lazily
 * unwraps it when asked.  Usages include wrapping the root object of a JMS ObjectMessage, where
 * the object graph might be large.  Subclasses can override byteArrayToObject() and objectToByteArray()
 * to customize the serialization behavior.
 * <br>
 * User: josh
 * Date: Sep 10, 2009
 * Time: 6:19:18 PM
 */
public class ByteArrayWrapper<T extends Serializable>
        implements SerializableWrapper<T>, Externalizable {
    private byte[] bytes;
    private T obj;

    public ByteArrayWrapper(T obj) {
        if (obj == null)
            throw new IllegalArgumentException("Wrapped object cannot be null!");
        this.obj = obj;
    }

    public ByteArrayWrapper() {
    }

    public final T unwrap() throws IOException, ClassNotFoundException {
        if (obj == null) {
            obj = byteArrayToObject(bytes);
            bytes = null; // Don't need the bytes now.
        }
        return obj;
    }

    public final byte[] wrap() throws IOException {
        if (bytes == null) {
            bytes = objectToByteArray(obj);
            obj = null; // Don't need the object now.
        }
        return bytes;
    }

    /**
     * Turns the byte array back into an object.  Subclasses can override this to customize
     * the behavior.
     * @param bytes the bytes to convert
     * @return the object
     * @throws IOException if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    protected T byteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        return (T) SerializationUtil.fromByteArray(bytes);
    }

    public final void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(wrap());
    }

    public final boolean isWrapped() {
        return obj == null && bytes != null;
    }

    public final boolean isUnwrapped() {
        return !isWrapped();
    }
    
    /**
     * Turns the object into a byte array.  Subclasses can override this to customize the behavior.
     * @param obj the object
     * @return the byte array
     * @throws IOException if something goes wrong.
     */
    protected byte[] objectToByteArray(T obj) throws IOException {
        return SerializationUtil.toByteArray(obj);
    }

    public final void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        bytes = (byte[]) in.readObject();
        obj = null;
    }
}
