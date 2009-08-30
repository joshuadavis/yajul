package org.yajul.fix.test;

import junit.framework.TestCase;
import org.yajul.fix.dictionary.Dictionary;
import org.yajul.fix.dictionary.DictionaryLoader;

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 9:30:33 AM
 */
public class DictionaryTest extends TestCase {
    public void testDictionaryLoader() throws Exception {
        Dictionary d40 = DictionaryLoader.load("FIX40.xml");
        Dictionary d44 = DictionaryLoader.load("FIX44.xml");
    }
}
