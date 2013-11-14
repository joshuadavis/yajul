package org.yajul.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Provides an easy way to make a key for a Map from.  It uses an array of objects, computing hash codes
 * and testing for equality.  Immutable, and the components should be immutable as well.
 * Objects used in the compound key should implement Comparable and Serializable if the CompoundKey
 * is to be used in either of those ways.
 *
 * @author Joshua Davis
 */
public class CompoundKey implements Comparable<CompoundKey>, Serializable, Cloneable {

    private static final int HASH_NUMBER = 31;

    /**
     * The component objects in the key.
     */
    private final Object[] components;

    /**
     * The pre-computed hash code.
     */
    private final int hash;

    /**
     * Computes the hash code for an array of objects.
     *
     * @param components An array of objects.
     * @return int - The hash value.
     */
    public static int computeHash(Object[] components) {
        // Sum all of the hash codes of the components, using an algorithm similar to that used by
        // java.lang.String.
        int rv = 0;
        int limit = components.length;
        for (int i = 0; i < limit; i++) {
            Object c = components[i];
            if (c == null)
                throw new IllegalArgumentException("A value for component[" + i + "] is required!");
            rv += c.hashCode() * (HASH_NUMBER ^ (limit - i));
        }
        return rv;
    }

    /**
     * Compare arrays of objects, as parallel arrays.
     * <p/>
     * NOTE: May throw a class cast exception if
     * the a_array objects do not implement Comparable<Object>!
     * <p/>
     * <br><br>
     * <b>Throws:</b>
     * <blockquote>ClassCastException if the type of any object in a_array prevents it
     * from being compared to the corresponding object in b_array.</blockquote>
     *
     * @param a_array an array of objects that implement Comparable
     * @param b_array a parallel array of objects.
     * @return a negative integer, zero, or a positive integer as a_array
     *         is less than, equal to, or greater than b_array
     */
    public static int compareObjectArrays(Object[] a_array, Object[] b_array) {
        // First, check if the length of the arrays is different.
        int rc = a_array.length - b_array.length;
        if (rc == 0) {
            Comparable<Object> a;
            Object b;
            for (int i = 0; i < a_array.length; i++) {
                //noinspection unchecked
                a = (Comparable<Object>) a_array[i];
                b = b_array[i];
                //noinspection unchecked
                rc = a.compareTo(b);
                if (rc != 0)    // If the a_array are not equal, stop now.
                    break;
            } // for
        }
        return rc;
    }

    /**
     * Creates a new compound key.  The array of objects <b><i>will not be copied</i></b>, so please make sure that
     * it does not change for the life of the compound key object.  Same goes for the objects referenced by the
     * array.
     *
     * @param components The components of the key.
     */
    public CompoundKey(Object... components) {
        if (components == null)
            throw new IllegalArgumentException("components cannot be null!");

        if (components.length == 0)
            throw new IllegalArgumentException("Must have at least one component!");

        this.components = components;
        hash = computeHash(components);
    }

    /**
     * Indicates whether some other object is "equal to" this one.  This implementation
     * compares all of the elements of the compound key.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *         argument; <code>false</code> otherwise.
     * @see java.lang.Boolean#hashCode()
     * @see java.util.Hashtable
     * @see java.util.Arrays#equals(java.lang.Object[], java.lang.Object[])
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompoundKey) {
            CompoundKey other = (CompoundKey) obj;
            // Check the hash codes first, if they are not equal... return false.
            if (hashCode() != other.hashCode())
                return false;
            // Hash codes are the same, so the objects must be compared.
            // Use the Arrays class as a short cut.
            return Arrays.equals(components, other.components);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
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
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getClass().getName());
        buf.append("@");
        buf.append(Integer.toHexString(System.identityHashCode(this)));
        buf.append("[ ");
        for (int i = 0; i < components.length; i++) {
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
     * NOTE: May throw a class cast exception if the 'components' of this
     * CompoundKey do not implement Comparable.
     * <p/>
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     * <p/>
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
     * @param other the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    public int compareTo(CompoundKey other) {
        return compareObjectArrays(components, other.components);
    }

    /**
     * Generic cast get method.
     *
     * @param clazz the type
     * @param index the index
     * @param <T>   the type
     * @return the element of the key, autocasted
     */
    public <T> T get(Class<T> clazz, int index) {
        return clazz.cast(getComponent(index));
    }

    /**
     * Returns the number of components in the compound key.
     *
     * @return int - The number of components.
     */
    public int size() {
        return components.length;
    }

    /**
     * Returns the 'nth' component of the compound key.
     *
     * @param n The index.
     * @return Object - The 'nth' component.
     */
    public Object getComponent(int n) {
        return components[n];
    }

    /**
     * Returns a new copy of the component array.
     *
     * @return Object[] - A copy of the array of components.
     */
    public Object[] cloneComponents() {
        Object[] array = new Object[components.length];
        System.arraycopy(components, 0, array, 0, components.length);
        return array;
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object.
     * <br>exception  OutOfMemoryError            if there is not enough memory.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the <code>Cloneable</code> interface. Subclasses
     *                                    that override the <code>clone</code> method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException {
        return new CompoundKey(cloneComponents());
    }

    /**
     * A two-component compound key using generics.
     *
     * @param <X> the type of the first component
     * @param <Y> the type of the second component.
     */
    public static class Two<X, Y> extends CompoundKey {
        protected Two(Object one, Object two, Object three) {
            super(one, two, three);
        }

        public Two(X one, Y two) {
            super(one, two);
        }

        /**
         * @return the first component of the key
         */
        public X getOne() {
            //noinspection unchecked
            return (X) getComponent(0);
        }

        /**
         * @return the second component of the key
         */
        public Y getTwo() {
            //noinspection unchecked
            return (Y) getComponent(1);
        }
    }

    /**
     * A three-component compound key using generics.
     *
     * @param <X> the type of the first component
     * @param <Y> the type of the second component.
     * @param <Z> the type of the third component.
     */
    public static class Three<X, Y, Z> extends Two<X, Y> {
        public Three(X one, Y two, Z three) {
            super(one, two, three);
        }

        /**
         * @return the third component of the key
         */
        public Z getThree() {
            //noinspection unchecked
            return (Z) getComponent(2);
        }
    }
}
