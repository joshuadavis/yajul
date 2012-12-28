package org.yajul.io;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.util.logging.Level;
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

    /**
     * Writes the optional externalizable object directly. NOTE: Use ObjectOutput.writeObject() if
     * you expect the object to have multiple references!
     * @param out the output stream
     * @param ex the object to write
     * @throws IOException if something goes wrong
     */
    public static void writeNullable(ObjectOutput out, Externalizable ex) throws IOException {
//        out.writeObject(ex);
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
     * @param in the input stream
     * @param exClass the object class
     * @param <T> parameterized object class
     * @return the object, or null
     * @throws IOException if something goes wrong
     * @throws ClassNotFoundException if something goes wrong
     */
    public static <T extends Externalizable> T readNullable(ObjectInput in, Class<T> exClass) throws IOException, ClassNotFoundException {
//        return exClass.cast(in.readObject());
        boolean isNull = readIsNull(in);
        if (isNull)
            return null;
        else {
            T ex = null;
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
}
