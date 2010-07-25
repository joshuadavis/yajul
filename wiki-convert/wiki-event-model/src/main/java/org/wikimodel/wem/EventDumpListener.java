/**
 * 
 */
package org.wikimodel.wem;

/**
 * This is a dump listener generating text traces of events for every listener
 * call.
 * 
 * @author kotelnikov
 */
public class EventDumpListener extends PrintTextListener {

    private int fDepth;

    /**
     * @param printer
     */
    public EventDumpListener(IWikiPrinter printer) {
        super(printer);
    }

    @Override
    public void beginDefinitionDescription() {
        println("beginDefinitionDescription()");
        inc();
    }

    @Override
    public void beginDefinitionList(WikiParameters params) {
        println("beginDefinitionList([" + params + "])");
        inc();
    }

    @Override
    public void beginDefinitionTerm() {
        println("beginDefinitionTerm()");
        inc();
    }

    @Override
    public void beginDocument() {
        println("beginDocument()");
        inc();
    }

    @Override
    public void beginFormat(WikiFormat format) {
        println("beginFormat(" + format + ")");
        inc();
    }

    @Override
    public void beginHeader(int level, WikiParameters params) {
        println("beginHeader(" + level + ",[" + params + "])");
        inc();
    }

    @Override
    public void beginInfoBlock(char infoType, WikiParameters params) {
        println("beginInfoBlock(" + infoType + ",[" + params + "])");
        inc();
    }

    @Override
    public void beginList(WikiParameters params, boolean ordered) {
        println("beginList([" + params + "], ordered=" + ordered + ")");
        inc();
    }

    @Override
    public void beginListItem() {
        println("beginListItem()");
        inc();
    }

    @Override
    public void beginParagraph(WikiParameters params) {
        println("beginParagraph([" + params + "])");
        inc();
    }

    @Override
    public void beginPropertyBlock(String propertyUri, boolean doc) {
        println("beginPropertyBlock('" + propertyUri + "',doc=" + doc + ")");
        inc();
    }

    @Override
    public void beginPropertyInline(String str) {
        println("beginPropertyInline('" + str + "')");
        inc();
    }

    @Override
    public void beginQuotation(WikiParameters params) {
        println("beginQuotation([" + params + "])");
        inc();
    }

    @Override
    public void beginQuotationLine() {
        println("beginQuotationLine()");
        inc();
    }

    @Override
    public void beginTable(WikiParameters params) {
        println("beginTable([" + params + "])");
        inc();
    }

    @Override
    public void beginTableCell(boolean tableHead, WikiParameters params) {
        println("beginTableCell(" + tableHead + ", [" + params + "])");
        inc();
    }

    @Override
    public void beginTableRow(WikiParameters params) {
        println("beginTableRow([" + params + "])");
        inc();
    }

    private void dec() {
        fDepth--;
    }

    @Override
    protected void endBlock() {
        dec();
        println("endBlock()");
    }

    @Override
    public void endDefinitionDescription() {
        dec();
        println("endDefinitionDescription()");
    }

    @Override
    public void endDefinitionList(WikiParameters params) {
        dec();
        println("endDefinitionList([" + params + "])");
    }

    @Override
    public void endDefinitionTerm() {
        dec();
        println("endDefinitionTerm()");
    }

    @Override
    public void endDocument() {
        dec();
        println("endDocument()");
    }

    @Override
    public void endFormat(WikiFormat format) {
        dec();
        println("endFormat(" + format + ")");
    }

    @Override
    public void endHeader(int level, WikiParameters params) {
        dec();
        println("endHeader(" + level + ", [" + params + "])");
    }

    @Override
    public void endInfoBlock(char infoType, WikiParameters params) {
        dec();
        println("endInfoBlock(" + infoType + ", [" + params + "])");
    }

    @Override
    public void endList(WikiParameters params, boolean ordered) {
        dec();
        println("endList([" + params + "], ordered=" + ordered + ")");
    }

    @Override
    public void endListItem() {
        dec();
        println("endListItem()");
    }

    @Override
    public void endParagraph(WikiParameters params) {
        dec();
        println("endParagraph([" + params + "])");
    }

    @Override
    public void endPropertyBlock(String propertyUri, boolean doc) {
        dec();
        println("endPropertyBlock('" + propertyUri + "', doc=" + doc + ")");
    }

    @Override
    public void endPropertyInline(String inlineProperty) {
        dec();
        println("endPropertyInline('" + inlineProperty + "')");
    }

    @Override
    public void endQuotation(WikiParameters params) {
        dec();
        println("endQuotation([" + params + "])");
    }

    @Override
    public void endQuotationLine() {
        dec();
        println("endQuotationLine()");
    }

    @Override
    public void endTable(WikiParameters params) {
        dec();
        println("endTable([" + params + "])");
    }

    @Override
    public void endTableCell(boolean tableHead, WikiParameters params) {
        dec();
        println("endTableCell(" + tableHead + ", [" + params + "])");
    }

    @Override
    public void endTableRow(WikiParameters params) {
        dec();
        println("endTableRow([" + params + "])");
    }

    private void inc() {
        fDepth++;
    }

    @Override
    public void onEmptyLines(int count) {
        println("onEmptyLines(" + count + ")");
    }

    @Override
    public void onEscape(String str) {
        println("onEscape('" + str + "')");
    }

    @Override
    public void onExtensionBlock(String extensionName, WikiParameters params) {
        println("onExtensionBlock('" + extensionName + "', [" + params + "])");
    }

    @Override
    public void onExtensionInline(String extensionName, WikiParameters params) {
        println("onExtensionInline('" + extensionName + "', [" + params + "])");
    }

    @Override
    public void onHorizontalLine() {
        println("onHorizontalLine()");
    }

    @Override
    public void onLineBreak() {
        println("onLineBreak()");
    }

    @Override
    public void onMacroBlock(
        String macroName,
        WikiParameters params,
        String content) {
        println("onMacroBlock('"
            + macroName
            + "', "
            + params
            + ", '"
            + content
            + "')");
    }

    @Override
    public void onMacroInline(
        String macroName,
        WikiParameters params,
        String content) {
        println("onMacroInline('"
            + macroName
            + "', "
            + params
            + ", '"
            + content
            + "')");
    }

    @Override
    public void onNewLine() {
        println("onNewLine()");
    }

    @Override
    public void onReference(String ref, boolean explicitLink) {
        println("onReference('" + ref + "')");
    }

    @Override
    public void onSpace(String str) {
        println("onSpace('" + str + "')");
    }

    @Override
    public void onSpecialSymbol(String str) {
        println("onSpecialSymbol('" + str + "')");
    }

    @Override
    public void onTableCaption(String str) {
        println("onTableCaption('" + str + "')");
    }

    @Override
    public void onVerbatimBlock(String str) {
        println("onVerbatimBlock('" + str + "')");
    }

    @Override
    public void onVerbatimInline(String str) {
        println("onVerbatimInline('" + str + "')");
    }

    @Override
    public void onWord(String str) {
        println("onWord('" + str + "')");
    }

    @Override
    protected void println(String str) {
        for (int i = 0; i < fDepth; i++) {
            super.print("    ");
        }
        super.println(str);
    }

}
