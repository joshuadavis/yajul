/*********************************************************************************
 *   $Header$
 * $Workfile: TeeOutputStream.java $
 * $Revision$
 *  $Modtime: 10/30/00 12:58p $
 *   $Author$
 *********************************************************************************/
/*********************************************************************************
*      $Log$
*      Revision 1.1  2002/11/04 13:47:49  pgmjsd
*      - Add new IO classes
*
*      Revision 1.1  2002/09/15 22:29:03  pgmjsd
*      Fix up build.xml and add the new files.
*
*      Revision 1.2  2000/12/02 00:00:47  cvsuser
*      Added VSS header.
*      date	2000.10.31.22.38.00;	author joshuad;	state Exp;
*
* 
* 2     10/31/00 5:38p Joshuad
* Added VSS header.
**********************************************************************************/

package org.yajul.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Output stream wrapper that echoes all characters written to the other
 * output streams.
 */
public class TeeOutputStream extends OutputStream
{
    private OutputStream[] m_stream;    // Array of streams to write to.
    
    /**
     * Creates a new output stream that echoes output to both
     * of the specified streams in the constructor.
     */
    public TeeOutputStream(OutputStream out,OutputStream out2)
    {
        m_stream = new OutputStream[2];
        m_stream[0] = out;
        m_stream[1] = out2;
    }
    
    /**
     * Closes this output stream and releases any system resources associated with this stream. 
     */
    public void close()
        throws IOException
    {
        IOException e = null;
        
        for (int i = 0; i < m_stream.length ; i++)
        {
            try { m_stream[i].close(); } catch (IOException ioe) { e = ioe; }
        }
        m_stream = null;
        if (e != null)
            throw e;
    }
    
    /**
     * Writes b.length bytes from the specified byte array to this output stream. 
     */
    public void write(byte[] b)
        throws IOException
    {
        IOException e = null;
        
        for (int i = 0; i < m_stream.length ; i++)
        {
            try {
                m_stream[i].write(b);
            } catch (IOException ioe) { e = ioe; }
        }
        if (e != null)
            throw e;
    }
    
    /**
     * Writes len bytes from the specified byte array starting at offset off to this output stream. 
     */
    public void write(byte[] b, int off, int len) 
        throws IOException
    {
        IOException e = null;
        
        for (int i = 0; i < m_stream.length ; i++)
        {
            try {
                m_stream[i].write(b,off,len);
            } catch (IOException ioe) { e = ioe; }
        }
        if (e != null)
            throw e;
    }
    
    /**
    * Writes the specified byte to this output stream. 
    */
    public  void write(int b)
        throws IOException
    {
        IOException e = null;
        
        for (int i = 0; i < m_stream.length ; i++)
        {
            try {
                m_stream[i].write(b);
            } catch (IOException ioe) { e = ioe; }
        }
        if (e != null)
            throw e;
    }
}
