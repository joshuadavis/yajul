/*********************************************************************************
 *   $Header$
 * $Workfile: ByteCountingInputStream.java $
 * $Revision$
 *  $Modtime: 10/23/00 7:22p $
 *   $Author$
 *********************************************************************************/
/*********************************************************************************
*      $Log$
*      Revision 1.1  2002/11/04 13:47:48  pgmjsd
*      - Add new IO classes
*
*      Revision 1.1  2002/09/15 22:29:03  pgmjsd
*      Fix up build.xml and add the new files.
*
*      Revision 1.2  2000/12/01 23:55:17  cvsuser
*      First cut.
*      date	2000.10.23.23.27.00;	author joshuad;	state Exp;
*
* 
* 2     10/23/00 7:27p Joshuad
* First cut.
**********************************************************************************/

package org.yajul.io;

import java.io.InputStream;

/**
 * InputStream class that counts the number of bytes read.
 */
public class ByteCountingInputStream extends InputStream
{
    private int m_bytes;
    private InputStream m_stream;
    
    public ByteCountingInputStream(InputStream is)
    {
        m_stream = is;
    }
    
    /**
     * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the next caller of a method for this input stream. 
     */
    public int available()
        throws java.io.IOException
    {
        return m_stream.available();
    }
    /**
     * Closes this input stream and releases any system resources associated with the stream. 
     */
    public void close() 
        throws java.io.IOException
    {
        m_stream.close();
        m_stream = null;
    }
    /**
     * Marks the current position in this input stream. 
     */
    public void mark(int readlimit) 
    {
        m_stream.mark(readlimit);
    }

    /**
     * Tests if this input stream supports the mark and reset methods. 
     */
    public boolean markSupported() 
    {
        return m_stream.markSupported();
    }
    
    /**
     *  Reads the next byte of data from the input stream. 
     */
    public int read()
        throws java.io.IOException
    {
        int b = m_stream.read();
        m_bytes++;
        return b;
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array b. 
     */
    public int read(byte[] b) 
        throws java.io.IOException
    {
        int bytes = m_stream.read(b);
        m_bytes += bytes;
        return bytes;
    }
    
    /**
     * Reads up to len bytes of data from the input stream into an array of bytes. 
     */
    public int read(byte[] b, int off, int len) 
        throws java.io.IOException
    {
        int bytes = m_stream.read(b,off,len);
        m_bytes += bytes;
        return bytes;
    }
    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream. 
     */
    public void reset() 
        throws java.io.IOException
    {
        m_stream.reset();
    }
     
    /**
     * Skips over and discards n bytes of data from this input stream. 
     */
    public long skip(long n) 
        throws java.io.IOException
    {
        long b = m_stream.skip(n);
        m_bytes += b;
        return b;
    }
    
    /**
     * Returns the number of bytes read from the input stream.
     */
    public int getByteCount() { return m_bytes; }
  }