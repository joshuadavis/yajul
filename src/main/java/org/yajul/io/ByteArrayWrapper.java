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

    public T unwrap() throws IOException, ClassNotFoundException {
        if (obj == null) {
            obj = byteArrayToObject(bytes);
            bytes = null; // Don't need the bytes now.
        }
        return obj;
    }

    public byte[] wrap() throws IOException {
        if (bytes == null) {
            bytes = objectToByteArray(obj);
            obj = null; // Don't need the object now.
        }
        return bytes;
    }

    protected T byteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        return (T) SerializationUtil.fromByteArray(bytes);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(wrap());
    }

    private byte[] objectToByteArray(T obj) throws IOException {
        return SerializationUtil.toByteArray(obj);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        bytes = (byte[]) in.readObject();
    }
}
