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
import org.wikimodel.wem.xwiki.XWikiParser;

/**
 * @author MikhailKotelnikov
 */
public class XWikiParserTest extends AbstractWikiParserTest {

    /**
     * @param name
     */
    public XWikiParserTest(String name) {
        super(name);
    }

    @Override
    protected IWikiParser newWikiParser() {
        return new XWikiParser();
    }

    /**
     * @throws WikiParserException
     */
    public void testDefinitionLists() throws WikiParserException {
        test(";term: definition");
        test(";:just definition");
        test(";just term");
        test(";:");

        test(";this:is_not_a_term : it is an uri");

        test(";term one: definition one\n"
            + ";term two: definition two\n"
            + ";term three: definition three");

        test(";One,\ntwo,\nbucle my shoes...:\n"
            + "...Three\nfour,\nClose the door\n"
            + ";Five,\nSix:Pick up\n sticks\n\ntam-tam, pam-pam...");

        test(";__term__:''definition''");

        test("this is not a definition --\n"
            + " ;__not__ a term: ''not'' a definition\n"
            + "----toto");
    }

    /**
     * @throws WikiParserException
     */
    public void testEscape() throws WikiParserException {
        test("[a reference]");
        test("[[not a reference]");

        test("~First letter is escaped");
        test("~[not a reference]");
        test("~~escaped tilda");
        test("~ just a tilda because there is an espace after this tilda...");

        test("!Heading\n~!Not a heading\n!Heading again!");
    }

    /**
     * @throws WikiParserException
     */
    public void testFormats() throws WikiParserException {
        test("*bold*");
        test("__bold__");
        test("~~italic~~");
        test("--strike--");
    }

    /**
     * @throws WikiParserException
     */
    public void testHeaders() throws WikiParserException {
        test("1 Heading  1");
        test("1.1 Heading 2");
        test("1.1.1 Heading 3");
        test("1.1.1.1 Heading 4");
        test("1.1.1.1.1.1 Heading 5");
        test("1.1.1.1.1.1 Heading 6");
    }

    /**
     * @throws WikiParserException
     */
    public void testHorLine() throws WikiParserException {
        test("----");
        test("-------");
        test("-----------");
        test(" -----------");
        test("----abc");
    }

    /**
     * @throws WikiParserException
     */
    public void testLineBreak() throws WikiParserException {
        test("abc\\\\def");
    }

    /**
     * @throws WikiParserException
     */
    public void testLists() throws WikiParserException {
        test("* first");
        test("** second");
        test("* first\n** second");
        test("*1. second");
        test("*item one\n"
            + "* item two\n"
            + "*1. item three\n"
            + "*1. item four\n"
            + "* item five - first line\n"
            + "   item five - second line\n"
            + "* item six\n"
            + "  is on multiple\n"
            + " lines");
    }

    public void testMacro() throws WikiParserException {
        test(
            "{toto}a{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a]]></pre>");
        test(
            "{x:toto y:param=value1 z:param2='value two'}a{/x:toto}",
            "<pre class='macro' macroName='x:toto' y:param='value1' z:param2='value two'><![CDATA[a]]></pre>");
        test(
            "{toto}a{toto}b{/toto}c{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a{toto}b{/toto}c]]></pre>");
        test(
            "{toto}a{tata}b{/tata}c{/toto}",
            "<pre class='macro' macroName='toto'><![CDATA[a{tata}b{/tata}c]]></pre>");
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
        test(
            "{x:toto}a{tata}b{/tata}c{/x:toto}",
            "<pre class='macro' macroName='x:toto'><![CDATA[a{tata}b{/tata}c]]></pre>");
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
        test("{/x}", "<p>{/x}</p>");
        test("before{a}x{b}y{c}z\n" + "new line in the same  macro", ""
            + "<p>before<span class='macro' macroName='a'><![CDATA[x{b}y{c}z\n"
            + "new line in the same  macro]]></span></p>");
        test(
            "before{a}x{b}y{c}z{/a}after",
            ""
                + "<p>before<span class='macro' macroName='a'><![CDATA[x{b}y{c}z]]></span>after</p>");

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

        // Not a macro
        test("{ toto a=b c=d}", "<p>{ toto a=b c=d}</p>");

        test(
            "This is a macro: {toto x:a=b x:c=d}\n"
                + "<table>\n"
                + "#foreach ($x in $table)\n"
                + "  <tr>hello, $x</tr>\n"
                + "#end\n"
                + "</table>\n"
                + "{/toto}",
            "<p>This is a macro: <span class='macro' macroName='toto' x:a='b' x:c='d'><![CDATA[\n"
                + "<table>\n"
                + "#foreach ($x in $table)\n"
                + "  <tr>hello, $x</tr>\n"
                + "#end\n"
                + "</table>\n"
                + "]]></span></p>");
        test(
            ""
                + "* item one\n"
                + "* item two\n"
                + "  {code} this is a code{/code} \n"
                + "  the same item (continuation)\n"
                + "* item three",
            ""
                + "<ul>\n"
                + "  <li>item one</li>\n"
                + "  <li>item two\n"
                + "  <span class='macro' macroName='code'><![CDATA[ this is a code]]></span> \n"
                + "  the same item (continuation)</li>\n"
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
        test("First paragraph.\n"
            + "Second line of the same paragraph.\n"
            + "\n"
            + "The second paragraph");
    }

    /**
     * @throws WikiParserException
     */
    public void testQuot() throws WikiParserException {
        test("This is a paragraph\n\n and this is a quotations\n the second line");
    }

    /**
     * @throws WikiParserException
     */
    public void testReferences() throws WikiParserException {
        test("before http://www.foo.bar/com after");
        test("before [toto] after");
        test("before wiki:Hello after");
        test("before wiki~:Hello after");
        test("before [#local ancor] after");

        test("not [[a reference] at all!");
    }

    /**
     * @throws WikiParserException
     */
    public void testTables() throws WikiParserException {
        test("{table}First line\nSecond line\nThird line", ""
            + "<table><tbody>\n"
            + "  <tr><th>First line</th></tr>\n"
            + "  <tr><td>Second line</td></tr>\n"
            + "  <tr><td>Third line</td></tr>\n"
            + "</tbody></table>");
        test("|This is not a table", "<p>|This is not a table</p>");
        test("{table}This | is a | table", ""
            + "<table><tbody>\n"
            + "  <tr><th>This </th><th> is a </th><th> table</th></tr>\n"
            + "</tbody></table>");
        test("{table}This | is a | table\n{table}", ""
            + "<table><tbody>\n"
            + "  <tr><th>This </th><th> is a </th><th> table</th></tr>\n"
            + "</tbody></table>");
        test("before\n{table}This is \na table\n{table}after", ""
            + "<p>before</p>\n"
            + "<table><tbody>\n"
            + "  <tr><th>This is </th></tr>\n"
            + "  <tr><td>a table</td></tr>\n"
            + "</tbody></table>\n"
            + "<p>after</p>");
        test("{table}This is a table", ""
            + "<table><tbody>\n"
            + "  <tr><th>This is a table</th></tr>\n"
            + "</tbody></table>");
        test("{table}First line\nSecond line\nThird line", ""
            + "<table><tbody>\n"
            + "  <tr><th>First line</th></tr>\n"
            + "  <tr><td>Second line</td></tr>\n"
            + "  <tr><td>Third line</td></tr>\n"
            + "</tbody></table>");
    }

}
