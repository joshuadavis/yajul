// $Id$
package org.yajul.security;

import org.apache.log4j.Logger;
import org.yajul.io.Base64Encoder;
import org.yajul.util.StringUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * One way hash for passwords that produces results compatible with the JBoss UsernamePasswordLoginModule and sub-classes
 * such as DatabaseServerLoginModule.  For example, the digest from this class can be stored in a database column which
 * will allow the user to use DatabaseServerLoginModule without any code modifications.  This can also be used with
 * other application servers with a custom 'realm' implementation.<br/>
 * This is a Spring-compatible Javabean as well.
 * <br/>
 * When used with JDKs prior to 1.4, JSSE is required.
 * @author josh Jul 3, 2004 6:13:39 AM
 */
public class CredentialDigester
{

    /**
     * The default digest algorithm - "MD5"
     */
    public static final String DEFAULT_ALGORITHM = "MD5";

    private String algorithm = DEFAULT_ALGORITHM;
    private boolean base16 = false;
    private MessageDigest messageDigest = null;

    /**
     * Returns the digest algorithm name.
     * @return the digest algorithm name.
     */
    public String getAlgorithm()
    {
        return algorithm;
    }

    /**
     * Sets the digest algorithm name (e.g. 'MD5').
     * @param algorithm the digest algorithm name (e.g. 'MD5').
     */
    public void setAlgorithm(String algorithm)
    {
        synchronized (this)
        {
            this.algorithm = algorithm;
            this.messageDigest = null;
        }
    }

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

    /**
     * Digest the password string, and return the digested and encoded string.
     * @param password The password string.
     * @param charset The character set of the password string.  If null, the default character set will be used.
     * @return the digested and encoded string.
     * @throws NoSuchAlgorithmException if the algorithm could not be found.
     */
    public String digest(String password, String charset)
            throws NoSuchAlgorithmException
    {
        byte[] bytes = StringUtil.getBytes(password,charset);   // Get the bytes using the specified charset, or the default
        byte[] digestBytes = digest(bytes);                     // Run the digest algorithm.
        return encodeBytes(digestBytes);                        // Encode the digest.
    }

    private byte[] digest(byte[] bytes)
            throws NoSuchAlgorithmException
    {
        byte[] digestBytes;
        try
        {
            digestBytes = getMessageDigest().digest(bytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw e;
        }
        return digestBytes;
    }

    private MessageDigest getMessageDigest()
            throws NoSuchAlgorithmException
    {
        synchronized (this)
        {
            if (messageDigest == null)
                messageDigest = MessageDigest.getInstance(getAlgorithm());
        }
        return messageDigest;
    }

    private String encodeBytes(byte[] digestBytes)
    {
        return (isBase16()) ? encodeBase16(digestBytes) : Base64Encoder.encode(digestBytes);
    }

    private String encodeBase16(byte[] digestBytes)
    {
        StringBuffer buf = new StringBuffer();
        StringUtil.hexString(buf,digestBytes,null,true);  // Use lower case hex chars.
        return buf.toString();
    }
}
