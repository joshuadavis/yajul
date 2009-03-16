package org.yajul.fix;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.UnsupportedEncodingException;

/**
 * Helper methods for IoBuffer, etc.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:51:06 PM
 */
public class CodecHelper {
    public static byte[] getBytes(String s) {
        try {
            return s.getBytes(CodecConstants.CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getBytes(IoBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    public static boolean matchBytes(IoBuffer buf,int offset,byte[] bytes) {
        for (int i = 0; i < bytes.length ; i++) {
            if (buf.get(offset + i) != bytes[i])
                return false;
        }
        return true;
    }

    public static int indexOf(IoBuffer buf,byte[] bytes) {
        int start = buf.position();
        int end = buf.limit() - bytes.length;
        if (end < 0)
            return -1;
        for (int i = start; i < end; i++) {
            if (buf.get(start + i) == bytes[0] && matchBytes(buf,i,bytes)) {
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

}
