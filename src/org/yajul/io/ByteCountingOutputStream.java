/*********************************************************************************
 *   $Header$
 * $Workfile: ByteCountingOutputStream.java $
 * $Revision$
 *  $Modtime: 10/23/00 7:22p $
 *   $Author$
 *
 *               Copyright 1999-2000 metiom, inc.               
 *********************************************************************************/
/*********************************************************************************
*      $Log$
*      Revision 1.1  2002/11/04 13:47:49  pgmjsd
*      - Add new IO classes
*
*      Revision 1.1  2002/09/15 22:29:03  pgmjsd
*      Fix up build.xml and add the new files.
*
*      Revision 1.2  2000/12/01 23:55:33  cvsuser
*      First cut.
*      date	2000.10.23.23.27.00;	author joshuad;	state Exp;
*
* 
* 2     10/23/00 7:27p Joshuad
* First cut.
**********************************************************************************/

package org.yajul.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Output stream wraper that counts the number of bytes.
 */
public class ByteCountingOutputStream extends OutputStream
{
    private int m_bytes;
    private OutputStream m_stream;
    
    /**
     * Creates a new output stream that counts then number of bytes written to 
     * the output stream specified.
     */
    public ByteCountingOutputStream(OutputStream out)
    {
        m_bytes = 0;
        m_stream = out;
    }
    
    /**
     * Closes this output stream and releases any system resources associated with this stream. 
     */
    public void close()
        throws IOException
    {
        m_stream.close();
        m_stream = null;
    }
    
    /**
     * Writes b.length bytes from the specified byte array to this output stream. 
     */
    public void write(byte[] b)
        throws IOException
    {
        m_stream.write(b);
        m_bytes += b.length;
    }
    
    /**
     * Writes len bytes from the specified byte array starting at offset off to this output stream. 
     */
    public void write(byte[] b, int off, int len) 
        throws IOException
    {
        m_stream.write(b,off,len);
        m_bytes += len;
    }
    
    /**
    * Writes the specified byte to this output stream. 
    */
    public  void write(int b)
        throws IOException
    {
        m_stream.write(b);
        m_bytes++;
    }
    
    /**
     * Returns the number of bytes written.
     */
    public int getByteCount() { return m_bytes; }
    
}