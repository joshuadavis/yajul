package org.yajul.sql;

import org.yajul.util.DetailedException;

/**
 * Thrown when there is a metadata problem.
 * User: jdavis
 * Date: Oct 23, 2003
 * Time: 3:55:08 PM
 * @author jdavis
 */
public class MetaDataException extends DetailedException
{
    /**
     * Default contstructor.
     */
    public MetaDataException()
    {
    }

    /**
     * Creates a new exception with no nested exception and the specified detail message.
     * @param s the detail message.
     */
    public MetaDataException(String s)
    {
        super(s);
    }

    /**
     * Constructs a DetailedException with the specified Throwable as the nested exception.
     * @param t an object of type Throwable
     */
    public MetaDataException(Throwable t)
    {
        super(t);
    }

    /**
     * Constructs a DetailedException with the specified detail message and
     * the specified throwable as the nested exception.
     * @param s the detail message.
     * @param t an object of type Throwable
     */
    public MetaDataException(String s, Throwable t)
    {
        super(s, t);
    }
}
