package org.yajul.wikiconvert;

import junit.framework.TestCase;
import org.wikimodel.wem.mediawiki.MediaWikiParser;
import org.wikimodel.wem.IWemListener;

import java.io.StringReader;

/**
 * Test for the MediaWiki parser.
 * <br/>
 * User: Josh
 * Date: Nov 16, 2009
 * Time: 10:44:48 PM
 */
public class MediaWikiParserTest extends TestCase
{
    public void testBasicSyntax() throws Exception
    {
        MediaWikiParser parser = new MediaWikiParser();
        StringReader reader = new StringReader("[[Category:Something]]\n" +
                ":''This is something''\n" +
                "== Overview ==\n" +
                "\n" +
                "This is the overview.  I hope you like it.\n" +
                "\n" +
                "=== First Detail ===\n" +
                "\n" +
                "Some details (1).\n" +
                "\n" +
                "=== Second Detail ===\n" +
                "\n" +
                "Some more details:\n" +
                "* one\n" +
                "* two\n" +
                "* three\n"
           );

        IWemListener listener = new MockListener();
        parser.parse(reader,listener);
    }
}
