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
package org.yajul.net.http;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Jan 25, 2004
 * Time: 9:50:55 AM
 */
public interface HeaderConstants
{
    /** Header name for the host. */
    String HOST             = "Host";
    /** Header name for the user agent. */
    String USER_AGENT       = "User-Agent";
    /** Header name for connection. */
    String CONNECTION       = "Connection";
    /** Header name for transfer encoding. */
    String TRANSFER_ENCODING = "Transfer-Encoding";
    /** Header name for accept. */
    String ACCEPT           = "Accept";
    /** Header name for content type. */
    String CONTENT_TYPE     = "Content-Type";
    /** Header name for content length. */
    String CONTENT_LENGTH   = "Content-Length";
    /** Header name for content encoding. */
    String CONTENT_ENCODING = "Content-Encodong";
    /** Header name for content disposition. */
    String CONTENT_DISPOSITION = "Content-Disposition";
    /** Header name for setting a cookie.  Sent by server to client in HTTP Response. */
    String SET_COOKIE = "Set-Cookie";
    /** Sent by client to server return a cookie. */
    String COOKIE = "Cookie";
    /** Header name for proxy authorization. */
    String PROXY_AUTH       = "Proxy-Authorization";
    /** Header name for basic authorization. */
    String AUTHORIZATION    = "Authorization";
    /** Header name for redirection. */
    String LOCATION    = "Location";
    /** Default value for User-Agent header. */
    String DEFAULT_USER_AGENT   = "Yajul-HTTP/1.0";
    /** Default value for Accept header. */
    String DEFAULT_ACCEPT       = "text/html, text/xml, image/gif, image/jpeg, *; q=.2, */*; q=.2";
    /** Default value for Connection header (close connections by default). */
    String DEFAULT_CONNECTION   = "close";
    /** The prefix for basic authorization. */
    String BASIC_PREFIX = "Basic ";
    /** Separator for basic authorization. */
    String BASIC_SEPARATOR = ":";
}
