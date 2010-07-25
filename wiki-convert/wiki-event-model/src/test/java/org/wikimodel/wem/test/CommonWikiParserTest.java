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
package org.wikimodel.wem.test;

import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.common.CommonWikiParser;

/**
 * @author MikhailKotelnikov
 */
public class CommonWikiParserTest extends AbstractWikiParserTest {

    /**
     * @param name
     */
    public CommonWikiParserTest(String name) {
        super(name);
    }

    @Override
    protected IWikiParser newWikiParser() {
        return new CommonWikiParser();
    }

    public void testComplexFormatting() throws WikiParserException {
        test("%rdf:type toto:Document\r\n"
            + "\r\n"
            + "%title Hello World\r\n"
            + "\r\n"
            + "%summary This is a short description\r\n"
            + "%locatedIn (((\r\n"
            + "    %type [City]\r\n"
            + "    %name [Paris]\r\n"
            + "    %address (((\r\n"
            + "      %building 10\r\n"
            + "      %street Cité Nollez\r\n"
            + "    ))) \r\n"
            + ")))\r\n"
            + "= Hello World =\r\n"
            + "\r\n"
            + "* item one\r\n"
            + "  * sub-item a\r\n"
            + "  * sub-item b\r\n"
            + "    + ordered X \r\n"
            + "    + ordered Y\r\n"
            + "  * sub-item c\r\n"
            + "* item two\r\n"
            + "\r\n"
            + "\r\n"
            + "The table below contains \r\n"
            + "an %seeAlso(embedded document). \r\n"
            + "It can contain the same formatting \r\n"
            + "elements as the root document.\r\n"
            + "\r\n"
            + "\r\n"
            + "!! Table Header 1.1 !! Table Header 1.2\r\n"
            + ":: Cell 2.1 :: Cell 2.2 (((\r\n"
            + "== Embedded document ==\r\n"
            + "This is an embedded document:\r\n"
            + "* item X\r\n"
            + "* item Y\r\n"
            + "))) The text goes after the embedded\r\n"
            + " document\r\n"
            + ":: Cell 3.1 :: Cell 3.2");
        test("----------------------------------------------\r\n"
            + "= Example1 =\r\n"
            + "\r\n"
            + "The table below contains an embedded document.\r\n"
            + "Using such embedded documents you can insert table\r\n"
            + "in a list or a list in a table. And embedded documents\r\n"
            + "can contain their own embedded documents!!!\r\n"
            + "\r\n"
            + "!! Header 1.1 !! Header 1.2\r\n"
            + ":: Cell 2.1 :: Cell 2.2 with an embedded document: (((\r\n"
            + "== This is an embedded document! ==\r\n"
            + "* list item one\r\n"
            + "* list item two\r\n"
            + "  * sub-item A\r\n"
            + "  * sub-item B\r\n"
            + "* list item three\r\n"
            + ")))\r\n"
            + ":: Cell 3.1 :: Cell 3.2\r\n"
            + "\r\n"
            + "This is a paragraphs after the table...\r\n"
            + "----------------------------------------------\r\n"
            + "");
    }

    /**
     * @throws WikiParserException
     */
    public void testDocuments() throws WikiParserException {
        test("before ((( inside ))) after ");
        test("before inside ))) after ");
        test("before (((\ninside ))) after ");
        test("before (((\n inside ))) after ");
        test("| Line One | First doc: (((\n inside ))) after \n"
            + "|Line Two | Second doc: (((lkjlj))) skdjg");
        test("| This is a table: | (((* item one\n"
            + "* item two\n"
            + " * subitem 1\n"
            + " * subitem 2\n"
            + "* item three))) ");

        test("before ((( opened and not closed");
        test("before ((( one ((( two ((( three ");
    }

    /**
     * @throws WikiParserException
     */
    public void testEscape() throws WikiParserException {
        test("[a reference]");
        test("\\[not a reference]");

        test("\\First letter is escaped");
        test("\\[not a reference]");
        test("\\\\escaped backslash");
        test("\\ a line break because it is followed by a space");

        test("= Heading =\n\\= Not a heading =\n= Heading again! =");
    }

    /**
     * @throws WikiParserException
     */
    public void testExtensions() throws WikiParserException {
        // Inline extensions
        test(" $abc ", "<p> <span class='extension' extension='abc'/> </p>");
        test(
            "abc $abc after",
            "<p>abc <span class='extension' extension='abc'/> after</p>");
        test(
            "abc $abc() after",
            "<p>abc <span class='extension' extension='abc'/> after</p>");
        test(
            "abc $abc(a=b c=d) after",
            "<p>abc <span class='extension' extension='abc' a='b' c='d'/> after</p>");
        test(
            "before$abc(hello)after",
            "<p>before<span class='extension' extension='abc' hello=''/>after</p>");

        // Block extensions
        test("$abc", "<div class='extension' extension='abc'/>");
        test("$abc()", "<div class='extension' extension='abc'/>");
        test(
            "$abc(a=b c=d)",
            "<div class='extension' extension='abc' a='b' c='d'/>");
        test("$abc(a)", "<div class='extension' extension='abc' a=''/>");

        test("before\n$abc after", ""
            + "<p>before</p>\n"
            + "<div class='extension' extension='abc'/>\n"
            + "<p> after</p>");
        test("before\n$abc() after", ""
            + "<p>before</p>\n"
            + "<div class='extension' extension='abc'/>\n"
            + "<p> after</p>");
        test("before\n$abc(a=b c=d) after", ""
            + "<p>before</p>\n"
            + "<div class='extension' extension='abc' a='b' c='d'/>\n"
            + "<p> after</p>");
        test("before\n$abc(hello)after", ""
            + "<p>before</p>\n"
            + "<div class='extension' extension='abc' hello=''/>\n"
            + "<p>after</p>");
    }

    /**
     * @throws WikiParserException
     */
    public void testFormats() throws WikiParserException {
        test("*bold* ", "<p><strong>bold</strong> </p>");
        test(" **bold** ", "<p> <strong>bold</strong> </p>");
        test("__italic__", "<p><em>italic</em></p>");

        test("*strong*", "<p><strong>strong</strong></p>");
        test(" *strong*", "<p> <strong>strong</strong></p>");
        test("__em__", "<p><em>em</em></p>");
        test("$$code$$", "<p><code>code</code></p>");
        test("^^sup^^", "<p><sup>sup</sup></p>");
        test("~~sub~~", "<p><sub>sub</sub></p>");

        // These special symbols ("--" and "++") at the begining of the line are
        // interpreted as list markers (see {@link #testLists()} method)
        test("before++big++after", "<p>before<big>big</big>after</p>");
        test("before--small--after", "<p>before<small>small</small>after</p>");

        test("@@ins@@", "<p><ins>ins</ins></p>");
        test("##del##", "<p><del>del</del></p>");

        test(""
            + "before"
            + "*bold*"
            + "__italic__"
            + "^^superscript^^"
            + "~~subscript~~"
            + "value after", "<p>"
            + "before"
            + "<strong>bold</strong>"
            + "<em>italic</em>"
            + "<sup>superscript</sup>"
            + "<sub>subscript</sub>"
            + "value after"
            + "</p>");

        // "Bad-formed" formatting
        test("normal*bold__bold-italic*italic__normal", "<p>"
            + "normal<strong>bold</strong>"
            + "<strong><em>bold-italic</em></strong>"
            + "<em>italic</em>normal"
            + "</p>");

        // Auto-closing (non used) style formatting at the end of lines.
        test("not a bold__", "<p>not a bold</p>");
        test("not an italic__", "<p>not an italic</p>");

        test("text*", "<p>text</p>");
        test("text**", "<p>text</p>");
        test("text__", "<p>text</p>");
        test("text$$", "<p>text</p>");
        test("text^^", "<p>text</p>");
        test("text~~", "<p>text</p>");

    }

    /**
     * @throws WikiParserException
     */
    public void testHeaders() throws WikiParserException {
        test("=Header1=", "<h1>Header1</h1>");
        test("==Header2==", "<h2>Header2</h2>");
        test("===Header3===", "<h3>Header3</h3>");
        test("====Header4====", "<h4>Header4</h4>");
        test("=Header1", "<h1>Header1</h1>");
        test("==Header2", "<h2>Header2</h2>");
        test("===Header3", "<h3>Header3</h3>");
        test("====Header4", "<h4>Header4</h4>");
        test("before\n= Header =\nafter", "<p>before</p>\n"
            + "<h1>Header </h1>\n"
            + "<p>after</p>");

        test("This is not a header: ==", "<p>This is not a header: ==</p>");

        test("{{a=b}}\n=Header1", "<h1 a='b'>Header1</h1>");
    }

    /**
     * @throws WikiParserException
     */
    public void testHorLine() throws WikiParserException {
        test("----", "<hr />");
        test("-------", "<hr />");
        test("-----------", "<hr />");
        test("----\nabc", "<hr />\n<p>abc</p>");
        test("before\n----\nafter", "<p>before</p>\n<hr />\n<p>after</p>");

        // Not lines
        test(" -----------", "<p> &mdash;&mdash;&mdash;</p>");
    }

    /**
     * @throws WikiParserException
     */
    public void testInfo() throws WikiParserException {
        test("/i\\ item {{{formatted block}}} {macro}123{/macro} after");
        test("before\n"
            + "/i\\Information block:\n"
            + "{{{pre\n"
            + "  formatted\n"
            + " block}}} sdlkgj\n"
            + "qsdg\n\n"
            + "after");
        test("/!\\");
        test("/i\\info");
        test("/i\\Information block:\n"
            + "first line\n"
            + "second line\n"
            + "third  line");
        test("{{a=b}}\n/!\\");
        test("{{a=b}}\n/i\\info");
    }

    /**
     * @throws WikiParserException
     */
    public void testLineBreak() throws WikiParserException {
        test("abc\\\ndef");
        test("abc\\  \ndef");
        test("abc\\ x \ndef");
        test("abc x \ndef");
    }

    /**
     * @throws WikiParserException
     */
    public void testLists() throws WikiParserException {
        test(
            "*this is a bold, and not a list",
            "<p><strong>this is a bold, and not a list</strong></p>");
        test("**bold**", "<p><strong>bold</strong></p>");

        test("* first", "<ul>\n  <li>first</li>\n</ul>");
        test(
            "** second",
            "<ul>\n  <li><ul>\n  <li>second</li>\n</ul>\n</li>\n</ul>");

        test("* item one\n"
            + "* item two\n"
            + "*+item three\n"
            + "*+ item four\n"
            + "* item five - first line\n"
            + "   item five - second line\n"
            + "* item six\n"
            + "  is on multiple\n"
            + " lines");

        test(
            "* item {{{formatted block}}} {macro}123{/macro} after",
            "<ul>\n"
                + "  <li>item <pre>formatted block</pre>\n"
                + " <span class='macro' macroName='macro'><![CDATA[123]]></span> after</li>\n</ul>");

        test("? term:  definition");
        test("?just term");
        test(":just definition");
        test(";:just definition");
        test(":just definition");
        test(";:");
        test(": Indenting is stripped out.\r\n"
            + " : Includes double indenting");

        test(";term one: definition one\n"
            + ";term two: definition two\n"
            + ";term three: definition three");
        test(":Term definition");
        test(";:Term definition");

        test(";One,\ntwo,\nbucle my shoes...:\n"
            + "...Three\nfour,\nClose the door\n"
            + ";Five,\nSix: Pick up\n sticks\n\ntam-tam, pam-pam...");

        test(";__term__: *definition*");

        test("this is not a definition --\n"
            + " ;__not__ a term: ''not'' a definition\n"
            + "----toto");

        test("{{a='b'}}\n* item one");
    }

    public void testMacro() throws WikiParserException {
        test(
            "{toto}a{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a]]></pre>");
        test(
            "{toto}a{toto}b{/toto}c{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a{toto}b{/toto}c]]></pre>");
        test("before\n{toto}a{/toto}\nafter", ""
            + "<p>before</p>\n"
            + "<pre class='macro' macroName='toto'><![CDATA[a]]></pre>\n"
            + "<p>after</p>");
        test("before\n{toto}a{/toto}after", ""
            + "<p>before</p>\n"
            + "<pre class='macro' macroName='toto'><![CDATA[a]]></pre>\n"
            + "<p>after</p>");

        // URIs as macro names
        test(
            "{x:toto}a{/x:toto}",
            "<pre class='macro' macroName='x:toto'><![CDATA[a]]></pre>");
        test(
            "{x:toto}a{x:toto}b{/x:toto}c{/x:toto}",
            "<pre class='macro' macroName='x:toto'><![CDATA[a{x:toto}b{/x:toto}c]]></pre>");
        test("before\n{x:toto}a{/x:toto}\nafter", ""
            + "<p>before</p>\n"
            + "<pre class='macro' macroName='x:toto'><![CDATA[a]]></pre>\n"
            + "<p>after</p>");
        test("before\n{x:toto}a{/x:toto}after", ""
            + "<p>before</p>\n"
            + "<pre class='macro' macroName='x:toto'><![CDATA[a]]></pre>\n"
            + "<p>after</p>");

        // Empty macros
        test(
            "{x:toto /}",
            "<pre class='macro' macroName='x:toto'><![CDATA[]]></pre>");
        test(
            "{x:toto a=b c=d /}",
            "<pre class='macro' macroName='x:toto' a='b' c='d'><![CDATA[]]></pre>");
        test(
            "before\n{x:toto  a=b c=d/}\nafter",
            ""
                + "<p>before</p>\n"
                + "<pre class='macro' macroName='x:toto' a='b' c='d'><![CDATA[]]></pre>\n"
                + "<p>after</p>");
        test(
            "before\n{x:toto  a='b' c='d'/}after",
            ""
                + "<p>before</p>\n"
                + "<pre class='macro' macroName='x:toto' a='b' c='d'><![CDATA[]]></pre>\n"
                + "<p>after</p>");
        test(
            "before{x:toto /}after",
            "<p>before<span class='macro' macroName='x:toto'><![CDATA[]]></span>after</p>");

        // Bad-formed block macros (not-closed)
        test("{toto}", "<pre class='macro' macroName='toto'><![CDATA[]]></pre>");
        test(
            "{toto}a{toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a{toto}]]></pre>");

        // 
        test(
            "{toto}a{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a]]></pre>");
        test(
            "before{toto}macro{/toto}after",
            "<p>before<span class='macro' macroName='toto'><![CDATA[macro]]></span>after</p>");

        test("before{toto a=b c=d}toto macro tata {/toto}after", ""
            + "<p>before<span class='macro' macroName='toto' a='b' c='d'>"
            + "<![CDATA[toto macro tata ]]>"
            + "</span>after</p>");

        test(
            "before{toto a=b c=d}toto {x qsdk} macro {sd} tata {/toto}after",
            ""
                + "<p>before<span class='macro' macroName='toto' a='b' c='d'>"
                + "<![CDATA[toto {x qsdk} macro {sd} tata ]]>"
                + "</span>after</p>");

        // Macros in other block elements (tables and lists)
        test("- before\n{code a=b c=d}this is a code{/code}after", ""
            + "<ul>\n"
            + "  <li>before<pre class='macro' macroName='code' a='b' c='d'>"
            + "<![CDATA[this is a code]]></pre>\n"
            + "after</li>\n"
            + "</ul>");
        test("- before{code a=b c=d}this is a code{/code}after", ""
            + "<ul>\n"
            + "  <li>before<span class='macro' macroName='code' a='b' c='d'>"
            + "<![CDATA[this is a code]]></span>after</li>\n"
            + "</ul>");

        // Not a macro
        test("{ toto a=b c=d}", "<p>{ toto a=b c=d}</p>");

        // Macro and its usage
        test(
            "This is a macro: {toto x:a=b x:c=d}\n"
                + "<table>\n"
                + "#foreach ($x in $table)\n"
                + "  <tr>hello, $x</tr>\n"
                + "#end\n"
                + "</table>\n"
                + "{/toto}\n\n"
                + "And this is a usage of this macro: $toto(a=x b=y)",
            "<p>This is a macro: <span class='macro' macroName='toto' x:a='b' x:c='d'><![CDATA[\n"
                + "<table>\n"
                + "#foreach ($x in $table)\n"
                + "  <tr>hello, $x</tr>\n"
                + "#end\n"
                + "</table>\n"
                + "]]></span></p>\n"
                + "<p>And this is a usage of this macro: <span class='extension' extension='toto' a='x' b='y'/></p>");

        test(
            "!!Header:: Cell with a macro: \n"
                + "{code}this is a code{/code} \n"
                + " this is afer the code...",
            ""
                + "<table><tbody>\n"
                + "  <tr><th>Header</th><td> Cell with a macro: "
                + "<pre class='macro' macroName='code'><![CDATA[this is a code]]></pre>\n \n"
                + " this is afer the code&hellip;</td></tr>\n"
                + "</tbody></table>");
        test(
            ""
                + "* item one\n"
                + "* item two\n"
                + "  * subitem with a macro:\n"
                + "  {code} this is a code{/code} \n"
                + "  the same item (continuation)\n"
                + "  * subitem two\n"
                + "* item three",
            ""
                + "<ul>\n"
                + "  <li>item one</li>\n"
                + "  <li>item two<ul>\n"
                + "  <li>subitem with a macro:\n"
                + "  <span class='macro' macroName='code'><![CDATA[ this is a code]]></span> \n"
                + "  the same item (continuation)</li>\n"
                + "  <li>subitem two</li>\n"
                + "</ul>\n"
                + "</li>\n"
                + "  <li>item three</li>\n"
                + "</ul>");

        // Macros with URIs as names
        test(
            "{x:y a=b c=d}",
            "<pre class='macro' macroName='x:y' a='b' c='d'><![CDATA[]]></pre>");
        test(
            "before{x:y a=b c=d}macro content",
            "<p>before<span class='macro' macroName='x:y' a='b' c='d'><![CDATA[macro content]]></span></p>");
        test(
            "before\n{x:y a=b c=d}macro content",
            ""
                + "<p>before</p>\n"
                + "<pre class='macro' macroName='x:y' a='b' c='d'><![CDATA[macro content]]></pre>");
        test(
            "before\n{x:y a=b c=d/}\nafter",
            ""
                + "<p>before</p>\n"
                + "<pre class='macro' macroName='x:y' a='b' c='d'><![CDATA[]]></pre>\n"
                + "<p>after</p>");

        // Not closed and bad-formed macros
        test(
            "a{a}{b}",
            "<p>a<span class='macro' macroName='a'><![CDATA[{b}]]></span></p>");
        test(
            "a{a}{b}{",
            "<p>a<span class='macro' macroName='a'><![CDATA[{b}{]]></span></p>");
        test(
            "a {{x:}} b",
            "<p>a {<span class='macro' macroName='x:'><![CDATA[} b]]></span></p>");
        test(
            "a {{x:}} }b",
            "<p>a {<span class='macro' macroName='x:'><![CDATA[} }b]]></span></p>");

        test(
            "a {{x:}} {}b",
            "<p>a {<span class='macro' macroName='x:'><![CDATA[} {}b]]></span></p>");
        test(
            "a {{x:}}, {{y:}} b",
            "<p>a {<span class='macro' macroName='x:'><![CDATA[}, {{y:}} b]]></span></p>");

    }

    /**
     * @throws WikiParserException
     */
    public void testParagraphs() throws WikiParserException {
        test("{{background='blue'}}", "<p background='blue'></p>");
        test(""
            + "{{background='blue'}}\n"
            + "{{background='red'}}\n"
            + "{{background='green'}}", ""
            + "<p background='blue'></p>\n"
            + "<p background='red'></p>\n"
            + "<p background='green'></p>");
        test(""
            + "{{background='blue'}}first\n"
            + "{{background='red'}}second\n"
            + "{{background='green'}}third", ""
            + "<p background='blue'>first</p>\n"
            + "<p background='red'>second</p>\n"
            + "<p background='green'>third</p>");
        test(""
            + "{{background='blue'}}\nfirst\n"
            + "{{background='red'}}\nsecond\n"
            + "{{background='green'}}\nthird", ""
            + "<p background='blue'>first</p>\n"
            + "<p background='red'>second</p>\n"
            + "<p background='green'>third</p>");

        test("{{background='blue'}}hello", "<p background='blue'>hello</p>");
        test("{{background='blue'}}\n"
            + "First paragraph\r\n"
            + "\r\n"
            + "\r\n"
            + "\r\n"
            + "", ""
            + "<p background='blue'>First paragraph</p>\n"
            + "<div style='height:3em;'></div>");

        test("First paragraph\r\n" + "\r\n" + "\r\n" + "\r\n" + "");
        test("First paragraph.\n"
            + "Second line of the same paragraph.\n"
            + "\n"
            + "The second paragraph");

        test("\n<toto");

    }

    /**
     * @throws WikiParserException
     */
    public void testPropertiesBlock() throws WikiParserException {
        test(
            "%toto hello  world\n123",
            "<div class='property' url='toto'><p>hello  world</p>\n</div>\n<p>123</p>");
        test("%prop1 value1\n%prop2 value2", ""
            + "<div class='property' url='prop1'><p>value1</p>\n</div>\n"
            + "<div class='property' url='prop2'><p>value2</p>\n</div>");
        test("%prop1 value1\nparagraph\n%prop2 value2", ""
            + "<div class='property' url='prop1'><p>value1</p>\n</div>\n"
            + "<p>paragraph</p>\n"
            + "<div class='property' url='prop2'><p>value2</p>\n</div>");

        test("%prop1 (((embedded)))next paragraph\n%prop2 value2", ""
            + "<div class='property' url='prop1'>\n"
            + "<p>embedded</p>\n"
            + "</div>\n"
            + "<p>next paragraph</p>\n"
            + "<div class='property' url='prop2'><p>value2</p>\n"
            + "</div>");
        test(
            "%prop1 (((=Header\n- item 1\n- item 2)))next paragraph\n%prop2 value2",
            ""
                + "<div class='property' url='prop1'>\n"
                + "<h1>Header</h1>\n"
                + "<ul>\n"
                + "  <li>item 1</li>\n"
                + "  <li>item 2</li>\n"
                + "</ul>\n"
                + "</div>\n"
                + "<p>next paragraph</p>\n"
                + "<div class='property' url='prop2'><p>value2</p>\n"
                + "</div>");

        test(
            "before\r\n"
                + "\r\n"
                + "%company (((\r\n"
                + "    %name Cognium Systems\r\n"
                + "    %addr (((\r\n"
                + "        %country [France]\r\n"
                + "        %city [Paris]\r\n"
                + "        %street Cité Nollez\r\n"
                + "        This is just a description...\r\n"
                + "    )))\r\n"
                + ")))\r\n"
                + "\r\n"
                + "after",
            ""
                + "<p>before</p>\n"
                + "<div class='property' url='company'>\n"
                + "<div class='property' url='name'><p>Cognium Systems</p>\n"
                + "</div>\n"
                + "<div class='property' url='addr'>\n"
                + "<div class='property' url='country'><p><a href='France'>France</a></p>\n"
                + "</div>\n"
                + "<div class='property' url='city'><p><a href='Paris'>Paris</a></p>\n"
                + "</div>\n"
                + "<div class='property' url='street'><p>Cité Nollez</p>\n"
                + "</div>\n"
                + "<p>        This is just a description&hellip;</p>\n"
                + "</div>\n"
                + "</div>\n"
                + "<p>after</p>");
        // Bad formed block properties

        // No closing brackets
        test(
            "before\r\n"
                + "\r\n"
                + "%company (((\r\n"
                + "    %name Cognium Systems\r\n"
                + "    %addr (((\r\n"
                + "        %country [France]\r\n"
                + "        %city Paris\r\n"
                + "        %street Cité Nollez\r\n"
                + "        This is just a description...\r\n"
                + "after",
            "<p>before</p>\n"
                + "<div class='property' url='company'>\n"
                + "<div class='property' url='name'><p>Cognium Systems</p>\n"
                + "</div>\n"
                + "<div class='property' url='addr'>\n"
                + "<div class='property' url='country'><p><a href='France'>France</a></p>\n"
                + "</div>\n"
                + "<div class='property' url='city'><p>Paris</p>\n"
                + "</div>\n"
                + "<div class='property' url='street'><p>Cité Nollez</p>\n"
                + "</div>\n"
                + "<p>        This is just a description&hellip;\n"
                + "after</p>\n"
                + "</div>\n"
                + "</div>");
    }

    public void testPropertiesInline() throws WikiParserException {
        test(
            "before %prop(value) after",
            "<p>before <span class='property' url='prop'>value</span> after</p>");
        test(
            "before %foo:bar:toto.com/titi/tata?query=x#ancor(value) after",
            "<p>before <span class='property' url='foo:bar:toto.com/titi/tata?query=x#ancor'>value</span> after</p>");
        test(
            "before %prop(before*bold*__italic__^^superscript^^~~subscript~~value) after",
            "<p>before <span class='property' url='prop'>before<strong>bold</strong><em>italic</em><sup>superscript</sup><sub>subscript</sub>value</span> after</p>");
    }

    /**
     * @throws WikiParserException
     */
    public void testQuot() throws WikiParserException {
        test("Q: Quotation");

        test(">This is a message\n"
            + ">>and this is a response to the message \n"
            + "> This is a continuation of the same message");

        test("This is a paragraph\n"
            + ">and this is a quotations\n"
            + "> the second line");
        test("        This is just a description...\r\n"
            + "    \r\n"
            + "\r\n"
            + "\r\n");
        test("> first\n"
            + ">> second\n"
            + ">> third\n"
            + ">>> subquot1\n"
            + ">>> subquot2\n"
            + ">> fourth");
        test("{{a='b'}}\n"
            + "  first\n"
            + "  second\n"
            + "  third\n"
            + "    subquot1\n"
            + "    subquot2"
            + "  fourth");

    }

    /**
     * @throws WikiParserException
     */
    public void testReferences() throws WikiParserException {
        test(
            "Это (=ссылка=) на внешний документ...",
            "<p>Это <a href='ссылка'>ссылка</a> на внешний документ&hellip;</p>");
        test(
            "Это (=http://www.google.com ссылка=) на внешний документ...",
            "<p>Это <a href='http://www.google.com'>ссылка</a> на внешний документ&hellip;</p>");
        test(
            "This is a (=reference=) to an external document...",
            "<p>This is a <a href='reference'>reference</a> to an external document&hellip;</p>");
        test(
            "This is a (=http://www.google.com reference=) to an external document...",
            "<p>This is a <a href='http://www.google.com'>reference</a> to an external document&hellip;</p>");

        test(
            "before http://www.foo.bar/com after",
            "<p>before <a href='http://www.foo.bar/com'>http://www.foo.bar/com</a> after</p>");
        test(
            "before http://www.foo.bar/com?q=abc#ancor after",
            "<p>before <a href='http://www.foo.bar/com?q=abc#ancor'>http://www.foo.bar/com?q=abc#ancor</a> after</p>");
        test(
            "before wiki:Hello after",
            "<p>before <a href='wiki:Hello'>wiki:Hello</a> after</p>");
        test(
            "before abc:cde#efg after",
            "<p>before <a href='abc:cde#efg'>abc:cde#efg</a> after</p>");
        // Opaque URIs
        test(
            "before first:second:third:anonymous@hello/path/?query=value#ancor after",
            "<p>before <a href='first:second:third:anonymous@hello/path/?query=value#ancor'>first:second:third:anonymous@hello/path/?query=value#ancor</a> after</p>");
        test(
            "http://123.234.245.34/toto/titi/MyDoc.pdf",
            "<p><a href='http://123.234.245.34/toto/titi/MyDoc.pdf'>http://123.234.245.34/toto/titi/MyDoc.pdf</a></p>");

        // "Magic" references (starting with "image:", "download:", ...)
        test(
            "before image:http://www.foo.com/bar.gif after",
            "<p>before <img src='http://www.foo.com/bar.gif' title='http://www.foo.com/bar.gif'/> after</p>");
        test(
            "before download:http://www.foo.com/bar.zip after",
            "<p>before <a href='http://www.foo.com/bar.zip'>http://www.foo.com/bar.zip</a> after</p>");
        test("download:MyDoc.pdf", "<p><a href='MyDoc.pdf'>MyDoc.pdf</a></p>");
        test(
            "Reference: download:MyDoc.pdf :not a reference",
            "<p>Reference: <a href='MyDoc.pdf'>MyDoc.pdf</a> :not a reference</p>");

        // Escaped reference
        test(
            "before wiki\\:Hello after",
            "<p>before wiki<span class='escaped'>:</span>Hello after</p>");

        // Not references
        test("download::MyDoc.pdf", "<p>download::MyDoc.pdf</p>");
        test("before abc::after", "<p>before abc::after</p>");
        test("before abc: after", "<p>before abc: after</p>");
        test("before abc# after", "<p>before abc# after</p>");
        test("before abc:#cde after", "<p>before abc:#cde after</p>");

        // Explicit references.
        test(
            "before [toto] after",
            "<p>before <a href='toto'>toto</a> after</p>");
        test(
            "before (=toto=) after",
            "<p>before <a href='toto'>toto</a> after</p>");
        test(
            "before [#local ancor] after",
            "<p>before <a href='#local'>ancor</a> after</p>");

        test("before (((doc-before(=toto=)doc-after))) after", ""
            + "<p>before</p>\n"
            + "<div class='doc'>\n"
            + "<p>doc-before<a href='toto'>toto</a>doc-after</p>\n"
            + "</div>\n"
            + "<p>after</p>");
        test("before ((((=toto=)))) after", ""
            + "<p>before</p>\n"
            + "<div class='doc'>\n"
            + "<p><a href='toto'>toto</a></p>\n"
            + "</div>\n"
            + "<p>after</p>");
        test(" ((((=toto=))))", ""
            + "<div class='doc'>\n"
            + "<p><a href='toto'>toto</a></p>\n"
            + "</div>");
        test("((((=toto=))))", ""
            + "<div class='doc'>\n"
            + "<p><a href='toto'>toto</a></p>\n"
            + "</div>");

        test("((((((toto))))))", ""
            + "<div class='doc'>\n"
            + "<div class='doc'>\n"
            + "<p>toto</p>\n"
            + "</div>\n"
            + "</div>");
        test("(((a(((toto)))b)))", ""
            + "<div class='doc'>\n"
            + "<p>a</p>\n"
            + "<div class='doc'>\n"
            + "<p>toto</p>\n"
            + "</div>\n"
            + "<p>b</p>\n"
            + "</div>");
    }

    /**
     * @throws WikiParserException
     */
    public void testSpecialSymbols() throws WikiParserException {
        test(":)");
    }

    /**
     * @throws WikiParserException
     */
    public void testTables() throws WikiParserException {
        test("!! Header :: Cell ", ""
            + "<table><tbody>\n"
            + "  <tr><th> Header </th><td> Cell </td></tr>\n"
            + "</tbody></table>");
        test("!!   Header    ::    Cell    ", ""
            + "<table><tbody>\n"
            + "  <tr><th>   Header    </th><td>    Cell    </td></tr>\n"
            + "</tbody></table>");

        test("::Cell 1 :: Cell 2", "<table><tbody>\n"
            + "  <tr><td>Cell 1 </td><td> Cell 2</td></tr>\n"
            + "</tbody></table>");
        test("Not a Header :: Not a Cell", "<p>Not a Header :: Not a Cell</p>");
        test("Not a Header::Not a Cell", "<p>Not a Header::Not a Cell</p>");

        test("|| cell 1.1 || cell 1.2\n" + "|| cell 2.1|| cell 2.2", ""
            + "<table><tbody>\n"
            + "  <tr><th> cell 1.1 </th><th> cell 1.2</th></tr>\n"
            + "  <tr><th> cell 2.1</th><th> cell 2.2</th></tr>\n"
            + "</tbody></table>");
        test("|| Head 1.1 || Head 1.2\n" + "| cell 2.1| cell 2.2", ""
            + "<table><tbody>\n"
            + "  <tr><th> Head 1.1 </th><th> Head 1.2</th></tr>\n"
            + "  <tr><td> cell 2.1</td><td> cell 2.2</td></tr>\n"
            + "</tbody></table>");
        test("|| Multi \nline  \nheader \n"
            + "| Multi\nline\ncell\n\nOne,two,three", ""
            + "<table><tbody>\n"
            + "  <tr><th> Multi \nline  \nheader </th></tr>\n"
            + "  <tr><td> Multi\nline\ncell</td></tr>\n"
            + "</tbody></table>\n"
            + "<p>One,two,three</p>");
        test("this is not || a table", "<p>this is not || a table</p>");
        test("this is not | a table", "<p>this is not | a table</p>");
        test(
            "|| __Italic header__ || *Bold header*\n"
                + "| __Italic cell__ | *Bold cell*\n",
            ""
                + "<table><tbody>\n"
                + "  <tr><th> <em>Italic header</em> </th><th> <strong>Bold header</strong></th></tr>\n"
                + "  <tr><td> <em>Italic cell</em> </td><td> <strong>Bold cell</strong></td></tr>\n"
                + "</tbody></table>");
        test(
            "|| __Italic header || *Bold header \n"
                + "| __Italic cell | *Bold cell \n",
            ""
                + "<table><tbody>\n"
                + "  <tr><th> <em>Italic header </em></th><th> <strong>Bold header </strong></th></tr>\n"
                + "  <tr><td> <em>Italic cell </em></td><td> <strong>Bold cell </strong></td></tr>\n"
                + "</tbody></table>");

        // Table parameters
        test("{{a=b}}\n|| Header ", ""
            + "<table a='b'><tbody>\n"
            + "  <tr><th> Header </th></tr>\n"
            + "</tbody></table>");
        test("{{a=b}}\n!! Header ", ""
            + "<table a='b'><tbody>\n"
            + "  <tr><th> Header </th></tr>\n"
            + "</tbody></table>");
        test("{{a=b}}\n| cell ", ""
            + "<table a='b'><tbody>\n"
            + "  <tr><td> cell </td></tr>\n"
            + "</tbody></table>");
        test("{{a=b}}\n:: cell ", ""
            + "<table a='b'><tbody>\n"
            + "  <tr><td> cell </td></tr>\n"
            + "</tbody></table>");

        // Row parameters
        test("{{a=b}}||cell");
        test("{{a=b}}::cell1\n{{c=d}}::cell2");

        test("{{a=b}}\n{{c=d}}||{{e=f}} cell");
        test("{{a=b}}\n{{c=d}}::{{e=f}} cell ::{{g=h}}");

    }

    /**
     * @throws WikiParserException
     */
    public void testVerbatimeBlocks() throws WikiParserException {
        test("{{{verbatim}}}", "<pre>verbatim</pre>");
        test("{{{ver\\}}}batim}}}", "<pre>ver}}}batim</pre>");
        test("before{{{verbatim}}}after", "<p>before</p>\n"
            + "<pre>verbatim</pre>\n"
            + "<p>after</p>");
        test("{{{verbatim", "<pre>verbatim</pre>");
        test("{{{{{{verbatim", "<pre>{{{verbatim</pre>");
        test("{{{{{{verbatim}}}", "<pre>{{{verbatim</pre>");
        test("{{{{{{verbatim}}}}}}", "<pre>{{{verbatim}}}</pre>");
        test(
            "{{{before{{{verbatim}}}after}}}",
            "<pre>before{{{verbatim}}}after</pre>");

        test(
            "{{{before{{{123{{{verbatim}}}456}}}after}}}",
            "<pre>before{{{123{{{verbatim}}}456}}}after</pre>");
        test(
            "{{{verbatim}}}}}} - the three last symbols should be in a paragraph",
            "<pre>verbatim</pre>\n"
                + "<p>}}} - the three last symbols should be in a paragraph</p>");

        // Complex formatting
        test("!! Syntax !! Results\n"
            + ":: {{{\n"
            + "!! Header 1 !! Header 2\n"
            + ":: Cell 1 :: Cell 2\n"
            + "}}} :: (((\n"
            + "!! Header 1 !! Header 2\n"
            + ":: Cell 1 :: Cell 2\n"
            + ")))\n"
            + ":: {{{\n"
            + "|| Header 1 || Header 2\n"
            + "| Cell 1 | Cell 2\n"
            + "}}} :: (((\n"
            + "|| Header 1 || Header 2\n"
            + "| Cell 1 | Cell 2\n"
            + ")))\n"
            + "");
    }

    public void testVerbatimInlineElements() throws WikiParserException {
        test("`verbatim`", "<p><code>verbatim</code></p>");
        test("before`verbatim`after", "<p>before<code>verbatim</code>after</p>");

        // Bad formed elements
        test("`verbatim", "<p>`verbatim</p>");
        test("before`after", "<p>before`after</p>");
        test("before`after\nnext line", "<p>before`after\nnext line</p>");
    }
}