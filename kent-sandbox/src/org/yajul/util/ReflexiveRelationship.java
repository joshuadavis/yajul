package org.yajul.util;

import java.util.Set;

/**
 * Represents a single valued 2-way dependency relationship between 2 sets of objects.
 *
 * Example:
 * 
 * <pre>
 * ReflexiveRelationship r = new ReflexiveRelationship();
 * r.add("adam", "CS101");
 * r.add("adam", "CS102");
 * r.add("bill", "CS101");
 * r.add("bill", "CS103");
 *
 * Set adamsClasses = r.getDependents("adam"); //returns a set of "CS101" and "CS102"
 * Set cs102Students = r.getDeterminanta("CS102"); //returns a set of "adam"
 * </pre>
 *
 * <b>Important:</b>  Subsets retrned by getDependents(...) and getDeterminants(...) are 'live'
 * views of the containing set.  Modification to <code>adamsClasses</code> above would modify the
 * containing relationship, and vice-versa.
 * 
 */
class ReflexiveRelationship implements Relationship {

    /** Implements the determinant->dependent relationship */
	protected Relationship m_a;

    /** Implements the dependent->determinant relationship */
	protected Relationship m_b;

    /** Constructs a new relexive relationship.  Uses SetRelationship to implement the underlying relationships */
	public ReflexiveRelationship() {
        m_a = new SetRelationship();
        m_b = new SetRelationship();
    }
    
    /** Constructs a new relexive relationship, using the give relationship implementations.  <code>a</code> and <code>b</code> should be empty, clear is not called. */
	public ReflexiveRelationship(Relationship a, Relationship b) {
        m_a = a;
        m_b = b;
    }

	//
	// Public interface
	//
	
	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public Set getDependents(Object determinant) {
        return m_a.getDependents(determinant);
    }

	/**
	 * Adds a new 2-way dependency between the given <code>determinant</code> and <code>dependent</code>
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void add(Object determinant, Object dependent) {
        m_a.add(determinant, dependent);
        m_b.add(dependent, determinant);
    }

	/**
	 * Adds a new 2-way dependency between the given <code>determinant</code> and <code>dependent</code>.
	 * Nothing is done if the dependency doesn't already exist.
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void remove(Object determinant, Object dependent) {
        m_a.remove(determinant, dependent);
        m_b.remove(dependent, determinant);
    }
    
	/**
	 * Returns true iff this Relationship contains the given dependency
	 */
	public boolean contains(Object determinant, Object dependent) {
        return m_a.contains(determinant, dependent);
    }

	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 * <br><b>Important:</b>  The Set returned is a view over this set.  Any modifications to that Set <em>will</em> affect this Set.
	 * Likewise, modifications to the containing Set will modify this returned subset.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public Set getDeterminants(Object dependent) {
		return m_b.getDependents(dependent);
	}

}
