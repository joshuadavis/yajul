package org.yajul.fix.util;

import java.io.UnsupportedEncodingException;

/**
 * Helper methods for dealing with byte arrays.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:34:50 PM
 */
public class Bytes {
    public static byte[] getBytes(String s) {
        try {
            return s.getBytes(CodecConstants.CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static int checksum(byte[] bytes, int start, int end, int modulo) {
        int sum = sum(bytes, start, end);
        return sum % modulo;
    }

    public static int sum(byte[] bytes, int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            byte b = bytes[i];
            sum += b;
        }
        return sum;
    }

    public static int sum(byte[] bytes) {
        return sum(bytes,0,bytes.length);
    }

    public static int count(byte[] bytes, byte b) {
        int count = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == b)
                count++;
        }
        return count;
    }

    public static int parseDigits(byte[] bytes, int start, int end) {
        int value = 0;
        for (int i = start; i < end; i++) {
            byte b = bytes[i];
            value = value * 10 + (b - '0');
        }
        return value;
    }

    private final static int [] INT_DIGITS_TABLE = { 9, 99, 999, 9999, 99999, 999999, 9999999,
                                     99999999, 999999999, Integer.MAX_VALUE };

    public static int numdigits(int i) {
        int x = (i < 0) ? -i : i;
        for (int j = 0; ; j++)
            if (x < INT_DIGITS_TABLE[j])
                return j+1;
    }

    public static byte[] copy(byte[] bytes) {
        return copy(bytes,0,bytes.length);
    }

    public static byte[] copy(byte[] bytes, int start, int end) {
        int len = end - start;
        byte[] copy = new byte[len];
        System.arraycopy(bytes,start,copy,0, len);
        return copy;
    }

    public static void append(StringBuilder sb,byte[] bytes) {
        sb.append(new ByteCharSequence(bytes));
    }

}
