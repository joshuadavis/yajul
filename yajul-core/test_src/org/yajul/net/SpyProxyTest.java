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

import junit.framework.TestCase;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import org.yajul.io.StreamCopier;

/**
 * Tests the SpyProxy class.
 */
public class SpyProxyTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public SpyProxyTest(String name)
    {
        super(name);
    }

    /**
     * Tests the basic spy proxy.
     */
    public void testSpyProxy() throws Exception
    {
        // Create a proxy for www.yahoo.com

        SpyProxy proxy = new SpyProxy("www.yahoo.com",80,8888);
        //proxy.setDebugText(true);
        proxy.setShowConnections(true);
        Thread proxyThread = new Thread(proxy,"SpyProxy-8888");
        System.out.println("\nStarting proxy server on port " + proxy.getProxyPort() + "...");
        proxyThread.start();

        URL url = new URL("http://localhost:8888");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        // Copy the input to "/dev/null".
        StreamCopier.unsyncCopy(is,null,StreamCopier.DEFAULT_BUFFER_SIZE);
        System.out.println("Shutting down proxy server on port " + proxy.getProxyPort() + "...");
        proxy.shutdown();
        assertEquals(1,proxy.getConnectionsAccepted());
    }
}
