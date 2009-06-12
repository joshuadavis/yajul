package org.yajul.fix.util;

import java.nio.charset.Charset;

/**
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:41:35 AM
 */
public class CodecConstants {
    public static final String CHARSET_NAME = "ISO-8859-1";
    public static final Charset CHARSET = Charset.forName(CHARSET_NAME);
    public static final byte SOH = 0x01;
    public static final int CHECKSUM_MODULO = 256;
    public static final byte DEFAULT_TAG_SEPARATOR = '=';
    public static final byte DEFAULT_SEPARATOR = SOH;
    public static final int TAG_BEGINSTRING = 8;
    public static final int TAG_BODYLENGTH = 9;
    public static final int TAG_CHECKSUM = 10;
}
