package org.yajul.jndi;

/**
 * Thrown when a JNDI lookup fails.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 10:28:44 AM
 */
public class LookupException extends RuntimeException {
    public LookupException(String message) {
        super(message);
    }

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
