package org.yajul.util;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import java.util.AbstractSet;

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
public class Relationship extends TreeSet {

	//
	// Public interface
	//
	
	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void add(Object determinant, Object dependent) {
		add(new Entry(determinant, dependent));
	}

	/**
	 * Adds a new dependency between the given <code>determinant</code> and <code>dependent</code>.
	 * Nothing is done if the dependency doesn't already exist.
	 *
	 * @param determinant Object which 'determines' the relationship.  Must implemenent the Comparable interface.
	 * @param dependent Object which is determined by <code>determinant</code>.  Must implemenent the Comparable interface.
	 */
	public void remove(Object determinant, Object dependent) {
		remove(new Entry(determinant, dependent));
	}
	
	/**
	 * Gets a set of the dependent objects of <code>determinant</code>.
	 * <br><b>Important:</b>  The Set returned is a view over this set.  Any modifications to that Set <em>will</em> affect this Set.
	 * Likewise, modifications to the containing Set will modify this returned subset.
	 *
	 * @param determinant The object that determines the returned dependents
	 * @return A Set of determinant objects. 
	 */
	public Set getDependents(Object determinant) {

		//get the subset view
		Set dependencies = subSet(
			new Boundary(determinant, Boundary.LOWER),
			new Boundary(determinant, Boundary.UPPER));

		//wrap it in a set that returnes Entry.dependents, instead of Entrys.
		return new TransformingSet(dependencies) {
			public Object transform(Object o) {
				return ((Entry)o).m_dependent;
			}
		};
	}

	/**
	 * Returns true iff this Relationship contains the given dependency
	 */
	public boolean contains(Object determinant, Object dependent) {
		return contains(new Entry(determinant, dependent));
	}

	//
	// Implementation
	//
	
	/** 
	 * Represents an individual dependency between one determinant and one dependent
	 */
	private static class Entry implements Comparable {

		/** The object which 'determines' the dependent.  (A in A->B) */
		public Object m_determinant;
		/** The object which is determined.  (B in A->B) */
		public Object m_dependent;

		/**
		 * Creates a new Relationship.Entry
		 *
		 * @param determinant The object which determines the dependency
		 * @param dependent the object that is determined by the dependency
		 */
		public Entry(Object determinant, Object dependent) {
			m_determinant = determinant;
			m_dependent = dependent;
		}

		/** 
		 * Implements the Comparable interface, such that this object, when sorted, will be grouped by
		 * determinant, then dependent
		 *
		 * @param The Entry or Boundary object to compare to
		 * @return Result of comparing the determinants, or if they are equal, the result of comparing the dependents.
		 */
		public int compareTo(Object o) {
			if (o instanceof Entry) {
				int diff = ((Comparable)m_determinant).compareTo(((Entry)o).m_determinant);
				if (diff == 0)
					return ((Comparable)m_dependent).compareTo(((Entry)o).m_dependent);
				else
					return diff;
			}
			else
				return ((Boundary)o).compareTo(this) * -1;
		}

		/**
		 * Returns true iff this Entry's determinant and dependent are equal the other other object's
		 *
		 * @param o the Entry object to compare to
		 */
		public boolean equals(Object o) {
			Entry other = (Entry)o;
			return (m_determinant.equals(other.m_determinant) &&
					m_dependent.equals(other.m_dependent));
		}
		
		/**
		 * Returns a string representation of this dependency.
		 * Form is A->B
		 * (determinant + "->" + dependent)
		 *
		 * @param o the Entry object to compare to
		 */
		public String toString() {
			return m_determinant + "->" + m_dependent;
		}
	}
	
	/**
	 * Represents an upper or lower boundary of a determinant in a sorted set.
	 * Used to allows subsets of the Relationship set to be defined
	 */
	private static class Boundary implements Comparable {
	
		/** Represents an upper boundary for the determinant */
		public static final int UPPER = 1;
		/** Represents a lower boundary for the determinant */
		public static final int LOWER = -1;

		/** The determinant object that this Boundry is an upper or lower boundary for */
		private Object m_determinant;
		
		/** UPPER or LOWER, determines if this is an upper or lower boundary */
		private int m_side;

		/** 
		 * Creates a new Boundary for the given determinant
		 *
		 * @param determinant The object to create an upper or lower boundary for
		 * @param side Specifies if this is an upper or lower boundary, must be Boundary.UPPER or Boundary.LOWER
		 */
		public Boundary(Object determinant, int side) {
			m_determinant = determinant;
			m_side = side;
		}
		
		/** 
		 * Implements the Comparable interface, such that this object, when sorted, will fall
		 * in the boundary between it's determinant, and it's adjacent Entrys
		 *
		 * @param The Entry or Boundary object to compare to
		 * @return Result of comparing the determinants, or if they are equal, the 'side'.  (1 for UPPER, -1 for LOWER)
		 */
		public int compareTo(Object o) {
			if (o instanceof Entry) {
				int diff = ((Comparable)m_determinant).compareTo(((Entry)o).m_determinant);
				if (diff  == 0)
					return m_side;
				else
					return diff;
			}
			else
				return m_side;
		}
	}

	/**
	 * A wrapper for a set that performs a transform on the contained objects
	 * before they are returned to the client.  Used by Relationship to provide
	 * a view over the Relationship Entries, that appears as a set of 'dependents'.
	 */
	abstract static class TransformingSet extends AbstractSet {
		
		/** The contained set */
		private Set m_set;
		
		/** Constructor */
		public TransformingSet(Set set) {
			m_set  = set;
		}
		
		/** The translform method to call before objects are returned to clients */
		public abstract Object transform(Object o);

		/** Retrieves a new iterator over the set. */
		public Iterator iterator() {
			
			return new Iterator() {
				public Iterator m_it = m_set.iterator();
				
				public Object next() {
					return transform(m_it.next());
				}
				
				public boolean hasNext() {
					return m_it.hasNext();
				}
				
				public void remove() {
					m_it.remove();
				}
			};
		}

		/** Gets the size of this set */
		public int size() {
			return m_set.size();
		}
	}
}

