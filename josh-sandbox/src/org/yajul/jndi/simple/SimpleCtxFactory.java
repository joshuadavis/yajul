/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.jndi.simple;

import org.yajul.log.Logger;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * Provides a simple, non-nested, memory based JNDI tree.
 * @author Joshua Davis
 */
public class SimpleCtxFactory implements InitialContextFactory
{
    // A logger for this class.
    private static Logger log = Logger.getLogger(SimpleCtxFactory.class);

    /**
     * This is invoked when the user specifies this class as the Context.INITIAL_CONTEXT_FACTORY value
     * in the Hashtable passed to the constructor for InitialContext.<br/>
     * For example:<br/>
     * <pre>
     * Hashtable env = new Hashtable();
     * env.put(Context.INITIAL_CONTEXT_FACTORY, "org.yajul.jndi.simple.SimpleCtxFactory");
     * Context ctx = new InitialContext(env);
     * </pre>
     */
    public Context getInitialContext(Hashtable env)
    {
        //        return new HierCtx(env);
        log.info("getInitialContext() - ENTER");
        Context ctx = new SimpleCtx(env);
        log.info("getInitialContext() - LEAVE : Returning " + ctx);
        return ctx;
    }
}