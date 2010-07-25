package org.wikimodel.wem.test;

import junit.framework.TestCase;

import org.wikimodel.wem.impl.WikiScannerUtil;

/**
 * @author MikhailKotelnikov
 */
public class WikiScannerUtilTest extends TestCase {

    /**
     * @param name
     */
    public WikiScannerUtilTest(String name) {
        super(name);
    }

    /**
     * 
     */
    public void testSubstringExtract() {
        testSubstringExtract1("123", "");
        testSubstringExtract1("123()", "");
        testSubstringExtract1("()", "");
        testSubstringExtract1("(abc)", "abc");
        testSubstringExtract1("123(abc)456", "abc");
        testSubstringExtract1("123(a\\(b\\)c)456", "a(b)c");

        testSubstringExtract2("123{{}}", "");
        testSubstringExtract2("{{}}", "");
        testSubstringExtract2("{{abc}}", "abc");
        testSubstringExtract2("123{{abc}}456", "abc");
        testSubstringExtract2("123{{a\\(b\\)c}}456", "a(b)c");
        testSubstringExtract2("123{{a\\{{b\\}}c}}456", "a{{b}}c");
    }

    private void testSubstringExtract1(String str, String result) {
        String test = WikiScannerUtil.extractSubstring(str, "(", ")", '\\');
        assertEquals(result, test);
    }

    private void testSubstringExtract2(String str, String result) {
        String test = WikiScannerUtil.extractSubstring(str, "{{", "}}", '\\');
        assertEquals(result, test);
    }

}
