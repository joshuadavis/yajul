/**
 * 
 */
package org.wikimodel.wem.examples;

import java.io.Reader;
import java.io.StringReader;

import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.jspwiki.JspWikiParser;
import org.wikimodel.wem.xwiki.XWikiSerializer;

/**
 * @author kotelnikov
 */
public class TransformationExample {

    public static void main(String[] args) throws WikiParserException {
        Reader reader = new StringReader(""
            + "!!! Hello, world\n"
            + "* list item 1\n"
            + "* list item 2\n"
            + "\n"
            + "||Table header|Table cell\n"
            + "||Table header|Table cell\n"
            + "\n"
            + "Paragraph...");

        IWikiParser parser = new JspWikiParser();
        IWikiPrinter printer = new IWikiPrinter() {
            public void print(String str) {
                System.out.print(str);
            }

            public void println(String str) {
                System.out.println(str);
            }
        };
        IWemListener serializer = new XWikiSerializer(printer);
        parser.parse(reader, serializer);
    }

}
