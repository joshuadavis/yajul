/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.jndi.simple;

import org.yajul.log.Logger;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import java.util.Hashtable;

/**
 * A sample service provider that implements a flat namespace in memory.
 * <ul>
 * <li>All instances should be created by SimpleCtxFactory.</li>
 * </ul>
 * @see SimpleCtxFactory
 */
class SimpleCtx implements Context
{
    // A logger for this class.
    private static Logger log = Logger.getLogger(SimpleCtx.class);

    private static NameParser myParser = new SimpleNameParser();    // The name parser.
    private Hashtable myEnv;                                        // The initial environment.
    private Hashtable bindings = new Hashtable(11);                 // The bindings, the names are the key.

    SimpleCtx(Hashtable environment)
    {
        // Store the initial environment as a clone.
        myEnv = (environment != null)
                ? (Hashtable) (environment.clone())
                : null;

        // If the caller gave a PROVIDER_URL, issue a warning: it will be ignored.
        if (myEnv.get(Context.PROVIDER_URL) != null)
        {
            log.warn("Context.PROVIDER_URL is not supported by this implementation, it will be ignored.");
        }
    }

    public Object lookup(String name) throws NamingException
    {
        if (name.equals(""))
        {
            log.info("lookup() - No name given, returning the root context.");
            // Asking to look up this context itself.  Create and return
            // a new instance with its own independent environment.
            return (new SimpleCtx(myEnv));
        }
        // Otherwise, look the name up in the current bindings.
        Object answer = bindings.get(name);
        if (answer == null)
        {
            log.error("lookup() - '" + name + "' not found");
            throw new NameNotFoundException(name + " not found");
        }
        return answer;
    }

    public Object lookup(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return lookup(name.toString());
    }

    public void bind(String name, Object obj) throws NamingException
    {
        if (name.equals(""))
        {
            throw new InvalidNameException("Cannot bind empty name");
        }
        if (bindings.get(name) != null)
        {
            throw new NameAlreadyBoundException(
                    "Use rebind to override");
        }
        bindings.put(name, obj);
    }

    public void bind(Name name, Object obj) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        bind(name.toString(), obj);
    }

    public void rebind(String name, Object obj) throws NamingException
    {
        if (name.equals(""))
        {
            throw new InvalidNameException("Cannot bind empty name");
        }
        bindings.put(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        rebind(name.toString(), obj);
    }

    public void unbind(String name) throws NamingException
    {
        if (name.equals(""))
        {
            throw new InvalidNameException("Cannot unbind empty name");
        }
        bindings.remove(name);
    }

    public void unbind(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        unbind(name.toString());
    }

    public void rename(String oldname, String newname)
            throws NamingException
    {
        if (oldname.equals("") || newname.equals(""))
        {
            throw new InvalidNameException("Cannot rename empty name");
        }

        // Check if new name exists
        if (bindings.get(newname) != null)
        {
            throw new NameAlreadyBoundException(newname +
                    " is already bound");
        }

        // Check if old name is bound
        Object oldBinding = bindings.remove(oldname);
        if (oldBinding == null)
        {
            throw new NameNotFoundException(oldname + " not bound");
        }

        bindings.put(newname, oldBinding);
    }

    public void rename(Name oldname, Name newname)
            throws NamingException
    {
        // Flat namespace; no federation; just call string version
        rename(oldname.toString(), newname.toString());
    }

    public NamingEnumeration list(String name)
            throws NamingException
    {
        if (name.equals(""))
        {
            // listing this context
            return new FlatNames(this, bindings.keys());
        }

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context)
        {
            return ((Context) target).list("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public NamingEnumeration list(Name name)
            throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return list(name.toString());
    }

    public NamingEnumeration listBindings(String name)
            throws NamingException
    {
        if (name.equals(""))
        {
            // listing this context
            return new FlatBindings(this, bindings.keys());
        }

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context)
        {
            return ((Context) target).listBindings("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    public NamingEnumeration listBindings(Name name)
            throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return listBindings(name.toString());
    }

    public void destroySubcontext(String name) throws NamingException
    {
        throw new OperationNotSupportedException(
                "SimpleCtx does not support subcontexts");
    }

    public void destroySubcontext(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        destroySubcontext(name.toString());
    }

    public Context createSubcontext(String name)
            throws NamingException
    {
        throw new OperationNotSupportedException(
                "SimpleCtx does not support subcontexts");
    }

    public Context createSubcontext(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return createSubcontext(name.toString());
    }

    public Object lookupLink(String name) throws NamingException
    {
        // This flat context does not treat links specially
        return lookup(name);
    }

    public Object lookupLink(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return lookupLink(name.toString());
    }

    public NameParser getNameParser(String name)
            throws NamingException
    {
        return myParser;
    }

    public NameParser getNameParser(Name name) throws NamingException
    {
        // Flat namespace; no federation; just call string version
        return getNameParser(name.toString());
    }

    public String composeName(String name, String prefix)
            throws NamingException
    {
        Name result = composeName(new CompositeName(name),
                new CompositeName(prefix));
        return result.toString();
    }

    public Name composeName(Name name, Name prefix)
            throws NamingException
    {
        Name result = (Name) (prefix.clone());
        result.addAll(name);
        return result;
    }

    public Object addToEnvironment(String propName, Object propVal)
            throws NamingException
    {
        if (myEnv == null)
        {
            myEnv = new Hashtable(5, 0.75f);
        }
        return myEnv.put(propName, propVal);
    }

    public Object removeFromEnvironment(String propName)
            throws NamingException
    {
        if (myEnv == null)
            return null;

        return myEnv.remove(propName);
    }

    public Hashtable getEnvironment() throws NamingException
    {
        if (myEnv == null)
        {
            // Must return non-null
            return new Hashtable(3, 0.75f);
        }
        else
        {
            return (Hashtable) myEnv.clone();
        }
    }

    public String getNameInNamespace() throws NamingException
    {
        return "";
    }

    public void close() throws NamingException
    {
        myEnv = null;
        bindings = null;
    }

    Hashtable getBindingsInternal()
    {
        return bindings;
    }

} // class SimpleCtx
