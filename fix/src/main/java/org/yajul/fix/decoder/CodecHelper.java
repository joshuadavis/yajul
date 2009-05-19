package org.yajul.fix.decoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.yajul.fix.util.CodecConstants;

import java.io.UnsupportedEncodingException;

/**
 * Helper methods for IoBuffer, etc.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:51:06 PM
 */
public class CodecHelper {

    public static boolean matchBytes(IoBuffer buf,int offset,byte[] bytes) {
        for (int i = 0; i < bytes.length ; i++) {
            if (buf.get(offset + i) != bytes[i])
                return false;
        }
        return true;
    }

    public static int indexOf(IoBuffer buf,byte[] bytes) {
        return indexOf(buf,buf.position(),bytes);
    }

    public static int indexOf(IoBuffer buf,int start,byte[] bytes) {
        int end = buf.limit() - bytes.length;
        if (end < 0)
            return -1;
        byte b = bytes[0];
        for (int i = start; i < end; i++) {
            byte b1 = buf.get(i);
            if (b1 == b && matchBytes(buf,i,bytes)) {
                return i;
            }
        }
        return -1;
    }

    public static int parseDigits(IoBuffer buf,int start, int len) {
        int value = 0;
        for (int i = start; i < start + len ; i++) {
            byte ch = buf.get(i);
            if (Character.isDigit(ch)) {
                value = value * 10 + (ch - '0');
            }
        }
        return value;
    }

    public static byte[] copyBytes(IoBuffer in, int start, int length) {
        byte[] bytes = new byte[length];
        int position = in.position();
        in.position(start);
        in.get(bytes, 0, length);
        // Put the position back where it was.
        in.position(position);
        return bytes;
    }
}
