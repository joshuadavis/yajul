/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 5:44:51 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.yajul.log.Logger;

/**
 * Represents a graph of all methods, which methods call them, and which methods are called by them.
 * Each method is represented by a MethodNode, which has a set of callers, and a set of called MethodNodes
 * @author Joshua Davis
 */
public class CallGraph
{
    private static Logger log = Logger.getLogger(CallGraph.class);

    public class TypeNode
    {
        private ReferenceType type;
        private Set methods;

        public TypeNode(ReferenceType type)
        {
            this.type = type;
        }

        /**
         * Returns a hash code value for the object. This method is
         * supported for the benefit of hashtables such as those provided by
         * <code>java.util.Hashtable</code>.
         * @return  a hash code value for this object.
         * @see     java.lang.Object#equals(java.lang.Object)
         * @see     java.util.Hashtable
         */
        public int hashCode()
        {
            return type.hashCode();
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * @param   obj   the reference object with which to compare.
         * @return  <code>true</code> if this object is the same as the obj
         *          argument; <code>false</code> otherwise.
         * @see     #hashCode()
         * @see     java.util.Hashtable
         */
        public boolean equals(Object obj)
        {
            if (!(obj instanceof TypeNode))
                return false;
            else
                return type.equals(((TypeNode) obj).type);
        }

    }

    public class MethodNode
    {
        private Method method;      // This method.
        private Set callers;        // A set of callers.
        private Set called;         // A set of called methods.
        private int callCount;      // The number of calls.

        public MethodNode(Method m)
        {
            method = m;
            callers = new HashSet();
            called = new HashSet();
            // Add the type node, if it doesn't exist.
            findTypeNode(m);
        }

        /**
         * Returns a hash code value for the object. This method is
         * supported for the benefit of hashtables such as those provided by
         * <code>java.util.Hashtable</code>.
         * @return  a hash code value for this object.
         * @see     java.lang.Object#equals(java.lang.Object)
         * @see     java.util.Hashtable
         */
        public int hashCode()
        {
            return method.hashCode();
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * @param   obj   the reference object with which to compare.
         * @return  <code>true</code> if this object is the same as the obj
         *          argument; <code>false</code> otherwise.
         * @see     #hashCode()
         * @see     java.util.Hashtable
         */
        public boolean equals(Object obj)
        {
            if (!(obj instanceof MethodNode))
                return false;
            else
                return method.equals(((MethodNode) obj).method);
        }

        /**
         * Adds a new called method to the method node.  The called method node will
         * have it's call count incremented.
         * @param m     The (jdi) Method being called.
         */
        public void addCalled(Method m)
        {
            MethodNode node = findMethodNode(m);
            called.add(node);
            node.callers.add(this);
            node.callCount++;
        }

        public Method getMethod()
        {
            return method;
        }

        public void setMethod(Method method)
        {
            this.method = method;
        }

        public Set getCallers()
        {
            return callers;
        }

        public Set getCalled()
        {
            return called;
        }

        public int getCallCount()
        {
            return callCount;
        }

        /**
         * Returns a string representation of the object, listing the callers and the called
         * methods.
         * @return  a string representation of the object.
         */
        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            appendFullMethodName(buf, method);
            buf.append(" callCount=");
            buf.append(Integer.toString(callCount));

            Iterator iter = null;
            MethodNode node = null;

            buf.append("\nCallers:\n");
            iter = callers.iterator();
            while (iter.hasNext())
            {
                node = (MethodNode) iter.next();
                buf.append("\t");
                appendFullMethodName(buf, node.getMethod());
                if (iter.hasNext())
                    buf.append("\n");
            }

            buf.append("\nCalled:\n");
            iter = called.iterator();
            while (iter.hasNext())
            {
                node = (MethodNode) iter.next();
                buf.append("\t");
                appendFullMethodName(buf, node.getMethod());
                if (iter.hasNext())
                    buf.append("\n");
            }

            return buf.toString();
        }

        private void appendFullMethodName(StringBuffer buf, Method method)
        {
            buf.append(method.declaringType().name());
            buf.append(".");
            buf.append(method.name());
        }
    }

    private Map methods;    // A map of (method,MethodNode) for callers
    private Map types;      // A map of (type,TypeNode) for all methods.
    private int callCount;  // The total number of calls.

    public CallGraph()
    {
        methods = new HashMap();
        types = new HashMap();
        callCount = 0;
    }

    MethodNode findMethodNode(Method m)
    {
        MethodNode node = (MethodNode) methods.get(m);
        if (node == null)
        {
            node = new MethodNode(m);
            methods.put(m, node);
        }
        return node;
    }

    TypeNode findTypeNode(Method m)
    {
        return findTypeNode(m.declaringType());
    }

    TypeNode findTypeNode(ReferenceType t)
    {
        TypeNode node = (TypeNode) types.get(t);
        if (node == null)
        {
            node = new TypeNode(t);
            types.put(t, node);
        }
        return node;
    }

    /**
     * Adds a call to the call graph, creating MethodNode objects if they don't already exist.
     * @param caller    The (jdi) method doing the calling (can be null, if it is unknown).
     * @param called    The (jdi) method being called.
     */
    public void addCall(Method caller, Method called)
    {
        callCount++;
        if (log.isDebugEnabled() && ((callCount % 1000) == 0))
            log.debug("total calls = " + callCount);

        if (caller != null)
        {
            MethodNode source = findMethodNode(caller);
            source.addCalled(called);
        }
        else
        {
            MethodNode target = findMethodNode(called);
            target.callCount++;
        }
    }

    /**
     * Returns an iterator of all of the MethodNodes in the call graph.
     * @return Iterator for all of the MethodNodes in the call graph.
     */
    public Iterator iterator()
    {
        return methods.values().iterator();
    }
}
