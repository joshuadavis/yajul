
package org.yajul.util;

import junit.framework.TestSuite;
import junit.framework.TestCase;
import junit.framework.Test;
import java.util.Set;

/**
 * Tests for org.yajul.util.Relationship
 */
public class RelationshipTest extends TestCase {
    
    public RelationshipTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(RelationshipTest.class);
        
        return suite;
    }
    
	/**
	 * Runs org.yajul.util.Relationship through a number of tests where student names are
	 * associated with classes
	 */
    public void testStudentClasses() {
		
		Relationship r = new Relationship();

		//add some students->classes
		r.add("kent", "CS101");
		r.add("kent", "CS102");
		r.add("kent", "EN203");
		r.add("kent", "EN504");
		r.add("alice", "CS101");
		r.add("alice", "CS204");
		r.add("alice", "CS355");

		//verify kent's classes
		Set kentsClasses = r.getDependents("kent");
		assertEquals(4, kentsClasses.size());
		assertTrue(kentsClasses.contains("CS101"));
		assertTrue(kentsClasses.contains("CS102"));
		assertTrue(kentsClasses.contains("EN203"));
		assertTrue(kentsClasses.contains("EN504"));

		//verify alice's classes
		Set alicesClasses = r.getDependents("alice");
		assertEquals(3, alicesClasses.size());
		assertTrue(alicesClasses.contains("CS101"));
		assertTrue(alicesClasses.contains("CS204"));
		assertTrue(alicesClasses.contains("CS355"));
		assertTrue(!alicesClasses.contains("EN203"));

		//test adding a duplicate
		r.add("kent", "CS102");
		assertTrue(r.size() == 7);
		
		//test removing from the subset
		alicesClasses.remove("CS101");
		assertTrue(!alicesClasses.contains("CS101"));
		//verify removing from the subset also removes from the containing set
		assertTrue(!r.contains("alice", "CS101"));

		//test removing from the containing set
		r.remove("kent", "EN203");
		assertTrue(!r.contains("kent", "EN203"));
		//verify removing from the containing set also removes from the subset
		assertTrue(!kentsClasses.contains("EN203"));
		
		//test getting a non-existent item
		Set joesClasses = r.getDependents("joe");
		assertEquals(0, joesClasses.size());
		
		//Check toString()
		//(order is guaranteed, as this is a sorted set)
		assertEquals("[alice->CS204, alice->CS355, kent->CS101, kent->CS102, kent->EN504]", r.toString());
    }
}
