package org.yajul.util;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a single valued dependency relationship between 2 sets of objects.
 *
 * Example:
 * 
 * <pre>
 * Relationship r = new SetRelationship();
 * r.add("adam", "CS101");
 * r.add("adam", "CS102");
 * r.add("bill", "CS101");
 * r.add("bill", "CS103");
 *
 * Set adamsClasses = r.getDependents("adam"); //returns a set of "CS101" and "CS102"
 * </pre>
 *
 * <b>Important:</b>  Subsets retrned by getDependents(...) are 'live'
 * views of the containing set.  Modification to <code>adamsClasses</code> above would modify the
 * containing relationship, and vice-versa.
 */
public interface Relationship {

	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public Set getDependents(Object determinant);

	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void add(Object determinant, Object dependent);

	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>.
	 * Nothing is done if the dependency doesn't already exist.
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void remove(Object determinant, Object dependent);
	
	/**
	 * Returns true iff this Relationship contains the given dependency
	 */
	public boolean contains(Object determinant, Object dependent);
}
