package org.yajul.scanner;

import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

/**
 * Test the classpath scanner.
 * <br>
 * User: josh
 * Date: Mar 6, 2008
 * Time: 6:24:45 PM
 */
public class ScannerTest {


    @Test
    public void testBasicScanner() {
        // NOTE: The properties resource must be in the same package is this test.
        BasicScanner myScanner = new BasicScanner("org/yajul/scanner/scanner-marker.properties");
        List<String> items1 = myScanner.getItems();
        System.out.println("paths=" + myScanner.getPaths());
        Set<String> items = new TreeSet<String>(items1);
        assertTrue(items.contains("scanner-marker.properties"));
        assertTrue(items.contains(this.getClass().getSimpleName().replace('.','/') + ".class"));
    }
}
