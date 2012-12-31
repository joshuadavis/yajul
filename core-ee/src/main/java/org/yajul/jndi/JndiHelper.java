package org.yajul.jndi;

import javax.naming.*;

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
     * This should not be used to replace EJB3 injection, but it does come in handy when:
     * <ul>
     * <li>There are EJBs that have circular dependencies, which makes lookup (Service Locator pattern)
     * necessary.</li>
     * <li>There are cluster singletons, or other cases where you want to use a different InitialContext
     * to look up an EJB or other JNDI object.  For example: EJB3 Timers are usually deployed as cluster
     * singletons in JBoss AS, so these should be looked up in HAJNDI.</li>
     * </ul>
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
        final Object object = doLookup(context, name);
        if (clazz.isAssignableFrom(object.getClass())) {
            return (T) object;
        }
        else {
            throw new LookupException(String.format(
                    "Found JNDI name '%s' of type %s, but it cannot be assigned to type: %s",
                    name, object.getClass(), clazz));
        }
    }

    public static String listBindings(Context context, String name) throws NamingException {
        StringBuilder sb = new StringBuilder();
        sb.append("Listing for ").append(name).append("\n");
        listContext("  ", context, name, sb);
        return sb.toString();
    }

    private static void listContext(String prefix, Context context, String name, StringBuilder sb)
            throws NamingException {
        NamingEnumeration<Binding> bindings = context.listBindings(name);
        while (bindings.hasMore()) {
            Binding binding = bindings.next();
            sb.append(prefix).append(name).append(binding.getName()).append(" -> ")
                    .append(binding.getClassName()).append("\n");
            Object obj = binding.getObject();
            if (obj instanceof Context) {
                Context nestedContext = (Context) obj;
                listContext(prefix + binding.getName() + "/",nestedContext,"",sb);
            }
        }
    }

    public static <T> T lookup(InitialContext ic, String name) {
        Object object = doLookup(ic, name);
        try {
            //noinspection unchecked
            return (T) object;
        }
        catch (ClassCastException cce) {
            throw new LookupException(String.format(
                    "Found JNDI name '%s' of type %s, but that isn't the right type.",
                    name, object.getClass()));

        }
    }

    private static Object doLookup(InitialContext ic, String name) {
        InitialContext context = ic;
        if (context == null) {
            try {
                context = new InitialContext();
            }
            catch (NamingException e1) {
                throw new LookupException("Unable to create default InitialContext", e1);
            }
        }
        try {
            return context.lookup(name);
        }
        catch (NamingException e) {
            throw new LookupException(String.format(
                    "Unable to find JNDI name '%s'", name), e);
        }
    }

    public static InitialContext getDefaultInitialContext() {
        try {
            return new InitialContext();
        }
        catch (NamingException e) {
            throw new RuntimeException("Unable to create default InitialContext due to " + e, e);
        }
    }
}
