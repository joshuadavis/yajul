package org.yajul.jndi;

/**
 * Performs a JNDI Lookup.
 * <br>
 * User: josh
 * Date: Sep 11, 2009
 * Time: 10:05:40 AM
 */
public interface JndiLookup
{
    /**
     * Type safe JNDI lookup that always throws a runtime exception.
     * @param clazz the class to cast to
     * @param <T> the class to cast to
     * @param name the name to look up
     * @return the object
     */
    <T> T lookup(Class<T> clazz, String name);    
}
