// $Id$
package org.yajul.security;

import org.yajul.io.Base64FormatException;
import org.yajul.util.StringUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import java.io.UnsupportedEncodingException;

/**
 * Encipher and decipher strings using DES or DESede
 * @author josh Jul 8, 2004 7:10:54 AM
 */
public class StringCipher extends Encoder
{
    public static final String DESEDE_ALGORITHM = "DESede";
    public static final String DES_ALGORITHM = "DES";

    private static final String DEFAULT_ALGORITHM = "DESede";
    private static final String	UNICODE = "UTF8";
    private String algorithm = DEFAULT_ALGORITHM;
    private SecretKey key;
    private SecretKeyFactory secretKeyFactory;
    private SecureRandom secureRandom;
    private Cipher encryptModeCipher;
    private Cipher decryptModeCipher;

    public void setAlgorithm(String algorithm)
    {
        synchronized (this)
        {
            this.algorithm = algorithm;
            key = null;
            encryptModeCipher = null;
            decryptModeCipher = null;
        }
    }

    public SecureRandom getSecureRandom()
    {
        synchronized (this)
        {
            if (secureRandom == null)
                secureRandom = new SecureRandom();
        }
        return secureRandom;
    }

    public void setKey(String keyPhrase) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException
    {
        if (!StringUtil.isEmpty(keyPhrase))
        {
            byte[] keyBytes = keyPhrase.getBytes(UNICODE);
            setKeyBytes(keyBytes);
        }
        else
            setKeyBytes(null);
    }

    public void setKeyBytes(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException
    {
        SecretKey newKey = null;
        if (keyBytes != null && keyBytes.length > 0)
        {
            KeySpec keySpec = getKeySpec(keyBytes);
            newKey = getSecretKeyFactory().generateSecret(keySpec);
        }

        synchronized (this)
        {
            key = newKey;
            encryptModeCipher = null;
            decryptModeCipher = null;
        }
    }
    
    public SecretKey generateSecretKey()
            throws NoSuchAlgorithmException
    {
        KeyGenerator generator = KeyGenerator.getInstance(algorithm);
        generator.init(getSecureRandom());
        SecretKey secretKey = generator.generateKey();
        return secretKey;
    }

    private KeySpec getKeySpec(byte[] keyBytes)
            throws InvalidKeyException, NoSuchAlgorithmException
    {
        KeySpec keySpec = null;
        if ( algorithm.equals( DESEDE_ALGORITHM) )
        {
            keySpec = new DESedeKeySpec( keyBytes );
        }
        else if ( algorithm.equals( DES_ALGORITHM ) )
        {
            keySpec = new DESKeySpec( keyBytes );
        }
        else
        {
            throw new NoSuchAlgorithmException( "Algorithm not supported: "
                                                + algorithm );
        }
        return keySpec;
    }

    private SecretKeyFactory getSecretKeyFactory() throws NoSuchAlgorithmException
    {
        synchronized (this)
        {
            if (secretKeyFactory == null)
                secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        }
        return secretKeyFactory;
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException
    {
        synchronized (this)
        {
            if (key == null)
            {
                key = generateSecretKey();
            }
        }
        return key;
    }

    private Cipher getEncryptModeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        synchronized (this)
        {
            if (encryptModeCipher == null)
            {
                encryptModeCipher = Cipher.getInstance(algorithm);
                encryptModeCipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            }
        }
        return encryptModeCipher;
    }

    private Cipher getDecryptModeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        synchronized (this)
        {
            if (decryptModeCipher == null)
            {
                decryptModeCipher = Cipher.getInstance(algorithm);
                decryptModeCipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            }
        }
        return decryptModeCipher;
    }

    public String encrypt(String cleartext) throws NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException
    {
        return encrypt(cleartext,null);
    }

    public String encrypt(String cleartext, String charset) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher c = getEncryptModeCipher();
        byte[] input = StringUtil.getBytes(cleartext, charset);
        byte[] output = c.doFinal(input);
        return encodeBytes(output);
    }

    public String decrypt(String ciphertext) throws Base64FormatException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher c = getDecryptModeCipher();
        byte[] input = decodeBytes(ciphertext);
        byte[] output = c.doFinal(input);
        return new String(output);
    }
}
