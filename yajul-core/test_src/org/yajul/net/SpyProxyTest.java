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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.yajul.io.StreamCopier;

/**
 * Tests the SpyProxy class.
 */
public class SpyProxyTest extends TestCase
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(SpyProxyTest.class.getName());

    public static final int CLIENT_COUNT = 5;
    public static final int CONNECTION_COUNT = 10;
    public static final int CLIENT_LIMIT = 3;

    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public SpyProxyTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Tests the basic spy proxy.
     */
    public void testSpyProxy() throws Exception
    {

        webProxy("www.w3.org", "http://localhost:8889/Protocols/HTTP/",CLIENT_COUNT);
        webProxy("www.microsoft.com", "http://localhost:8889",1);
    }

    private void webProxy(String host, String spec,int clientCount) throws IOException, InterruptedException
    {
        // TODO: Let the proxy pick an available port.
        SpyProxy proxy = new SpyProxy(host,80,8889);
        // proxy.setDebugText(true);
        proxy.setShowConnections(true);
        proxy.setMaxConnections(CLIENT_LIMIT);
        Thread proxyThread = new Thread(proxy,"SpyProxy-8889");
        log.info("\nStarting proxy server on port " + proxy.getProxyPort() + "...");
        proxyThread.start();

        // Create a few client threads...
        Thread[] clientThreads = new Thread[clientCount];

        for (int i = 0; i < clientThreads.length; i++)
        {
            clientThreads[i] = new Thread(new SimpleClient(spec),"Client-" + i);
        }

        log.info("Starting " + clientThreads.length + " clients...");
        for (int i = 0; i < clientThreads.length; i++)
            clientThreads[i].start();

        log.info("Waiting for clients to join...");
        for (int i = 0; i < clientThreads.length; i++)
            clientThreads[i].join();

        log.info("Shutting down proxy server on port " + proxy.getProxyPort() + "...");
        proxy.shutdown();
        proxyThread.join();
    }

    private class SimpleClient implements Runnable
    {

        private String spec;

        public SimpleClient(String spec)
        {
            this.spec = spec;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see     Thread#run()
         */
        public void run()
        {
            try
            {
                HttpURLConnection con = null;
                for(int i = 0; i < CONNECTION_COUNT ; i++)
                {
                    URL url = new URL(spec);
                    con = (HttpURLConnection)url.openConnection();
                    // Keep-alive doesn't work nicely with the
                    // connection limit in the proxy, so force the server to
                    // disconnect.
                    con.setRequestProperty("Connection","close");
                    InputStream is = con.getInputStream();

                    // Copy the input to "/dev/null".
                    StreamCopier.unsyncCopy(is,null,StreamCopier.DEFAULT_BUFFER_SIZE);
                    is.close();
                    // Disconnect every time.
                    if (con != null)
                        con.disconnect();
                }
                log.info("Finished.");
            }
            catch (java.io.IOException e)
            {
                log.error(e,e);
            }
        }
    }
}
