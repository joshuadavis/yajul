/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 12, 2002
 * Time: 5:06:49 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jndi;

import junit.framework.TestCase;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Test the simple JNDI implementation.
 */
public class SimpleCtxTest extends TestCase
{
    public SimpleCtxTest(String name)
    {
        super(name);
    }

    private void assertLookupThrowsNamingException(Context ctx, String name)
    {
        NamingException ne = null;

        try
        {
            ctx.lookup(name);
        }
        catch (NamingException e)
        {
            ne = e;
        }
        assertNotNull("NamingException expected for lookup of: " + name, ne);
    }

    public void testInitialContext() throws NamingException
    {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.yajul.jndi.simple.SimpleCtxFactory");
        Context ctx = new InitialContext(env);
        assertEquals("org.yajul.jndi.simple.SimpleCtxFactory",
                ctx.getEnvironment().get(Context.INITIAL_CONTEXT_FACTORY));
        Integer testObject = new Integer(1234);
        ctx.bind("testname", testObject);
        assertEquals(testObject, ctx.lookup("testname"));
        ctx.unbind("testname");
        assertLookupThrowsNamingException(ctx,"testname");
        ctx.close();
    }
}
