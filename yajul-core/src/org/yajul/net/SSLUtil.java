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

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.*;
import java.security.cert.CertificateException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Provides utility methods for using SSL/HTTPS.
 * User: josh
 * Date: Jan 24, 2004
 * Time: 3:24:16 PM
 */
public class SSLUtil
{
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

    public static SSLContext initializeSSLContext()
            throws NoSuchAlgorithmException, KeyStoreException, IOException,
            CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        /*
         * Set up a key manager for client authentication
         * if asked by the server.  Use the implementation's
         * default TrustStore and secureRandom routines.
         */

        SSLContext ctx = SSLContext.getInstance(DEFAULT_SSL_CONTEXT);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(DEFAULT_KEY_MANAGER);
        KeyStore ks = KeyStore.getInstance(DEFAULT_KEY_STORE_NAME);
        String keystoreFileName = System.getProperty(PROP_TRUST_STORE);
        String password = System.getProperty(PROP_TRUST_STORE_PASSWORD);
        char[] passphrase = password.toCharArray();
        ks.load(new FileInputStream(keystoreFileName), passphrase);
        kmf.init(ks, passphrase);
        ctx.init(kmf.getKeyManagers(), null, null);
        return ctx;
    }
}
