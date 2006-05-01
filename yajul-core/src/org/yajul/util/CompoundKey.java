// $Id$
package org.yajul.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Provides an easy way to make a key for a Map from.  It uses an array of objects, computing hash codes
 * and testing for equality.
 * @author Joshua Davis
 */
public class CompoundKey implements Comparable, Serializable, Cloneable
{
    /** The component objects in the key. **/
    private Object[] components;
    /** The pre-computed hash code. **/
    private transient int hash = 0;
    /** Flag indicating the status of the precomputed hash. **/
    private transient boolean isHashComputed = false;

    /**
     * Computes the hash code for an array of objects.
     * @param components An array of objects.
     * @return int - The hash value.
     */
    public static int computeHash(Object[] components)
    {
        return ArrayUtil.computeHashCode(components);
    }

    /**
     * Creates a new compound key.  The array of objects <b><i>will not be copied</i></b>, so please make sure that
     * it does not change for the life of the compound key object.  Same goes for the objects referenced by the
     * array.
     * @param components The components of the key.
     */
    public CompoundKey(Object[] components)
    {
        setComponents(components);
    }

    /**
     * Convenience constructor: Creates a compound key with two objects.
     * @param one The first component
     * @param two The second component
     */
    public CompoundKey(Object one, Object two)
    {
        Object[] components = new Object[2];
        components[0] = one;
        components[1] = two;
        setComponents(components);
    }

    /**
     * Convenience constructor: Creates a compound key with three objects.
     * @param one The first component
     * @param two The second component
     * @param three The third coponent
     */
    public CompoundKey(Object one, Object two, Object three)
    {
        Object[] components = new Object[3];
        components[0] = one;
        components[1] = two;
        components[2] = three;
        setComponents(components);
    }

    /**
     * Convenience constructor: Creates a compound key with three objects.
     * @param one The first component
     * @param two The second component
     * @param three The third coponent
     * @param four The fourth component
     */
    public CompoundKey(Object one, Object two, Object three,Object four)
    {
        Object[] components = new Object[4];
        components[0] = one;
        components[1] = two;
        components[2] = three;
        components[3] = four;
        setComponents(components);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     * This implementation returns the sum of all the hash codes of the components, using an algorithm
     * similar to that of java.lang.String.hashCode().
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.util.Hashtable
     * @see     java.lang.String#hashCode()
     */
    public int hashCode()
    {
        if (!isHashComputed)
        {
            hash = computeHash(components);
            isHashComputed = true;
        }
        return hash;
    }

    /**
     * Indicates whether some other object is "equal to" this one.  This implementation
     * compares all of the elements of the compound key.
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     * @see     java.lang.Boolean#hashCode()
     * @see     java.util.Hashtable
     * @see     java.util.Arrays#equals(java.lang.Object[], java.lang.Object[])
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        CompoundKey other = (CompoundKey) obj;
        // Check the hash codes first, if they are not equal... return false.
        if (hashCode() != other.hashCode())
            return false;
        // Hash codes are the same, so the objects must be compared.
        // Use the Arrays class as a short cut.
        return Arrays.equals(components, other.components);
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getClass().getName());
        buf.append("@");
        buf.append(Integer.toHexString(System.identityHashCode(this)));
        buf.append("[ ");
        for (int i = 0; i < components.length; i++)
        {
            buf.append("#");
            buf.append(Integer.toString(i));
            buf.append(" [ ");
            buf.append(components[i].toString());
            buf.append(" ] ");
        }

        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     *
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <br><br>
     * <b>Throws:</b>
     * <blockquote>ClassCastException if the specified object's type prevents it
     * from being compared to this Object.</blockquote>
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *      is less than, equal to, or greater than the specified object.
     *
     */
    public int compareTo(Object o)
    {
        CompoundKey other = (CompoundKey) o;

        Comparable a;
        Comparable b;
        // First, check if the length of the arrays is different.
        int rc = components.length - other.components.length;
        if (rc == 0)
        {
            for (int i = 0; i < components.length; i++)
            {
                a = (Comparable) components[i];
                b = (Comparable) other.components[i];
                rc = a.compareTo(b);
                if (rc != 0)    // If the components are not equal, stop now.
                    break;
            } // for
        }
        return rc;
    }

    /**
     * Returns the number of components in the compound key.
     * @return int - The number of components.
     */
    public int size()
    {
        return components.length;
    }

    /**
     * Returns the 'nth' component of the compound key.
     * @param n The index.
     * @return Object - The 'nth' component.
     */
    public Object getComponent(int n)
    {
        return components[n];
    }

    /**
     * Sets the components of the compound key.  <i>IMPORTANT: Do not invoke this method on an object
     * that is already in a Map or Set class!</i>
     * @param components The vector of components.
     */
    public void setComponents(Object[] components)
    {
        for (int i = 0; i < components.length; i++)
        {
            if (components[i] == null)
                throw new IllegalArgumentException("A value for component[" + i + "] is required!");
        }
        this.components = components;
        this.isHashComputed = false;
    }

    /**
     * Returns a new copy of the component array.
     * @return Object[] - A copy of the array of components.
     */
    public Object[] cloneComponents()
    {
        Object[] array = new Object[components.length];
        System.arraycopy(components,0,array,0,components.length);
        return array;
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object.
     * <br>exception  OutOfMemoryError            if there is not enough memory.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not
     *               support the <code>Cloneable</code> interface. Subclasses
     *               that override the <code>clone</code> method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        CompoundKey c = (CompoundKey)super.clone();
        // Object.clone() already copied: hash, isHashComputed
        // Just copy the array.
        c.components = cloneComponents();
        return c;
    }
}

