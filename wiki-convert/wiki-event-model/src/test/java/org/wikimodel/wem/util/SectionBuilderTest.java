/**
 * 
 */
package org.wikimodel.wem.util;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class SectionBuilderTest extends TestCase {

    final StringBuffer fBuf = new StringBuffer();

    SectionBuilder<String> fBuilder;

    /**
     * @param name
     */
    public SectionBuilderTest(String name) {
        super(name);
    }

    private void check(String control) {
        assertEquals(control, fBuf.toString());
    }

    @Override
    protected void setUp() throws Exception {
        ISectionListener<String> listener = new ISectionListener<String>() {

            public void beginLevel(int level, String data) {
                fBuf.append("<level>");
            }

            public void beginSection(int level, String data) {
                fBuf.append("<s>");
            }

            public void beginSectionContent(int level, String data) {
                fBuf.append("<c>");
            }

            public void beginSectionHeader(int level, String data) {
                fBuf.append("<h" + level + ">");
            }

            public void endLevel(int level, String data) {
                fBuf.append("</level>");
            }

            public void endSection(int level, String data) {
                fBuf.append("</s>");
            }

            public void endSectionContent(int level, String data) {
                fBuf.append("</c>");
            }

            public void endSectionHeader(int level, String data) {
                fBuf.append("</h" + level + ">");
            }

        };

        fBuilder = new SectionBuilder<String>(listener);
    }

    public void test() {
        fBuilder.beginDocument();
        check("");

        fBuilder.beginHeader(1, "A");
        check("<level><s><h1>");
        fBuilder.endHeader();
        check("<level><s><h1></h1><c>");

        fBuilder.beginHeader(3, "A");
        check("<level><s><h1></h1><c><level><s><h3>");
        fBuilder.endHeader();
        check("<level><s><h1></h1><c><level><s><h3></h3><c>");

        fBuilder.beginHeader(3, "A");
        check("<level><s><h1></h1><c><level><s><h3></h3><c></c></s><s><h3>");
        fBuilder.endHeader();
        check("<level><s><h1></h1><c><level><s><h3></h3><c></c></s><s><h3></h3><c>");

        fBuilder.endDocument();
        check("<level><s><h1></h1><c>"
            + "<level><s><h3></h3><c></c></s><s><h3></h3><c></c></s></level>"
            + "</c></s>");

    }

}
