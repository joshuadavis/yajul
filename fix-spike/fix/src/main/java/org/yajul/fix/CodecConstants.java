package org.yajul.fix;

import static org.yajul.fix.CodecHelper.getBytes;

import java.nio.charset.Charset;

/**
 *
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:41:35 AM
 */
public class CodecConstants {
    public static final String CHARSET_NAME = "ISO-8859-1";
    public static final Charset CHARSET = Charset.forName(CHARSET_NAME);
    public static final byte SOH = 0x01;
    public static final byte[] MSGLEN_HEADER = getBytes("8=FIX");
    public static final byte[] FIX_TRAILER = getBytes("10=");
}
