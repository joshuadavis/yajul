
package org.yajul.util;

import junit.framework.TestSuite;
import junit.framework.TestCase;
import junit.framework.Test;
import java.util.Set;

/**
 * Tests for org.yajul.util.Relationship
 */
public class DelimitedStringBufferTest extends TestCase {
    
    public DelimitedStringBufferTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DelimitedStringBufferTest.class);
        
        return suite;
    }
    
    abstract class Name {
        String prefix, first, middle, last, suffix;
        abstract String getFullName();
    }

    abstract class Data {
        String a, b, c, d, e;
        abstract String getData();
    }
    
    public void testAppend() {
        
        nameTest( new Name() {
            String getFullName() {
                return new DelimitedStringBuffer(" ")
                .append(prefix)
                .append(first)
                .append(middle)
                .append(last)
                .append(suffix)
                .toString();
            }
        });
        
        dataTest( new Data() {
            String getData() {
                return new DelimitedStringBuffer(",", true)
                .append(a)
                .append(b)
                .append(c)
                .append(d)
                .append(e)
                .toString();
            }
        });
    }

    public void testPrepend() {
        
        nameTest( new Name() {
            String getFullName() {
                return new DelimitedStringBuffer(" ")
                .prepend(suffix)
                .prepend(last)
                .prepend(middle)
                .prepend(first)
                .prepend(prefix)
                .toString();
            }
        });
        
        dataTest( new Data() {
            String getData() {
                return new DelimitedStringBuffer(",", true)
                .prepend(e)
                .prepend(d)
                .prepend(c)
                .prepend(b)
                .prepend(a)
                .toString();
            }
        });
	}

    public void testNoData() {
        assertEquals("", new DelimitedStringBuffer().toString());
    }

    public void testNoDelimiter() {
        assertEquals("xy",
            new DelimitedStringBuffer()
            .append("x")
            .append("y")
            .toString());
    }

    private void nameTest(Name name) {
        name.first = "John";
        name.last = "Doe";
        assertEquals("John Doe", name.getFullName());
        name.prefix = "Mr.";
        assertEquals("Mr. John Doe", name.getFullName());
        name.middle = "Jacob";
        assertEquals("Mr. John Jacob Doe", name.getFullName());
    }
        
    private void dataTest(Data data) {
        data.a = "aaa";
        data.c = "ccc";
        assertEquals("aaa,,ccc,,", data.getData());
        data.b = "bbb";
        assertEquals("aaa,bbb,ccc,,", data.getData());
        data.d = "ddd";
        data.e = "eee";
        assertEquals("aaa,bbb,ccc,ddd,eee", data.getData());
    }

}
