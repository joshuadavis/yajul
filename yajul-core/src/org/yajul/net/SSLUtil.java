/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.net;

import org.apache.log4j.Logger;

import javax.net.ssl.SSLSessionContext;
import java.security.*;
import java.security.cert.CertificateException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides utility methods for using SSL/HTTPS.
 * User: josh
 * Date: Jan 24, 2004
 * Time: 3:24:16 PM
 */
public class SSLUtil
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(SSLUtil.class.getName());

    /** The default SSL context. */
    public static final String DEFAULT_SSL_CONTEXT          = "TLS";
    /** The default key manager. */
    public static final String DEFAULT_KEY_MANAGER          = "SunX509";
    /** The default key store name. */
    public static final String DEFAULT_KEY_STORE_NAME       = "JKS";
    /** The system property name that will define the trust store (keystore). */
    public static final String PROP_TRUST_STORE             = "javax.net.ssl.trustStore";
    /** The system property name that will define the trust store (keystore) password. */
    public static final String PROP_TRUST_STORE_PASSWORD    = "javax.net.ssl.trustStorePassword";

    public static SSLSessionContext initializeSSLContext()
            throws NoSuchAlgorithmException, KeyStoreException, IOException,
            CertificateException, UnrecoverableKeyException, KeyManagementException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException
    {

        /*
         * Set up a key manager for client authentication
         * if asked by the server.  Use the implementation's
         * default TrustStore and secureRandom routines.
         */
        SSLSessionContext ctx = getSSLSessionContext();
        Object kmf = getInstance("javax.net.ssl.KeyManagerFactory","com.sun.net.ssl.KeyManagerFactory",DEFAULT_KEY_MANAGER);
        KeyStore ks = KeyStore.getInstance(DEFAULT_KEY_STORE_NAME);
        String keystoreFileName = System.getProperty(PROP_TRUST_STORE);
        String password = System.getProperty(PROP_TRUST_STORE_PASSWORD);
        char[] passphrase = password.toCharArray();
        ks.load(new FileInputStream(keystoreFileName), passphrase);
        Method m = kmf.getClass().getMethod("init",new Class[] { KeyStore.class, String.class });
        m.invoke(kmf,new Object[] { ks, passphrase });

        m = kmf.getClass().getMethod("getKeyManagers",new Class[0] );
        Object keyManagers = m.invoke(kmf,new Object[0]);

        m = ctx.getClass().getMethod("init",new Class[] { Object.class, Object.class, Object.class });
        m.invoke(ctx,new Object[] { keyManagers, null, null} );
        return ctx;
    }

    public static SSLSessionContext getSSLSessionContext()
            throws NoSuchAlgorithmException
    {
        try
        {
            SSLSessionContext ctx = (SSLSessionContext)getInstance("javax.net.ssl.SSLContext", "com.sun.net.ssl.SSLContext", DEFAULT_SSL_CONTEXT);
            return ctx;
        }
        catch (Exception e)
        {
            log.error(e,e);
            throw new NoSuchAlgorithmException("Unable to find SSLSesisonContext due to: " + e);
        }
    }

    private static Object getInstance(String jsseClass, String sunClass, String arg) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Class c = null;
        c = findClass(jsseClass, sunClass);


        // Find the 'getInstance(String) static method.
        Method m = c.getMethod("getInstance", new Class[] { String.class });

        Object instance = m.invoke(null,new Object[] { arg });
        return instance;
    }

    private static Class findClass(String jsseClass, String sunClass) throws ClassNotFoundException
    {
        Class c;
        // Look for the JDK 1.4.x SSLContext class.
        try
        {
            c = Class.forName(jsseClass);
        }
        catch (ClassNotFoundException e)
        {
            c = null;
        }

        // If not found, look for the Sun JSSE SSLContext class (JDK1.3)
        if (c == null)
        {
            c = Class.forName(sunClass);
        }
        return c;
    }
}
