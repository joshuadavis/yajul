/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 26, 2002
 * Time: 9:15:14 AM
 */
package org.yajul.util;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * Provides merging functionality for a pair of sets, including:
 * <ul>
 * <li>The intersection of the two sets.</li>
 * <li>The union of the two sets.</li>
 * <li>The exclusive elements of each set (i.e. elements in a and not in b,
 * etc.).</li>
 * </ul>
 * @author Joshua Davis
 */
public class MergedSet
{
    /**
     * Utility method that merges two sets, providing the intersection, union
     * and the unique sets for both.  The output sets will not be cleared in
     * this method, so the caller is responsible for providing sets are empty.
     * @param a The first set to be merged.
     * @param b The second set to be merged.
     * @param aOnly *OUTPUT* A set where the 'a only' members will be placed.
     * <i>NOTE: The clear() method will not be invoked!</i>
     * @param bOnly *OUTPUT* A set where the 'b only' members will be placed.
     * <i>NOTE: The clear() method will not be invoked!</i>
     * @param intersection *OUTPUT* A set where the intersection will be placed.
     * <i>NOTE: The clear() method will not be invoked!</i>
     * @param union *OUTPUT* A set where the merged members will be placed.
     * <i>NOTE: The clear() method will not be invoked!</i>
     */
    public static final void merge(Set a, Set b,
                                   Set aOnly, Set bOnly,
                                   Set intersection, Set union)
    {
        Object obj = null;

        // Iterate through a, accumulating object into the union set
        // and the a-only set.

        Iterator iter = a.iterator();

        while (iter.hasNext())
        {
            obj = iter.next();
            // If the object exists in b, then add it to the intersection.
            if (b.contains(obj))
                intersection.add(obj);
            else
                aOnly.add(obj);
            // Add all members of a to the merged set.
            union.add(obj);
        }


        // Iterate through b, accumulating the intersection set, the
        // b-only set, and the union set.

        iter = b.iterator();

        while (iter.hasNext())
        {
            obj = iter.next();
            // If the object exists in a, then add it to the intersection.
            if (a.contains(obj))
            {
                intersection.add(obj);
                // Since this object is in the a set as well, and
                // all objects in the a set have already been added to the
                // union set by the previous loop.  They don't need to be added
                // here.
            }
            else
            {
                bOnly.add(obj);
                // Add the elements from 'b only' into the merged set.
                union.add(obj);
            }
        }
    }

    // --- fields ---

    private Set a;
    private Set b;
    private Set aOnly;
    private Set bOnly;
    private Set intersection;
    private Set union;
    private boolean merged = false;

    // --- constructors ---

    /**
     * Creates a new merged set from the supplied sets.  The result
     * sets will be HashSets.
     * @param a The first set to be merged.
     * @param b The second set to be merged.
     */
    public MergedSet(Set a, Set b)
    {
        this.a = a;
        this.b = b;
        this.aOnly = new HashSet();
        this.bOnly = new HashSet();
        this.intersection = new HashSet();
        this.union = new HashSet();
    }

    // --- methods ---

    /**
     * Returns the 'a' set.
     * @return Set - The 'a' set.
     */
    public Set getA()
    {
        return a;
    }

    /**
     * Returns the 'b' set.
     * @return Set - The 'b' set.
     */
    public Set getB()
    {
        return b;
    }

    /**
     * Returns the set of elements only in the 'a' set.
     * @return Set - The 'a only' set.
     */
    public Set getAOnly()
    {
        mergeIfNeeded();
        return aOnly;
    }

    /**
     * Returns the set of elements only in the 'b' set.
     * @return Set - The 'b only' set.
     */
    public Set getBOnly()
    {
        mergeIfNeeded();
        return bOnly;
    }

    /**
     * Returns the intersection of the 'a' and 'b' sets.
     * @return Set - the intersection
     */
    public Set getIntersection()
    {
        mergeIfNeeded();
        return intersection;
    }

    /**
     * Returns the union of the 'a' and 'b' sets.
     * @return Set - the union
     */
    public Set getUnion()
    {
        mergeIfNeeded();
        return union;
    }

    /**
     * Returns true if the merge operation has taken place.
     * @return boolean - True if merged.
     */
    public boolean isMerged()
    {
        return merged;
    }

    // --- implementation methods ---

    private void mergeIfNeeded()
    {
        if (!merged)
            merge(a, b, aOnly, bOnly, intersection, union);
    }
}