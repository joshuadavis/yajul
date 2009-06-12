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
        int sum = 0;
        for (int i = start; i <= end; i++) {
            byte b = bytes[i];
            sum += b;
        }
        return sum % modulo;
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

    public static int numdigits(int i) {
        if (i == 0 || i < 10)
            return 1;
        int digits = 0;
        while (i > 0) {
            i /= 10;
            digits++;
        }
        return digits;
    }
}
