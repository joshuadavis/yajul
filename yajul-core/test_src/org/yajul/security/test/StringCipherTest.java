// $Id$

package org.yajul.security.test;

import junit.framework.TestCase;
import org.yajul.security.StringCipher;
import org.yajul.io.Base64FormatException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

/**
 * Tests StringCipher
 */
public class StringCipherTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * 
     * @param name The name of the test case.
     */
    public StringCipherTest(String name)
    {
        super(name);
    }

    /**
     * Test basic encode/decode
     */
    public void testStringCipher() throws Exception
    {
        StringCipher sc = new StringCipher();
        String cleartext = "this is the cleartext";
        String ciphertext = sc.encrypt(cleartext);
        String s = sc.decrypt(ciphertext);
        assertEquals(cleartext,s);
        sc.setKey("this is a passphrase that must be > 24 chars for DESede");
        checkPassphrase(sc, cleartext, ciphertext);


        sc.setAlgorithm("DES");
        sc.setKey(null);    // Regular DES has very strict key criteria, best to generate one on the fly.
        checkPassphrase(sc, cleartext, ciphertext);

/* This isn't working yet.  TODO: figure out how to set the key bytes correctly for DES.
        sc.setAlgorithm("DES");
        sc.setKeyBytes(sc.generateSecretKey().getEncoded());
        checkPassphrase(sc, cleartext, ciphertext);
*/

        sc.setAlgorithm("DESede");
        sc.setKey("this is a passphrase that must be > 24 chars for DESede");
        ciphertext = sc.encrypt(cleartext);
        s = sc.decrypt(ciphertext);
        assertEquals(cleartext,s);

        sc.setBase16(true);
        ciphertext = sc.encrypt(cleartext);
        s = sc.decrypt(ciphertext);
        assertEquals(cleartext,s);

        NoSuchAlgorithmException nsae = null;
        try
        {
            sc.setAlgorithm("FOO-bar");
            sc.setKey("blah-dee blah");
        }
        catch (NoSuchAlgorithmException x)
        {
            nsae = x;
        }
        assertNotNull(nsae);

        // Don't bother checking the other ciphertext, DES knows that it was enciphered with
        // a different secret key.
//        s = sc.decrypt(ciphertext);
//        assertTrue(!s.equals(cleartext));
    }

    private void checkPassphrase(StringCipher sc, String cleartext, String ciphertext)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, Base64FormatException
    {
        String s;
        String ciphertext2 = sc.encrypt(cleartext);
        assertTrue(!ciphertext2.equals(ciphertext));
        s = sc.decrypt(ciphertext2);
        assertEquals(cleartext,s);
        String cleartext2 = "secret-y secret";
        ciphertext2 = sc.encrypt(cleartext2);
        assertEquals(cleartext2,sc.decrypt(ciphertext2));
    }
}
