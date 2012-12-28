package org.yajul.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Test for Comparator classes.
 * <br>
 * User: josh
 * Date: Dec 24, 2009
 * Time: 8:27:08 AM
 */
public class ComparatorTest extends TestCase {
    public void testReverseComparator() {
        String[] array = {"f", "c", "a", "b", "e", "d"};
        Arrays.sort(array, new ReverseComparator<String>(ComparatorUtil.STRING_COMPARATOR));
        String[] reverse = {"f", "e", "d", "c", "b", "a"};
        assertTrue(Arrays.equals(reverse,array));
    }

    public void testNullComparator() {
        String[] array = {"f", "c", null, "b", "e", "d"};
        Arrays.sort(array, new NullComparator<String>(ComparatorUtil.STRING_COMPARATOR));
        String[] expect = { null, "b", "c", "d", "e", "f" };
        assertTrue(Arrays.equals(expect,array));
        Arrays.sort(array, new NullComparator<String>(true,ComparatorUtil.STRING_COMPARATOR));
        String[] expect2 = { "b", "c", "d", "e", "f", null };
        assertTrue(Arrays.equals(expect2,array));
    }

    public void testComparators() {
        assertTrue(ComparatorUtil.NULL_LOW_COMPARATOR.compare(null,"a") < 0);
        assertTrue(ComparatorUtil.NULL_LOW_COMPARATOR.compare("", null) > 0);
        assertTrue(ComparatorUtil.NULL_LOW_COMPARATOR.compare(null, null) == 0);
        assertTrue(ComparatorUtil.NULL_EQUALS_EMPTY_STRING_COMPARATOR.compare("", null) == 0);
        assertTrue(ComparatorUtil.NULL_EQUALS_EMPTY_STRING_COMPARATOR.compare(null, "") == 0);
        assertTrue(ComparatorUtil.NULL_EQUALS_EMPTY_STRING_COMPARATOR.compare("", "") == 0);
        assertTrue(ComparatorUtil.NULL_EQUALS_EMPTY_STRING_COMPARATOR.compare(null, null) == 0);
    }
    
    public void testComparatorChain() {
        Thing[] array = { new Thing("f",1), new Thing("c", 2), new Thing("a", 2), new Thing("b",1), new Thing("e",2), new Thing("d",3) };
        Comparator<Thing> departmentIdSort = new Comparator<Thing>() {
            public int compare(Thing o1, Thing o2) {
                return ComparatorUtil.compareIntegers(o1.getDepartmentId(),o2.getDepartmentId());
            }
        };
        Comparator<Thing> nameSort = new Comparator<Thing>() {
            public int compare(Thing o1, Thing o2) {
                return ComparatorUtil.STRING_COMPARATOR.compare(o1.getName(),o2.getName());
            }
        };

        Arrays.sort(array,new ComparatorChain<Thing>(departmentIdSort,nameSort));
        Thing[] sorted = { new Thing("b",1), new Thing("f",1),  new Thing("a", 2), new Thing("c", 2),  new Thing("e",2), new Thing("d",3) };
        assertTrue(Arrays.equals(sorted,array));
    }

    private static class Thing {
        private String name;
        private int departmentId;

        private Thing(String name, int departmentId) {
            this.name = name;
            this.departmentId = departmentId;
        }

        public String getName() {
            return name;
        }

        public int getDepartmentId() {
            return departmentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Thing)) return false;

            Thing thing = (Thing) o;

            if (departmentId != thing.departmentId) return false;
            if (!name.equals(thing.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + departmentId;
            return result;
        }

        @Override
        public String toString() {
            return "Thing{" +
                    "name='" + name + '\'' +
                    ", departmentId=" + departmentId +
                    '}';
        }
    }
}
