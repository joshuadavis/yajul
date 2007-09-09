package org.yajul.ee5.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper functions for JNDI.
 * <br>User: Joshua Davis
 * Date: Sep 9, 2007
 * Time: 10:17:15 AM
 */
public class JndiHelper {
    /**
     * Encapsulates the standard JNDI lookup calls for EJBs, etc. in a method that
     * throws an uncheckeced exception.  The typical use case would be inside an EAR where
     * a failed lookup means some kind of unrecoverable deployment problem.  That eliminates
     * some of the redundant code sprinkled throughout an application.
     * <br>
     * This should not be used to replace EJB3 injection, but it does come in handy when there
     * are EJB3 beans that have circular dependencies, which makes lookup (Service Locator pattern)
     * necessary.
     *
     * @param context The initial context.  Null to use the default <tt>new InitialContext()</tt>
     * @param clazz   the expected type of the object in JNDI.  For an EJB, this will be the local
     *                interface class.
     * @param name    The JNDI name for the object, e.g. XxxDao/local or, XxxDao/Remote
     * @return the JNDI object
     * @throws LookupException if the lookup fails for any reason
     */
    @SuppressWarnings("unchecked")
    public static <T> T lookup(InitialContext context, Class<T> clazz, String name) {

        if (context == null) {
            try {
                context = new InitialContext();
            } catch (NamingException e) {
                throw new LookupException("Unable to create default InitialContext", e);
            }
        }
        try {
            final Object object = context.lookup(name);
            if (clazz.isAssignableFrom(object.getClass())) {
                return (T) object;
            } else {
                throw new LookupException(String.format(
                        "Found JNDI name '%s' of type %s, but it cannot be assigned to type: %s",
                        name, object.getClass(), clazz));
            }

        } catch (NamingException e) {
            throw new LookupException(String.format(
                    "Unable to find JNDI name '%s'", name), e);
        }
    }
}
