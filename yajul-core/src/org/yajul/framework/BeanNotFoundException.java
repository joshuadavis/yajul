// $Id$
package org.yajul.framework;

/**
 * An exception thrown when a bean is not found by the ServiceLocator.
 * @author josh Mar 21, 2004 8:15:53 AM
 */
public class BeanNotFoundException extends Exception
{
    public BeanNotFoundException(String message)
    {
        super(message);
    }
}
