/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 26, 2002
 * Time: 10:04:17 AM
 */
package org.yajul.util;

import junit.framework.TestCase;

import java.util.Set;
import java.util.HashSet;

public class MergedSetTest  extends TestCase
{
    public MergedSetTest(String name)
    {
        super(name);
    }

    public void testNoIntersection()
    {
        Set a = new HashSet();
        a.add("one");
        a.add("two");
        a.add("three");

        Set b = new HashSet();
        b.add("four");
        b.add("five");
        b.add("six");

        Set intersection = new HashSet();
        Set aOnly = new HashSet();
        Set bOnly = new HashSet();
        Set union = new HashSet();

        MergedSet.merge(a,b,aOnly,bOnly,intersection,union);
        assertEquals(3,aOnly.size());
        assertEquals(3,bOnly.size());
        assertEquals(0,intersection.size());
        assertEquals(6,union.size());
        
        MergedSet merged = new MergedSet(a,b);
        aOnly = merged.getAOnly();
        bOnly = merged.getBOnly();
        intersection = merged.getIntersection();
        union = merged.getUnion();

        assertEquals(3,aOnly.size());
        assertEquals(3,bOnly.size());
        assertEquals(0,intersection.size());
        assertEquals(6,union.size());

    }

    public void testOneIntersection()
    {
        Set a = new HashSet();
        a.add("one");
        a.add("two");
        a.add("three");
        a.add("five");
        Set b = new HashSet();
        b.add("four");
        b.add("five");
        b.add("six");

        Set intersection = new HashSet();
        Set aOnly = new HashSet();
        Set bOnly = new HashSet();
        Set union = new HashSet();

        MergedSet.merge(a,b,aOnly,bOnly,intersection,union);
        assertEquals(3,aOnly.size());
        assertEquals(2,bOnly.size());
        assertEquals(1,intersection.size());
        assertEquals(6,union.size());

        MergedSet merged = new MergedSet(a,b);
        aOnly = merged.getAOnly();
        bOnly = merged.getBOnly();
        intersection = merged.getIntersection();
        union = merged.getUnion();

        assertEquals(3,aOnly.size());
        assertEquals(2,bOnly.size());
        assertEquals(1,intersection.size());
        assertEquals(6,union.size());
    }

    public void testAllIntersection()
    {
        Set a = new HashSet();
        a.add("one");
        a.add("two");
        a.add("three");

        Set b = new HashSet();
        b.add("one");
        b.add("two");
        b.add("three");

        Set intersection = new HashSet();
        Set aOnly = new HashSet();
        Set bOnly = new HashSet();
        Set union = new HashSet();

        MergedSet.merge(a,b,aOnly,bOnly,intersection,union);
        assertEquals(0,aOnly.size());
        assertEquals(0,bOnly.size());
        assertEquals(3,intersection.size());
        assertEquals(3,union.size());

        MergedSet merged = new MergedSet(a,b);
        aOnly = merged.getAOnly();
        bOnly = merged.getBOnly();
        intersection = merged.getIntersection();
        union = merged.getUnion();

        assertEquals(0,aOnly.size());
        assertEquals(0,bOnly.size());
        assertEquals(3,intersection.size());
        assertEquals(3,union.size());

    }

}
