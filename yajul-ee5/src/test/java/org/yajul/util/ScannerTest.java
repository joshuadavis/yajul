package org.yajul.util;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

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
        MyScanner myScanner = new MyScanner("test-properties.properties");
        myScanner.scan();
        List<String> items = myScanner.getItems();


        assertTrue(items.contains("test-properties.properties"));
        assertTrue(items.contains(ScannerTest.class.getName().replace('.','/') + ".class"));

    }

    private class MyScanner extends AbstractScanner {
        private List<String> items = new LinkedList<String>();

        public MyScanner(String resourceName) {
            super(resourceName);
        }

        void handleItem(String name) {
            items.add(name);
        }

        public List<String> getItems() {
            return items;
        }
    }
}
