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
package org.yajul.net.http.server;

import org.yajul.net.AbstractServerSocketListener;
import org.yajul.net.AbstractClientConnection;
import org.yajul.net.http.server.HTTPServerAttributes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A mini HTTPConstants server.
 * User: josh
 * Date: Jan 17, 2004
 * Time: 11:35:10 AM
 */
public class HTTPServer extends AbstractServerSocketListener
        implements HTTPServerAttributes
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(HTTPServer.class);

    /** The default server socket timeout for HTTPConstants servers. */
    public static final int DEFAULT_HTTP_TIMEOUT = 30000;
    /** The default maximum number of connections for an HTTPConstants server. */
    public static final int DEFAULT_HTTP_CONNECTIONS = 32;

    private boolean keepAlive = true;

    /**
     * Creates a Web server at the specified port number.
     */
    public HTTPServer (int port) throws IOException
    {
        super(port);
        setConnectionTimeout(DEFAULT_HTTP_TIMEOUT);
        setMaxConnections(DEFAULT_HTTP_CONNECTIONS);
    }

    /**
     * Accept the incoming connection and create a client connection object.
     * @param incoming The incoming socket.
     * @return A new client connection object.
     * @throws java.io.IOException if something goes wrong.
     */
    protected AbstractClientConnection acceptClient(Socket incoming) throws IOException
    {
        return new HTTPClientConnection(this,incoming);
    }

    /** Returns true if the server should support keep-alive connections. */
    public boolean getKeepAlive() { return keepAlive; }

    /** Enable / Disable HTTPConstants keep alive support for the server. */
    public void setKeepAlive(boolean flag) { keepAlive = true; }

    /**
     * Handle an unexpected exception.
     * @param t The unexpected exception.
     */
    protected void unexpected(Throwable t)
    {
        log.error(t,t);
    }
}
