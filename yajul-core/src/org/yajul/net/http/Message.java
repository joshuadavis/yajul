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

import org.apache.log4j.Logger;
import org.yajul.io.StreamCopier;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Jan 22, 2004
 * Time: 9:21:59 PM
 */
public class Message implements HTTPConstants
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(Message.class);

    /** Reader that handles the input stream for the response. **/
    private HTTPInputStream reader;
    /** The header for the message. **/
    private MessageHeader header;
    /** # of msec it took to read the content. **/
    private long transferTime;
    /** The message content. **/
    private byte[] content;

    /** Create a response with the given reader. */
    public Message(HTTPInputStream reader,MessageHeader header)
    {
        this.header = header;
        this.reader = reader;
    }

    public MessageHeader getHeader()
    {
        return header;
    }

    public long getTransferTime()
    {
        return transferTime;
    }

    /**
     * Returns the reader for the response which can be used for
     * reading the body of the message.
     */
    HTTPInputStream getInputStream()
    {
        return reader;
    }

    /**
     * Reads 'chunked' content from the reader.
     */
    private byte[] readChunked(HTTPInputStream r) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] chunk = r.readChunk();
        int chunks = 0;
        while (chunk != null && chunk.length > 0)
        {
            chunks++;
            baos.write(chunk);
            chunk = r.readChunk();
        }

        if (log.isDebugEnabled())
            log.debug("readChunked() : " + chunks + " chunks read.");

        return baos.toByteArray();
    }

    /**
     * Read the message content.
     */
    public void readContent() throws IOException
    {
        HTTPInputStream r = getInputStream();

        if (r == null)
            throw new IOException("Response reader is null!");

        long start = System.currentTimeMillis();
        byte[] content = null;
        switch (header.getTransferMode())
        {
            case MessageHeader.TRANSFER_MODE_LENGTH:
                int len = header.getContentLength();
                content = StreamCopier.readByteArray(r, len);
                break;
            case MessageHeader.TRANSFER_MODE_CLOSE:
                content = StreamCopier.readByteArray(r);
                break;
            case MessageHeader.TRANSFER_MODE_CHUNKED:
                content = readChunked(r);
                break;
            default:
                throw new IOException("Unsupported transfer encoding or content length!");
        }
        setContent(content);
        transferTime = System.currentTimeMillis() - start;
        close();
    }

    private void setContent(byte[] content)
    {
        this.content = content;
    }

    /** Returns the content as an array of bytes.  If the content has not been
     * read yet, it will be read.
     */
    public byte[] getContent()
    {
        if (content == null)
        {
            try
            {
                readContent();
            }
            catch (IOException ioe)
            {
                return new byte[0];
            }
        }
        return content;
    }


    /** Closes the underlying reader. */
    public void close()
    {
        try
        {
            getInputStream().close();
        }
        catch (IOException ignore)
        {
        }
    }
}
