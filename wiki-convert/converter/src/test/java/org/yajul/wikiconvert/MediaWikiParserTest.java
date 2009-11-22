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
        reader = new StringReader(
        "__NOTOC__\n" +
                "<big>Welcome to the FooCo Wiki</big>\n" +
                "* Please '''[[Special:Userlogin|register]] and then [[Special:Preferences|confirm your e-mail address]]''' if you haven't already done so.\n" +
                "* Consult the [http://meta.wikimedia.org/wiki/Help:Contents User's Guide] for information on using the wiki software.\n" +
                "* '''NEW!! Use the [http://search.fooco.com/cgi-bin/search.cgi Intranet Search]''' at left to find wiki pages and other things on our intranet.  Use the ''Search box on the side below'' to search for recently created wiki pages.  See the '''[[Special:Categories|Categories]]''' page for a list of our wiki categories.  When browsing also have a look at the category links at the bottom of each wiki page.\n" +
                "<hr>\n" +
                "{|cellspacing=\"10\" cellpadding=\"5\"\n" +
                "|-\n" +
                "|valign=\"top\" width=\"50%\"|\n" +
                "<h3>FooCo Links</h3>\n" +
                "* Main corporate web site<br>http://www.fooco.com\n" +
                "* [http://svn.fooco.com/something/doc/API-3.3.pdf API Doc (v 3.3)]\n" +
                "* [[Developer_Page|Developer Page]]\n" +
                "* '''[[Contact List]]''' and [[Operations Coverage]]\n" +
                "* [[Link Archive|more ...]]\n" +
                "\n" +
                "<h4>Announcements</h4>\n" +
                "* '''2009-10-19''' Everybody wins!\n" +
                "* '''2009-08-10''' Started.\n" +
                "* [[Announcement Archive|more ...]]\n" +
                "\n" +
                "<h4> Wiki Configuration </h4>\n" +
                "* See [[MediaWiki]]\n" +
                "|valign=\"top\"|\n" +
                "\n" +
                "<h3>Recent updates</h3>\n" +
                "{{Special:NewestPagesBlog/order=updated/limit=20}}\n" +
                "[[Special:NewestPagesBlog|More...]]\n" +
                "|}");
        parser.parse(reader,listener);
    }
}
