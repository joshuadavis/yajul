// $Id$
package org.yajul.security;

import org.yajul.io.Base64Encoder;
import org.yajul.io.Base64Decoder;
import org.yajul.io.Base64FormatException;
import org.yajul.util.StringUtil;

/**
 * Provides base64 and base16 encoding behavior for encryption and one way hash classes.
 * @author josh Jul 8, 2004 7:17:09 AM
 */
public class Encoder
{
    private boolean base16 = false;

    /**
     * Returns true if the digest will be hex encoded (if false, it will be Base64 encoded, which is the default).
     * @return True if the digest will be hex encoded (if false, it will be Base64 encoded, which is the default).
     */
    public boolean isBase16()
    {
        return base16;
    }

    /**
     * Set to true to use hexadecimal digest encoding, false to use BASE64 encoding.
     * @param base16 true to use hexadecimal digest encoding, false to use BASE64 encoding.
     */
    public void setBase16(boolean base16)
    {
        this.base16 = base16;
    }

    protected String encodeBytes(byte[] bytes)
    {
        return (isBase16()) ? encodeBase16(bytes) : Base64Encoder.encode(bytes);
    }

    private String encodeBase16(byte[] bytes)
    {
        StringBuffer buf = new StringBuffer();
        StringUtil.hexString(buf,bytes,null,true);  // Use lower case hex chars.
        return buf.toString();
    }

    protected byte[] decodeBytes(String s) throws Base64FormatException
    {
        if (isBase16())
            return StringUtil.parseHexString(s);
        else
            return Base64Decoder.decode(s.getBytes());
    }
}
