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
package org.yajul.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Provides an implementation of the JAAS CallbackHandler interface using
 * a username string and a pssword.
 * <hr>User: josh
 * <br>Date: Oct 22, 2003
 * <br>Time: 6:34:13 AM
 */
public class SimpleCallbackHandler implements CallbackHandler
{
    private transient String username;
    private transient char[] password;

    /**
     * Log into JAAS using the specified username and password.
     * @param username The username.
     * @param password The password.
     * @param protocolProperty The login protocol that will be used (registered in auth.conf).
     * @throws LoginException if something goes wrong.
     */
    public static void login(String username, char[] password,Object protocolProperty) throws LoginException
    {
        // Get the login protocol (registered in 'auth.conf').
        String protocol = "other";
        if (protocolProperty != null)
            protocol = protocolProperty.toString();
        SimpleCallbackHandler handler = new SimpleCallbackHandler(username, password);
        // 1) Find the login context (in auth.conf), and register the handler.
        LoginContext lc = new LoginContext(protocol, handler);
        // 2) Perform the login!
        lc.login();
    }

    /** Initialize the UsernamePasswordHandler with the usernmae
     and password to use.
     */
    public SimpleCallbackHandler(String username, char[] password)
    {
        this.username = username;
        this.password = password;
    }

    /**
     * Sets any NameCallback name property to the instance username,
     * sets any PasswordCallback password property to the instance, and any
     * password.
     * @param callbacks The list of callbacks.
     * @exception javax.security.auth.callback.UnsupportedCallbackException thrown if any
     * callback other than NameCallback or PasswordCallback is encountered.
     */
    public void handle(Callback[] callbacks) throws
            UnsupportedCallbackException
    {
        for (int i = 0; i < callbacks.length; i++)
        {
            Callback c = callbacks[i];
            if (c instanceof NameCallback)
            {
                NameCallback nc = (NameCallback) c;
                nc.setName(username);
            }
            else if (c instanceof PasswordCallback)
            {
                PasswordCallback pc = (PasswordCallback) c;
                pc.setPassword(password);
            }
            else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback " + callbacks[i]);
            }
        }
    }
}
