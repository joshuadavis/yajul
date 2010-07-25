package org.wikimodel.wem.jspwiki;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiParameters;

/**
 * Not ready yet
 * @author voelkel
 *
 */
public class JspWikiSerializer implements IWemListener {

	private StringBuffer fBuffer;
	
	public JspWikiSerializer() {
		
	}
	
	public JspWikiSerializer(StringBuffer buf) {
        fBuffer = buf;
    }
	 
	public void beginDefinitionDescription() {
		// TODO Auto-generated method stub

	}

	public void beginDefinitionList(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void beginDefinitionTerm() {
		// TODO Auto-generated method stub

	}

	public void beginDocument() {
		// TODO Auto-generated method stub

	}

	public void beginFormat(WikiFormat format) {
		// TODO Auto-generated method stub

	}

	public void beginHeader(int level, WikiParameters params) {
		println(getEol());
		for (int i=0; i < level; i++) {
		 print("!");	
		}
		print(" ");

	}

	public void beginInfoBlock(char infoType, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void beginList(WikiParameters params, boolean ordered) {
		// TODO Auto-generated method stub

	}

	public void beginListItem() {
		print("* ");

	}

	public void beginParagraph(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void beginPropertyBlock(String propertyUri, boolean doc) {
		// TODO Auto-generated method stub

	}

	public void beginPropertyInline(String str) {
		// TODO Auto-generated method stub

	}

	public void beginQuotation(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void beginQuotationLine() {
		// TODO Auto-generated method stub

	}

	public void beginTable(WikiParameters params) {
	}

	public void beginTableCell(boolean tableHead, WikiParameters params) {
		if (tableHead)
			print("||");
		else
			print("|");
	}

	public void beginTableRow(WikiParameters params) {
		println("");
	}

	public void endDefinitionDescription() {
		// TODO Auto-generated method stub

	}

	public void endDefinitionList(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void endDefinitionTerm() {
		// TODO Auto-generated method stub

	}

	public void endDocument() {
		// TODO Auto-generated method stub

	}

	public void endFormat(WikiFormat format) {
		// TODO Auto-generated method stub

	}

	public void endHeader(int level, WikiParameters params) {
		println("");
		println("");

	}

	public void endInfoBlock(char infoType, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void endList(WikiParameters params, boolean ordered) {
		// TODO Auto-generated method stub

	}

	public void endListItem() {
		println("");

	}

	public void endParagraph(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void endPropertyBlock(String propertyUri, boolean doc) {
		// TODO Auto-generated method stub

	}

	public void endPropertyInline(String inlineProperty) {
		// TODO Auto-generated method stub

	}

	public void endQuotation(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void endQuotationLine() {
		// TODO Auto-generated method stub

	}

	public void endTable(WikiParameters params) {
	}

	public void endTableCell(boolean tableHead, WikiParameters params) {
		if(tableHead)
			print("||");
		else
			print("|");
	}

	public void endTableRow(WikiParameters params) {
		println("");
	}

	public void onEscape(String str) {
		// TODO Auto-generated method stub

	}

	public void onExtensionBlock(String extensionName, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void onExtensionInline(String extensionName, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	public void onHorizontalLine() {
		println("----");

	}

	public void onLineBreak() {
		println("");
		println("");

	}

	public void onNewLine() {
		println("");

	}

	public void onReference(String ref, boolean explicitLink) {
		if (ref.indexOf("Image") == 0 )
			print("{image"+ref.substring(5)+"}");
		else {
			int index = ref.indexOf("|");
			if (index > 0) {
				String label = ref.substring(index+1);
				String link = ref.substring(0, index);
				link = link.replaceAll(" ","_");
				print("["+label+">"+link+"]");
			} else {
				ref = ref.replaceAll(" ","_");
				print("["+ref+"]");
			}
				
		}
		

	}

	public void onSpace(String str) {
		print(str);

	}

	public void onSpecialSymbol(String str) {
		print(str);

	}

	public void onTableCaption(String str) {
		println(str);

	}

	public void onVerbatimBlock(String str) {
		print("{{{"+str+"}}}");
	}

	public void onVerbatimInline(String str) {
		println("{{{"+str+"}}}");
	}

	public void onWord(String str) {
		print(str);
	}
	
    protected void print(String str) {
        if (fBuffer != null) {
            fBuffer.append(str);
        } else {
            System.out.print(str);
        }
    }

    protected void println(String str) {
        if (fBuffer != null) {
            fBuffer.append(str);
            String eol = getEol();
            fBuffer.append(eol);
        } else {
            System.out.println(str);
        }
    }

    protected String getEol() {
        return "\n";
    }

	public void onMacro(String macroName, WikiParameters params, String content) {
		// TODO Auto-generated method stub
		
	}

	public void onEmptyLines(int count) {
		// TODO Auto-generated method stub
		
	}

	public void onMacroBlock(String macroName, WikiParameters params,
			String content) {
		// TODO Auto-generated method stub
		
	}

	public void onMacroInline(String macroName, WikiParameters params,
			String content) {
		// TODO Auto-generated method stub
		
	}
}
