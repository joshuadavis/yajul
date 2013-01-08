package org.yajul.serialization;

import org.yajul.collections.CollectionUtil;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static org.yajul.juli.LogHelper.unexpected;

/**
 * Helper methods for Externalizable.
 * <ul>
 * <li>Nullable boxed types - readObject() / writeObject() are not very efficient.</li>
 * </ul>
 * <br>
 * User: josh
 * Date: Sep 9, 2009
 * Time: 12:52:38 PM
 */
public class ExternalizableHelper {

    private final static Logger log = Logger.getLogger(ExternalizableHelper.class.getName());
    private static final int NULL_VALUE = -1;
    private static final int NOT_NULL_VALUE = 1;

    public static void writeNullableString(ObjectOutput out, String value) throws IOException {
        if (value == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(value.length());
            out.writeBytes(value);
        }
    }

    public static String readNullableString(ObjectInput in) throws IOException {
        int len = in.readInt();
        if (len < 0)
            return null;
        else {
            byte[] bytes = new byte[len];
            if (len > 0) {
                int r = in.read(bytes);
                if (r != len)
                    throw new IOException("Unexpected end of stream!  Expected " + len + " bytes, read " + r);
            }
            return new String(bytes);
        }
    }

    public static void writeNullableLong(ObjectOutput out, Long aLong) throws IOException {
        if (aLong == null)
            writeNull(out);
        else {
            writeNotNull(out);
            long v = aLong;
            out.writeLong(v);
        }
    }

    public static Long readNullableLong(ObjectInput in) throws IOException {
        boolean isNull = readIsNull(in);
        if (isNull)
            return null;
        else {
            long v = in.readLong();
            return v;
        }
    }

    public static void writeNullableInteger(ObjectOutput out, Integer anInteger) throws IOException {
        if (anInteger == null)
            writeNull(out);
        else {
            writeNotNull(out);
            out.writeInt(anInteger);
        }
    }

    public static Integer readNullableInteger(ObjectInput in) throws IOException {
        boolean isNull = readIsNull(in);
        if (isNull)
            return null;
        else
            return in.readInt();
    }

    public static void writeNullableEnum(ObjectOutput out, Enum<?> value) throws IOException {
        if (value == null)
            out.writeInt(NULL_VALUE);
        else
            out.writeInt(value.ordinal());
    }

    public static <T> T readNullableEnum(ObjectInput in, T[] values) throws IOException {
        int ord = in.readInt();
        if (ord == NULL_VALUE)
            return null;
        else
            return values[ord];
    }

    public static void writeNullableEnumByte(ObjectOutput out, Enum<?> value) throws IOException {
        if (value == null)
            out.writeByte(NULL_VALUE);
        else
            writeEnumByte(out, value);
    }

    public static void writeEnumByte(ObjectOutput out, Enum<?> value) throws IOException {
        assert value != null;
        out.writeByte(value.ordinal());
    }


    public static <T> T readNullableEnumByte(ObjectInput in, T[] values) throws IOException {
        return readEnumByte(in, values);
    }

    public static <T> T readEnumByte(ObjectInput in, T[] values) throws IOException {
        int ord = in.readByte();
        if (ord == NULL_VALUE)
            return null;
        else
            return values[ord];
    }

    /**
     * Writes the optional externalizable object directly. NOTE: Use ObjectOutput.writeObject() if
     * you expect the object to have multiple references!
     *
     * @param out the output stream
     * @param ex  the object to write
     * @throws IOException if something goes wrong
     */
    public static void writeNullable(ObjectOutput out, Externalizable ex) throws IOException {
        if (ex == null)
            writeNull(out);
        else {
            writeNotNull(out);
            ex.writeExternal(out);
        }
    }

    /**
     * Reads in an optional 'child' externalizable.  NOTE: Use ObjectInput.readObject() if you
     * expect the object to have multiple references!
     *
     * @param in      the input stream
     * @param exClass the object class
     * @param <T>     parameterized object class
     * @return the object, or null
     * @throws IOException            if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    public static <T extends Externalizable> T readNullable(ObjectInput in, Class<T> exClass) throws IOException, ClassNotFoundException {
        boolean isNull = readIsNull(in);
        if (isNull)
            return null;
        else {
            T ex;
            try {
                ex = exClass.newInstance();
            } catch (InstantiationException e) {
                unexpected(log, e);
                throw new IOException("Unable to instantiate " + exClass.getName() + " due to : " + e.getMessage());
            } catch (IllegalAccessException e) {
                unexpected(log, e);
                throw new IOException("Unable to instantiate " + exClass.getName() + " due to : " + e.getMessage());
            }
            ex.readExternal(in);
            return ex;
        }
    }

    private static void writeNotNull(ObjectOutput out) throws IOException {
        out.writeByte(NOT_NULL_VALUE);
    }

    private static void writeNull(ObjectOutput out) throws IOException {
        out.writeByte(NULL_VALUE);
    }

    private static boolean readIsNull(ObjectInput in) throws IOException {
        return in.readByte() == NULL_VALUE;
    }

    public static <T> T readObject(ObjectInput in, Class<T> clazz) throws IOException, ClassNotFoundException {
        Object o = in.readObject();
        return o == null ? null : clazz.cast(o);
    }

    public static void writeList(ObjectOutput out, Collection<? extends Object> collection) throws IOException {
        if (collection == null) {
            out.writeInt(NULL_VALUE);
            return;
        } else {
            out.writeInt(collection.size());
            for (Object obj : collection) {
                out.writeObject(obj);
            }
        }
    }

    public static <T> List<T> readArrayList(ObjectInput in, Class<T> clazz) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        if (size == NULL_VALUE)
            return null;
        else {
            List<T> list = CollectionUtil.newArrayList(size);
            for (int i = 0; i < size; i++) {
                T e = readObject(in, clazz);
                list.add(e);
            }
            return list;
        }
    }


    /**
     * Returns an int representing the state of each object: 0 for null, 1 for not null.
     *
     * @param objects objects that may or may not be null
     * @return an int contining 'null bits'
     */
    public static int getNullBits(Object... objects) {
        if (objects == null || objects.length == 0)
            return 0;
        if (objects.length > 30)
            throw new IllegalArgumentException("Too many nullable objects.");
        int bits = 0;
        int bitflag = 1;
        for (Object object : objects) {
            if (object != null)
                bits |= bitflag;
            bitflag <<= 1;
        }
        return bits;
    }


    /**
     * Returns true if the object at the given position was NOT NULL, false if it IS NULL.
     *
     * @param bits the bit flags, returned from getNullBits()
     * @param pos  the position (starting with zero)
     * @return true if the object at the given position was NOT NULL, false if it IS NULL.
     */
    public static boolean isNotNullBit(int bits, int pos) {
        int bitflag = 1 << pos;
        return (bitflag & bits) != 0;
    }
}
