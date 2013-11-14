/*
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.yajul.comparators;

import java.io.Serializable;
import java.util.*;

/**
 * A simple comparator chain based on commons-collections ComparatorChain.
 *
 * @author (original) Morgan Delagrange
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * @since Commons Collections 2.0
 */
public class ComparatorChain<T> implements Comparator<T>, Serializable {

    private List<Comparator<T>> comparatorChain;

    /**
     * Construct a ComparatorChain from the Comparators in the
     * List.  All Comparators will default to the forward
     * sort order.
     * @param comparators any number of comparators
     */
    public ComparatorChain(Comparator<T>... comparators) {
        comparatorChain = Arrays.asList(comparators);
    }

    /**
     * Number of Comparators in the current ComparatorChain.
     *
     * @return Comparator count
     */
    public int size() {
        return comparatorChain.size();
    }

    //-----------------------------------------------------------------------

    /**
     * Perform comparisons on the Objects as per
     * Comparator.compare(o1,o2).
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return -1, 0, or 1
     * @throws UnsupportedOperationException if the ComparatorChain does not contain at least one
     *                                       Comparator
     */
    public int compare(T o1, T o2) throws UnsupportedOperationException {
        for (Comparator<T> comparator : comparatorChain) {
            int retval = comparator.compare(o1, o2);
            if (retval != 0) {
                return retval;
            }
        }
        // if comparators are exhausted, return 0
        return 0;
    }

    //-----------------------------------------------------------------------

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals(Object) equals}.
     *
     * @return a suitable hash code
     * @since Commons Collections 3.0
     */
    public int hashCode() {
        int hash = 0;
        if (null != comparatorChain) {
            hash ^= comparatorChain.hashCode();
        }
        return hash;
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is
     * is a {@link Comparator} whose ordering is known to be
     * equivalent to mine.
     * <p/>
     * This implementation returns <code>true</code>
     * iff <code><i>object</i>.{@link Object#getClass() getClass()}</code>
     * equals <code>this.getClass()</code>, and the underlying
     * comparators and order bits are equal.
     * Subclasses may want to override this behavior to remain consistent
     * with the {@link Comparator#equals(Object)} contract.
     *
     * @param object the object to compare with
     * @return true if equal
     * @since Commons Collections 3.0
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (null == object) {
            return false;
        }
        else if (object.getClass().equals(this.getClass())) {
            ComparatorChain chain = (ComparatorChain) object;
            return ((null == comparatorChain ? null == chain.comparatorChain : comparatorChain.equals(chain.comparatorChain)));
        }
        else {
            return false;
        }
    }
}
