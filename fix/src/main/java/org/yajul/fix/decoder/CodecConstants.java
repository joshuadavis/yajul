package org.yajul.fix.decoder;

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
}
