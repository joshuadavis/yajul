package org.wikimodel.wem.xwiki;

import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.PrintTextListener;
import org.wikimodel.wem.WikiParameters;

public class XWikiSerializer extends PrintTextListener {

    private boolean fNewTableRow;

    public XWikiSerializer(IWikiPrinter printer) {
        super(printer);
    }

    public void beginHeader(int level, WikiParameters params) {
        print("1");
        for (int i = 0; i < level - 1; i++) {
            print(".1");
        }
        print(" ");
    }

    public void beginListItem() {
        print("* ");
    }

    public void beginTable(WikiParameters params) {
        println("{table}");
    }

    @Override
    public void beginTableCell(boolean tableHead, WikiParameters params) {
        if (!fNewTableRow) {
            print("|");
        }
        fNewTableRow = false;
    }

    @Override
    public void beginTableRow(WikiParameters params) {
        fNewTableRow = true;
    }

    public void endHeader(int level, WikiParameters params) {
        println();
        println();
    }

    public void endList(WikiParameters params, boolean ordered) {
        println();
    }

    public void endListItem() {
        println();
    }

    @Override
    public void endParagraph(WikiParameters params) {
        println();
        println();
    }

    public void endTable(WikiParameters params) {
        println("{table}");
        println();
    }

    public void endTableRow(WikiParameters params) {
        println();
    }

    protected String getEol() {
        return "\n";
    }

    public void onHorizontalLine() {
        println("----");

    }

    public void onLineBreak() {
        println();
        println();
    }

    public void onReference(String ref, boolean explicitLink) {
        if (ref.indexOf("Image") == 0)
            print("{image" + ref.substring(5) + "}");
        else {
            int index = ref.indexOf("|");
            if (index > 0) {
                String label = ref.substring(index + 1);
                String link = ref.substring(0, index);
                link = link.replaceAll(" ", "_");
                print("[" + label + ">" + link + "]");
            } else {
                ref = ref.replaceAll(" ", "_");
                print("[" + ref + "]");
            }

        }
    }

    public void onSpecialSymbol(String str) {
        print(str);
    }

    public void onTableCaption(String str) {
        println(str);
    }

    public void onVerbatimInline(String str) {
        println("{code}");
        println(str);
        println("{code}");

    }

}
