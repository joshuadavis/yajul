/*********************************************************************************
 *   $Header$
 * $Workfile: Base64FormatException.java $
 * $Revision$
 *  $Modtime: 10/25/00 11:28p $
 *   $Author$
 *********************************************************************************/
/*********************************************************************************
 *      $Log$
 *      Revision 1.1  2002/09/15 19:03:13  pgmjsd
 *      Add Base64 encoding / decoding clases and unit test.
 *
 *      Revision 1.4  2000/12/01 23:54:53  cvsuser
 *      Added VSS headers, minor cleanup.
 *      date	2000.10.26.03.30.00;	author joshuad;	state Exp;
 *
 *
 * 4     10/25/00 11:30p Joshuad
 * Added VSS headers, minor cleanup.
 **********************************************************************************/

// Base64FormatException.java
// $Id$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.yajul.util;

/**
 * Exception for invalid BASE64 streams.
 */

public class Base64FormatException extends Exception
{

    /**
     * Create that kind of exception
     * @param msg The associated error message
     */

    public Base64FormatException(String msg)
    {
        super(msg);
    }

}
