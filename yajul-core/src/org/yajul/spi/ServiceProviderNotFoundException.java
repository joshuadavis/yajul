package org.yajul.spi;

/**
 * Thrown when ImplementationFinder encounters a reference to an SPI implementation
 * that was not found.
 * User: jdavis
 * Date: Oct 31, 2003
 * Time: 2:26:14 PM
 * @author jdavis
 */
public class ServiceProviderNotFoundException extends org.yajul.util.DetailedException
{
    /**
     * Default contstructor.
     */
    public ServiceProviderNotFoundException()
    {
    }

    /**
     * Creates a new exception with no nested exception and the specified detail message.
     * @param s the detail message.
     */
    public ServiceProviderNotFoundException(String s)
    {
        super(s);
    }

    /**
     * Constructs a DetailedException with the specified Throwable as the nested exception.
     * @param t an object of type Throwable
     */
    public ServiceProviderNotFoundException(Throwable t)
    {
        super(t);
    }

    /**
     * Constructs a DetailedException with the specified detail message and
     * the specified throwable as the nested exception.
     * @param s the detail message.
     * @param t an object of type Throwable
     */
    public ServiceProviderNotFoundException(String s, Throwable t)
    {
        super(s, t);
    }
}
