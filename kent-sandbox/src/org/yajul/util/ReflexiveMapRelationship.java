package org.yajul.util;
/**
 * Represents a single valued dependency relationship between 2 sets of objects.
 *
 * Example:
 * 
 * <pre>
 * Relationship r = new Relationship();
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
 * 
 */
class ReflexiveMapRelationship extends SetRelationship {

	Relationship m_reflection;
	
	public ReflexiveMapRelationship() {
		m_reflection  = new SetRelationship() {
				public boolean remove(Object o) {
					removeNoPropigate(o);
					return super.remove(o);
				}
				public boolean add(Object o) {
					addNoPropigate(o);
					return super.add(o);
				}
			};
	}

	protected void removeNoPropigate(Object o) {
		super.remove(o);
	}

	protected void addNoPropigate(Object o) {
		super.add(o);
	}
	
	//
	// Public interface
	//
	
	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 * <br><b>Important:</b>  The Set returned is a view over this set.  Any modifications to that Set <em>will</em> affect this Set.
	 * Likewise, modifications to the containing Set will modify this returned subset.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public java.util.Set getDeterminants(Object dependent) {
		return m_reflection.getDependents(dependent);
	}
}
