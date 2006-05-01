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

//import javax.net.ssl.SSLContext;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SocketFactory
{
    private static SocketFactory instance;
    private SSLSocketFactory secureSocketFactory;

    public synchronized static SocketFactory getInstance()
    {
        if (instance == null)
        {
            instance = new SocketFactory();
        }
        return instance;
    }

    private SocketFactory()
    {
    }

    /**
     * Creates a new plain socket, or a secure (SSL) socket, based on the
     * parameters.
     * @return The new socket as the 'Socket' class, regardless of
     * whether it is actually an SSL socket.
     * @param ssl If true, the method will create an SSL socket.  Otherwise, it creates an 'ordinary' socket.
     * @param host The host to connect to.
     * @param port The port to connect to.
     * @throws java.io.IOException If there is a problem creating the socket
     * @throws java.net.UnknownHostException If the host name is not valid.
     */
    public Socket createSocket(boolean ssl,String host,int port)
        throws java.io.IOException, java.net.UnknownHostException, NoSuchAlgorithmException
    {
        Socket socket;

        // Create the socket object differently based on the protocol.
        if (ssl)    // Create an SSL socket.
        {
            SSLSocketFactory factory = getSecureSocketFactory();
            SSLSocket sslSocket = (SSLSocket)factory.createSocket(host,port);
            sslSocket.startHandshake();
            socket = sslSocket;
        }
        else        // Create a 'normal' socket.
            socket = new Socket(host,port);
        // Return the new socket as the more general class.
        return socket;  // Return the socket as the more general class.
    }

    private SSLSocketFactory getSecureSocketFactory()
    {
        synchronized (this)
        {
            if (secureSocketFactory == null)
            {
//                SSLSessionContext ctx = SSLUtil.getSSLSessionContext();
//                secureSocketFactory = ctx.getSocketFactory();
                secureSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            }
        }
        return secureSocketFactory;
    }
}

