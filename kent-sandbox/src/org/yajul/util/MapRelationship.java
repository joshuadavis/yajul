package org.yajul.util;

import java.util.Set;

/**
 * Implements the Relationship interface as a HashMap of HashSets.
 * <br><br>
 * 
 * This implementation should perform lookups sleightly faster than SetRelationship, but
 * use memory less efficiently.  Specifically:<br><br>
 * 
 * {@link SetRelationship SetRelationship}: Lookups are <b>O(log(N))</b>.  The structure consists of exactly 1 Set.<br>
 * <code><b>MapRelationship</b></code>: Lookups are <b>O(1)</b>.  The structure uses <b>N+M</b> Sets, where <b>N</b> is the number of determinants, and <b>M</b> is the
 * number of determinants that have been 'accessed', but not added to.  (The implementation 'leaks' empty Sets).
 * If frequent adding and removing is needed, SetRelationship should be used instead.
 * 
 */
public class MapRelationship extends java.util.HashMap implements Relationship {

	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void add(Object determinant, Object dependent) {
		Set dependents = (Set)get(determinant);
		if (dependents == null) {
			dependents = new java.util.HashSet();
			put(determinant, dependents);
		}
		dependents.add(dependent);
	}

	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>.
	 * Nothing is done if the dependency doesn't already exist.
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void remove(Object determinant, Object dependent) {
		Set dependents = (Set)get(determinant);
		if (dependents != null)
			dependents.remove(dependent);
	}
	
	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 * <br><b>Important:</b>  The Set returned is a view over this relationship.  Any modifications to that Set <em>will</em> affect this Set.
	 * Likewise, modifications to the containing Set will modify this returned subset.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public Set getDependents(Object determinant) {
		Set dependents = (Set)get(determinant);
		if (dependents == null) {
			dependents = new java.util.HashSet();
			put(determinant, dependents);
		}
		return dependents;
	}

	/**
	 * Returns true iff this Relationship contains the given dependency
	 */
	public boolean contains(Object determinant, Object dependent) {
		return getDependents(determinant).contains(dependent);
	}
    
    /**
     * Removes any empty set objects in the map. 
     * <em>Note: Clients may have references to theses empty objects, with the intention of
     * populating them in the future.  Compact should only be called if your are sure
     * this is not the case.</em>  If many dependencies will be added and removed, use
     * SetRelationship instead of MapRelationship.
     */ 
    public void compact() {
        java.util.Iterator it = values().iterator();
        while (it.hasNext()) {
            Set s = (Set)it.next();
            if (s.isEmpty())
                it.remove();
        }
    }
}
