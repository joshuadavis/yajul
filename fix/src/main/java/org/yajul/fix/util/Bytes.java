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
}
