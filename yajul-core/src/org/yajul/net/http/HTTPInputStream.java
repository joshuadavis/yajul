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

import org.yajul.net.http.Bytes;
import org.yajul.net.http.GenericMessageHeader;
import org.yajul.net.http.HTTPConstants;
import org.yajul.net.http.HTTPHeader;
import org.yajul.io.ParsingInputStream;
import org.yajul.io.StringInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;

/**
 * An input stream with HTTP parsing methods.
 * User: josh
 * Date: Jan 29, 2004
 * Time: 7:28:49 AM
 */
public class HTTPInputStream extends ParsingInputStream implements HTTPConstants
{
    private static int DEFAULT_BUFFER_SIZE = 1024;

    public HTTPInputStream(InputStream in, int bufferSize)
    {
        super(in, bufferSize);
    }

    public HTTPInputStream(InputStream inputStream)
    {
        super(inputStream,DEFAULT_BUFFER_SIZE);
    }

    public HTTPInputStream(byte[] input)
    {
        super(new ByteArrayInputStream(input),DEFAULT_BUFFER_SIZE);
    }

    public HTTPInputStream(String input)
    {
        super(new StringInputStream(input),DEFAULT_BUFFER_SIZE);
    }

    /**
     * Parses an HTTP/1.1 token from the input stream.
     *
     * <i>From RFC 2616</i>
     *  Many HTTP/1.1 header field values consist of words separated by LWS or special characters. These special characters MUST be in a quoted string to be used within a parameter value (as defined in section 3.6).
     *
     * <pre>
     *  token          = 1*<any CHAR except CTLs or separators>
     *  separators     = "(" | ")" | "<" | ">" | "@"
     *                 | "," | ";" | ":" | "\" | <">
     *                 | "/" | "[" | "]" | "?" | "="
     *                 | "{" | "}" | SP | HT
     * </pre>
     */
    public String readToken()
            throws IOException
    {
        StringBuffer buf = new StringBuffer();
        int c = read();
        if (c == -1)
            return null;        // Return null for EOS.
        while ((c != -1) && !Bytes.isControl(c) && !Bytes.isSeparator(c))
        {
            buf.append((char) c);
            c = read();
        }
        if (c != -1)            // If the last char was not EOS, then put it back.
            unread(c);

        String s = buf.toString();
        return s;
    }

    /**
     * Reads a CRLF.
     * <i>From RFC 2616</i>
     * HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all protocol elements except the entity-body (see appendix 19.3 for tolerant applications). The end-of-line marker within an entity-body is defined by its associated media type, as described in section 3.7.
     * <pre>
     *   CRLF           = CR LF
     * </pre>
     */
    public byte[] readCRLF()
            throws IOException
    {
        return matchBytes(CRLF_BYTES);
    }

    /**
     * Reads 'line and whitespace'.
     * <i>From RFC 2616</i>
     * HTTP/1.1 header field values can be folded onto multiple lines if the continuation line begins with a space or horizontal tab. All linear white space, including folding, has the same semantics as SP. A recipient MAY replace any linear white space with a single SP before interpreting the field value or forwarding the message downstream.
     * <pre>
     *   LWS            = [CRLF] 1*( SP | HT )
     * </pre>
     */
    public byte[] readLWS()
            throws IOException
    {
        byte[] crlf = readCRLF();
        byte[] spaces = readCharset(SPHT_BYTES, true);
        if (spaces == null || spaces.length == 0)
        {
            if (crlf != null) unread(crlf);
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (crlf != null)
            baos.write(crlf);
        baos.write(spaces);
        return baos.toByteArray();
    }

    /**
     * Reads up to and including a CRLF.
     * <i>From RFC 2616</i>
     * HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all protocol elements except the entity-body (see appendix 19.3 for tolerant applications). The end-of-line marker within an entity-body is defined by its associated media type, as described in section 3.7.
     * <pre>
     *   CRLF           = CR LF
     * </pre>
     * @return The line.
     * @throws IOException
     */
    public String readLine()
            throws IOException
    {
        StringBuffer buf = new StringBuffer();
        int c = read();
        if (c == -1)
            return null;    // Return null if at EOS.
        while (c != -1)
        {
            if (c == '\r')
            {
                int n = read();
                if (n == '\n')
                    break;
                else
                    unread(n);
            }
            buf.append((char) c);
            c = read();
        }
        String s = buf.toString();
        return s;
    }

    /**
     * Reads an HTTP header element.
     * @return an HTTP header element.
     * @throws IOException if something goes wrong.
     */
    public HTTPHeader readHeader()
            throws IOException
    {
        HTTPHeader h = new HTTPHeader();

        // From HTTP v1.1 RFC 2616:
        // message-header   = field-name ":" [ field-value | token ]
        // field-name       = token
        // field-value      = *( field-content | LWS )
        // field-content    = <the OCTETs making up the field-value>

        h.setName(readToken());    // Parse the 'field-name'.

        // Although the RFC says a colon will follow, be forgiving and
        // eat any spaces or tabs.
        int c = read();
        if (c == -1)
            return null;
        if (c != ':')
            throw new IOException("Bad header field name delmiter '"+(char)c+"'.  The preceeding token was '" + h.getName() + "'");

        // Read the field-value...
        readLWS();
        StringBuffer buf = new StringBuffer();
        c = read();
        while(!Bytes.isControl(c))
        {
            buf.append((char)c);
            c = read();
            if (c == '\r')
            {
                unread(c);
                if (readLWS() == null)
                    break;
            }
        }
        h.setValue(buf.toString());
        // Read the terminating CRLF.
        readCRLF();
        return h;
    }

    /**
     * Reads a generic HTTP message header from the input stream.
     * @return a generic HTTP message header
     * @throws IOException if something goes wrong.
     */
    public GenericMessageHeader readGenericMessageHeader() throws IOException
    {
/*
        generic-message = start-line
                          *(message-header CRLF)
                          CRLF
                          [ message-body ]
        start-line      = Request-Line | Status-Line
        Status-Line     = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
        Request-Line    = Method SP Request-URI SP HTTP-Version CRLF
*/
        GenericMessageHeader header = new GenericMessageHeader();
        header.setStartLine(readLine());
        // CRLF separates headers from the body.
        while (readCRLF() == null)              // While there is no 'CRLF' by itself...
        {
            HTTPHeader h = readHeader();        // ... read the next header.
            if (h == null)                      // If EOS was encountered while reading the header,
                return header;                  // stop now.
            header.putHeader(h);                // Otherwise, add the header to the generic message header set.
        }
        return header;
    }

    /**
     * Reads a single 'chunk' of the incoming stream in the
     * 'chunked transfer encoding' form.  Returns a zero
     * length array if this is the last chunk.
     * See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1">
     * RFC 2616, section 3.1.6</a>.
     */
    public byte[] readChunk() throws IOException
    {
/**
 From RFC 2616, section 3.1.6:

       Chunked-Body   = *chunk
                        last-chunk
                        trailer
                        CRLF

       chunk          = chunk-size [ chunk-extension ] CRLF
                        chunk-data CRLF
       chunk-size     = 1*HEX
       last-chunk     = 1*("0") [ chunk-extension ] CRLF

       chunk-extension= *( ";" chunk-ext-name [ "=" chunk-ext-val ] )
       chunk-ext-name = token
       chunk-ext-val  = token | quoted-string
       chunk-data     = chunk-size(OCTET)
       trailer        = *(entity-header CRLF)
**/
        int chunkSize = 0;
        // Read the chunk-size, the chunk-extension (if any) and a CRLF
        chunkSize = readChunkSize();
        byte[] chars = new byte[chunkSize];
        int c = 0;
        for (int i = 0 ; i < chunkSize; i++)
        {
            c = read();
            if (c < 0)
                throw new IOException("Unexpected end of stream: readChunk()");
            chars[i] = (byte)c;
        }
        // Read and discard the CRLF after the data.
        readCRLF();
        return chars;
    }

    /**
     * Read a chunk size line, returns the chunk size or zero if there are no more chunks to read.
     */
    private int readChunkSize() throws IOException
    {
        String line = readLine();
        StringTokenizer t = new StringTokenizer(line," \t;");
        String size = t.nextToken();
        int chunkSize = Integer.parseInt(size,16);
        return chunkSize;
    }
}
