/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.jndi;

import javax.naming.*;
import java.util.*;

import org.yajul.log.Logger;

/**
 * A sample service provider that implements a flat namespace in memory.
 */
class SimpleCtx implements Context
{
    // A logger for this class.
    private static Logger log = Logger.getLogger(SimpleCtx.class);
    
    Hashtable myEnv;                                    // The initial environment.
    private Hashtable bindings = new Hashtable(11);     // The bindings, the names are the key.
    static NameParser myParser = new SimpleNameParser();

    SimpleCtx(Hashtable environment)
    {
        // Store the initial environment as a clone.
        myEnv = (environment != null)
            ? (Hashtable)(environment.clone()) 
            : null;
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

    public void bind(Name name, Object obj) throws NamingException {
        // Flat namespace; no federation; just call string version
        bind(name.toString(), obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        if (name.equals("")) {
            throw new InvalidNameException("Cannot bind empty name");
        }
        bindings.put(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException {
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
            throws NamingException {
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
            return new FlatNames(bindings.keys());
        } 

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context)
        {
            return ((Context)target).list("");
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
            return new FlatBindings(bindings.keys());
        } 

        // Perhaps 'name' names a context
        Object target = lookup(name);
        if (target instanceof Context)
        {
            return ((Context)target).listBindings("");
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
        Name result = (Name)(prefix.clone());
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
            return (Hashtable)myEnv.clone();
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

    static class SimpleNameParser implements NameParser
    {
        static Properties syntax = new Properties();
        static 
        {
            syntax.put("jndi.syntax.direction", "flat");
            syntax.put("jndi.syntax.ignorecase", "false");
        }
        
        public Name parse(String name) throws NamingException
        {
            return new CompoundName(name, syntax);
        }
    }

    // Class for enumerating name/class pairs
    class FlatNames implements NamingEnumeration
    {
        Enumeration names;

        FlatNames (Enumeration names)
        {
            this.names = names;
        }

        public boolean hasMoreElements()
        {
            return names.hasMoreElements();
        }

        public boolean hasMore() throws NamingException
        {
            return hasMoreElements();
        }

        public Object nextElement()
        {
            String name = (String)names.nextElement();
            String className = bindings.get(name).getClass().getName();
            return new NameClassPair(name, className);
        }

        public Object next() throws NamingException
        {
            return nextElement();
        }
        public void close() {
        }
    }

    // Class for enumerating bindings
    class FlatBindings implements NamingEnumeration
    {
        Enumeration names;

        FlatBindings (Enumeration names)
        {
            this.names = names;
        }

        public boolean hasMoreElements()
        {
            return names.hasMoreElements();
        }

        public boolean hasMore() throws NamingException
        {
            return hasMoreElements();
        }

        public Object nextElement()
        {
            String name = (String)names.nextElement();
            return new Binding(name, bindings.get(name));
        }

        public Object next() throws NamingException
        {
            return nextElement();
        }
        public void close()
        {
        }
    }
} // class SimpleCtx
