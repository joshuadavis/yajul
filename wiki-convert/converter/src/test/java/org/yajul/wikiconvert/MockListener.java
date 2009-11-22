package org.yajul.wikiconvert;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fake listener.
 * <br/>
 * User: Josh
 * Date: Nov 16, 2009
 * Time: 10:50:15 PM
 */
public class MockListener implements IWemListener
{
    private static final Logger log = LoggerFactory.getLogger(MockListener.class);

    public void beginDefinitionDescription()
    {
        if (log.isDebugEnabled())
           log.debug("beginDefinitionDescription() :");
    }

    public void beginDefinitionList(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginDefinitionList() :");
    }

    public void beginDefinitionTerm()
    {
        if (log.isDebugEnabled())
           log.debug("beginDefinitionTerm() :");
    }

    public void beginDocument()
    {
        if (log.isDebugEnabled())
           log.debug("beginDocument() :");
    }

    public void beginFormat(WikiFormat format)
    {
        if (log.isDebugEnabled())
           log.debug("beginFormat() :");
    }

    public void beginHeader(int level, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginHeader() :");
    }

    public void beginInfoBlock(char infoType, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginInfoBlock() :");
    }

    public void beginList(WikiParameters params, boolean ordered)
    {
        if (log.isDebugEnabled())
           log.debug("beginList() :");
    }

    public void beginListItem()
    {
        if (log.isDebugEnabled())
           log.debug("beginListItem() :");
    }

    public void beginParagraph(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginParagraph() :");
    }

    public void beginPropertyBlock(String propertyUri, boolean doc)
    {
        if (log.isDebugEnabled())
           log.debug("beginPropertyBlock() :");
    }

    public void beginPropertyInline(String str)
    {
        if (log.isDebugEnabled())
           log.debug("beginPropertyInline() :");
    }

    public void beginQuotation(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginQuotation() :");
    }

    public void beginQuotationLine()
    {
        if (log.isDebugEnabled())
           log.debug("beginQuotationLine() :");
    }

    public void beginTable(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginTable() :");
    }

    public void beginTableCell(boolean tableHead, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginTableCell() :");
    }

    public void beginTableRow(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("beginTableRow() :");
    }

    public void endDefinitionDescription()
    {
        if (log.isDebugEnabled())
           log.debug("endDefinitionDescription() :");
    }

    public void endDefinitionList(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endDefinitionList() :");
    }

    public void endDefinitionTerm()
    {
        if (log.isDebugEnabled())
           log.debug("endDefinitionTerm() :");
    }

    public void endDocument()
    {
        if (log.isDebugEnabled())
           log.debug("endDocument() :");
    }

    public void endFormat(WikiFormat format)
    {
        if (log.isDebugEnabled())
           log.debug("endFormat() :");
    }

    public void endHeader(int level, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endHeader() :");
    }

    public void endInfoBlock(char infoType, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endInfoBlock() :");
    }

    public void endList(WikiParameters params, boolean ordered)
    {
        if (log.isDebugEnabled())
           log.debug("endList() :");
    }

    public void endListItem()
    {
        if (log.isDebugEnabled())
           log.debug("endListItem() :");
    }

    public void endParagraph(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endParagraph() :");
    }

    public void endPropertyBlock(String propertyUri, boolean doc)
    {
        if (log.isDebugEnabled())
           log.debug("endPropertyBlock() :");
    }

    public void endPropertyInline(String inlineProperty)
    {
        if (log.isDebugEnabled())
           log.debug("endPropertyInline() :");
    }

    public void endQuotation(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endQuotation() :");
    }

    public void endQuotationLine()
    {
        if (log.isDebugEnabled())
           log.debug("endQuotationLine() :");
    }

    public void endTable(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endTable() :");
    }

    public void endTableCell(boolean tableHead, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endTableCell() :");
    }

    public void endTableRow(WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("endTableRow() :");
    }

    public void onEmptyLines(int count)
    {
        if (log.isDebugEnabled())
           log.debug("onEmptyLines() :");
    }

    public void onEscape(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onEscape() :");
    }

    public void onExtensionBlock(String extensionName, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("onExtensionBlock() :");
    }

    public void onExtensionInline(String extensionName, WikiParameters params)
    {
        if (log.isDebugEnabled())
           log.debug("onExtensionInline() :");
    }

    public void onHorizontalLine()
    {
        if (log.isDebugEnabled())
           log.debug("onHorizontalLine() :");
    }

    public void onLineBreak()
    {
        if (log.isDebugEnabled())
           log.debug("onLineBreak() :");
    }

    public void onMacroBlock(String macroName, WikiParameters params, String content)
    {
        if (log.isDebugEnabled())
           log.debug("onMacroBlock() :");
    }

    public void onMacroInline(String macroName, WikiParameters params, String content)
    {
        if (log.isDebugEnabled())
           log.debug("onMacroInline() :");
    }

    public void onNewLine()
    {
        if (log.isDebugEnabled())
           log.debug("onNewLine() :");
    }

    public void onTableCaption(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onTableCaption() :");
    }

    public void onVerbatimBlock(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onVerbatimBlock() :");
    }

    public void onVerbatimInline(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onVerbatimInline() :");
    }

    public void onSpecialSymbol(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onSpecialSymbol() : '" + str + "'");
    }


    public void onReference(String ref, boolean explicitLink)
    {
        if (log.isDebugEnabled())
           log.debug("onReference() : '" + ref + "'");
    }

    public void onSpace(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onSpace() : '" + str + "'");
    }

    public void onWord(String str)
    {
        if (log.isDebugEnabled())
           log.debug("onWord() : '" + str + "'");
    }
}
