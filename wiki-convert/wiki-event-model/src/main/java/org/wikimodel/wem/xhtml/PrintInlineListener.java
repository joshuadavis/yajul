package org.wikimodel.wem.xhtml;

import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.PrintTextListener;
import org.wikimodel.wem.ReferenceHandler;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiPageUtil;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.util.WikiEntityUtil;

/**
 * @author MikhailKotelnikov
 */
public class PrintInlineListener extends PrintTextListener {

    /**
     * 
     */
    public PrintInlineListener(IWikiPrinter printer) {
        super(printer);
    }

    /**
     * @see org.wikimodel.wem.IWemListener#beginFormat(org.wikimodel.wem.WikiFormat)
     */
    public void beginFormat(WikiFormat format) {
        print(format.getTags(true));
    }

    /**
     * @see org.wikimodel.wem.IWemListener#beginPropertyInline(java.lang.String)
     */
    public void beginPropertyInline(String str) {
        print("<span class='property' url='"
            + WikiPageUtil.escapeXmlAttribute(str)
            + "'>");
    }

    /**
     * @see org.wikimodel.wem.IWemListener#endFormat(org.wikimodel.wem.WikiFormat)
     */
    public void endFormat(WikiFormat format) {
        print(format.getTags(false));
    }

    /**
     * @see org.wikimodel.wem.IWemListener#endPropertyInline(java.lang.String)
     */
    public void endPropertyInline(String inlineProperty) {
        print("</span>");
    }

    /**
     * Returns an HTML/XML entity corresponding to the specified special symbol.
     * Depending on implementation it can be real entities (like &amp;amp;
     * &amp;lt; &amp;gt; or the corresponding digital codes (like &amp;#38;,
     * &amp;#&amp;#38; or &amp;#8250;). Digital entity representation is better
     * for generation of XML files.
     * 
     * @param str the special string to convert to an HTML/XML entity
     * @return an HTML/XML entity corresponding to the specified special symbol.
     */
    protected String getSymbolEntity(String str) {
        String entity = null;
        if (isHtmlEntities()) {
            entity = WikiEntityUtil.getHtmlSymbol(str);
        } else {
            int code = WikiEntityUtil.getHtmlCodeByWikiSymbol(str);
            if (code > 0) {
                entity = "#" + Integer.toString(code);
            }
        }
        if (entity != null) {
            entity = "&" + entity + ";";
            if (str.startsWith(" --")) {
                entity = "&nbsp;" + entity + " ";
            }
        }
        return entity;
    }

    /**
     * Returns <code>true</code> if special Wiki entities should be
     * represented as the corresponding HTML entities or they should be
     * visualized using the corresponding XHTML codes (like &amp;amp; and so
     * on). This method can be overloaded in subclasses to re-define the
     * visualization style.
     * 
     * @return <code>true</code> if special Wiki entities should be
     *         represented as the corresponding HTML entities or they should be
     *         visualized using the corresponding XHTML codes (like &amp;amp;
     *         and so on).
     */
    protected boolean isHtmlEntities() {
        return true;
    }

    @Override
    protected ReferenceHandler newReferenceHandler() {
        return new ReferenceHandler() {

            @Override
            protected void handleImage(String ref, String label) {
                print("<img src='"
                    + WikiPageUtil.escapeXmlAttribute(ref)
                    + "' title='"
                    + WikiPageUtil.escapeXmlAttribute(label)
                    + "'/>");
            }

            @Override
            protected void handleReference(String ref, String label) {
                print("<a href='"
                    + WikiPageUtil.escapeXmlAttribute(ref)
                    + "'>"
                    + WikiPageUtil.escapeXmlString(label)
                    + "</a>");
            }

        };
    }

    /**
     * @see org.wikimodel.wem.IWemListener#onEscape(java.lang.String)
     */
    public void onEscape(String str) {
        print("<span class='escaped'>"
            + WikiPageUtil.escapeXmlString(str)
            + "</span>");
    }

    public void onExtensionInline(String extensionName, WikiParameters params) {
        print("<span class='extension' extension='"
            + extensionName
            + "'"
            + params
            + "/>");
    }

    /**
     * @see org.wikimodel.wem.IWemListener#onLineBreak()
     */

    public void onLineBreak() {
        print("<br />");
    }

    /**
     * @see org.wikimodel.wem.IWemListener#onSpecialSymbol(java.lang.String)
     */
    public void onSpecialSymbol(String str) {
        String entity = getSymbolEntity(str);
        if (entity == null) {
            entity = WikiPageUtil.escapeXmlString(str);
        }
        print(entity);
    }

    /**
     * @see org.wikimodel.wem.IWemListener#onVerbatimInline(java.lang.String)
     */
    public void onVerbatimInline(String str) {
        print("<code>" + WikiPageUtil.escapeXmlString(str) + "</code>");
    }

}
