/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem;

/**
 * @author MikhailKotelnikov
 */
public interface IWemListener {

    void beginDefinitionDescription();

    void beginDefinitionList(WikiParameters params);

    void beginDefinitionTerm();

    void beginDocument();

    void beginFormat(WikiFormat format);

    void beginHeader(int level, WikiParameters params);

    void beginInfoBlock(char infoType, WikiParameters params);

    void beginList(WikiParameters params, boolean ordered);

    void beginListItem();

    void beginParagraph(WikiParameters params);

    void beginPropertyBlock(String propertyUri, boolean doc);

    void beginPropertyInline(String str);

    void beginQuotation(WikiParameters params);

    void beginQuotationLine();

    void beginTable(WikiParameters params);

    void beginTableCell(boolean tableHead, WikiParameters params);

    void beginTableRow(WikiParameters params);

    void endDefinitionDescription();

    void endDefinitionList(WikiParameters params);

    void endDefinitionTerm();

    void endDocument();

    void endFormat(WikiFormat format);

    void endHeader(int level, WikiParameters params);

    void endInfoBlock(char infoType, WikiParameters params);

    void endList(WikiParameters params, boolean ordered);

    void endListItem();

    void endParagraph(WikiParameters params);

    void endPropertyBlock(String propertyUri, boolean doc);

    void endPropertyInline(String inlineProperty);

    void endQuotation(WikiParameters params);

    void endQuotationLine();

    void endTable(WikiParameters params);

    void endTableCell(boolean tableHead, WikiParameters params);

    void endTableRow(WikiParameters params);

    void onEmptyLines(int count);

    void onEscape(String str);

    void onExtensionBlock(String extensionName, WikiParameters params);

    void onExtensionInline(String extensionName, WikiParameters params);

    void onHorizontalLine();

    void onLineBreak();

    void onMacroBlock(String macroName, WikiParameters params, String content);

    void onMacroInline(String macroName, WikiParameters params, String content);

    void onNewLine();

    void onReference(String ref, boolean explicitLink);

    void onSpace(String str);

    void onSpecialSymbol(String str);

    void onTableCaption(String str);

    void onVerbatimBlock(String str);

    void onVerbatimInline(String str);

    void onWord(String str);

}
