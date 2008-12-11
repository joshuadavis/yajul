package org.yajul.util;

import junit.framework.TestCase;

import java.util.Set;
import java.util.HashSet;

/**
 * Test the classpath scanner.
 * <br>
 * User: josh
 * Date: Mar 6, 2008
 * Time: 6:24:45 PM
 */
public class ScannerTest extends TestCase {
    public ScannerTest(String s) {
        super(s);
    }

    public void testBasicScanner() {
        BasicScanner myScanner = new BasicScanner("org/yajul/util/test-properties.properties");
        myScanner.scan();
        Set<String> items = new HashSet<String>(myScanner.getItems());
        assertTrue(items.contains("test-properties.properties"));
        assertTrue(items.contains(ScannerTest.class.getSimpleName().replace('.','/') + ".class"));
    }
}
