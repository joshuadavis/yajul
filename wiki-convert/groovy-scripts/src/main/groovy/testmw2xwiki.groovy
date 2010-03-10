import org.wikimodel.wem.mediawiki.MediaWikiParser
import org.wikimodel.wem.xwiki.XWikiSerializer
import org.wikimodel.wem.*

class XWiki20Serializer extends XWikiSerializer
{
  def XWiki20Serializer(IWikiPrinter printer)
  {
    super(printer);
  }

  def void beginHeader(int level, WikiParameters params)
  {
    print "\n" + getHeader(level) + " ";
  }

  private String getHeader(int level) {
    def sb = new StringBuilder()
    for (i in 0..level)
    { sb.append("=") }
    sb.toString()
  }
  def void endHeader(int level, WikiParameters params)
  {
    println getHeader(level);
  }

  def void beginFormat(WikiFormat format)
  {
    def fmt = getFormatWikitext(format)
    if (fmt != null && fmt.length() > 0)
      print fmt
  }

  String getFormatWikitext(WikiFormat format)
  {
    def sb = new StringBuffer();
    if (format.hasStyle(IWemConstants.STRONG)) sb.append "**"
    if (format.hasStyle(IWemConstants.EM)) sb.append "//"
    sb.toString()
  }

  def void endFormat(WikiFormat format)
  {
    def fmt = getFormatWikitext(format)
    if (fmt != null && fmt.length() > 0)
      print fmt
  }

  public void onReference(String ref, boolean explicitLink)
  {
    if (ref.indexOf("Image") == 0)
      print("[[image:" + ref.substring(5) + "]]");
    else if (ref.indexOf("Category:") == 0)
    {
      // Ignore categories for now.  Maybe they can be tags.  
    }
    else
    {
      int index = ref.indexOf("|");
      if (index > 0)
      {
        String label = ref.substring(index + 1);
        String link = ref.substring(0, index);
        link = cleanLink(link);
        print("[[" + label + ">>" + link + "]]");
      }
      else
      {
        print("[[" + ref + ">>" + cleanLink(ref) + "]]");
      }
    }
  }

  private String cleanLink(String link)
  {
    return link.replaceAll(" ", "_")
  }

  def void onVerbatimInline(String str)
  {
    println '{{code}}'
    println str
    println '{{/code}}'
  }
}

/**
 * TODO: Add class level comments.
 * <br>
 * User: josh
 * Date: Mar 10, 2010
 * Time: 3:07:08 PM
 */
reader = new StringReader("[[Category:Something]]\n" +
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
        "* three\n" +
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
parser = new MediaWikiParser();
buffer = new StringBuffer()
printer = new WikiPrinter(buffer)
listener = new XWiki20Serializer(printer);
//listener = new EventDumpListener(printer);
parser.parse(reader, listener);
println "***** Dump *****"
println buffer.toString()
