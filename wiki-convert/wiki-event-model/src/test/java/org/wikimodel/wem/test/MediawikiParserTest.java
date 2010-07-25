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
import org.wikimodel.wem.mediawiki.MediaWikiParser;

/**
 * @author MikhailKotelnikov
 */
public class MediawikiParserTest extends AbstractWikiParserTest {

    /**
     * @param name
     */
    public MediawikiParserTest(String name) {
        super(name);
    }

    @Override
    protected IWikiParser newWikiParser() {
        return new MediaWikiParser();
    }

    /**
     * @throws WikiParserException
     */
    public void testTables() throws WikiParserException {
        test("{|\n|table\n|}");
        test("{|\n"
            + "| Cell 1.1 || Cell 1.2 \n"
            + "|-\n"
            + "| Cell 2.1 || Cell 2.2 \n"
            + "|-\n"
            + "| Cell 3.1 || Cell 3.2 \n"
            + "|}");
        test("before\n"
            + "{|\n"
            + "| Cell 1.1 || Cell 1.2 \n"
            + "|-\n"
            + "| Cell 2.1 || Cell 2.2 \n"
            + "|-\n"
            + "| Cell 3.1 || Cell 3.2 \n"
            + "|}\n"
            + "after");

        // Bad-formed but recognized tables (bad-formed from the point of view
        // of MediaWiki)
        test("|cell\nthe same cell || the next cell");
        test("!Head\nthe same head !! the next head\n"
            + "|-\n"
            + "| Cell || The next cell\n"
            + "|- style='background-color: #eeeeee'\n"
            + "|The third row");

        test("before\n{|\n|table\n{|\nembedded\n|}\n|} after");
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
        test("''''bold-italic''''");
        test("'''bold'''");
        test("''italic''");
        test("2H<sup>+</sup> + SO<sup>-</sup><sub>4</sub>");
    }

    /**
     * @throws WikiParserException
     */
    public void testHeaders() throws WikiParserException {
        test("=Header1=");
        test("==Header2==");
        test("===Header3===");
        test("====Header4====");
        test("=====Header5====");
        test("======Header6======");
        test("=======Header6(?)=======");

        test("\n===Header===\n * list item");
        test("before\n=== Header ===\nafter");
        test("before\n=== Header \nafter");
        test("This is not a header: ===");
        test("== Header**bold** //italic// ==");
    }

    /**
     * @throws WikiParserException
     */
    public void testHorLine() throws WikiParserException {
        test("----");
        test("-------");
        test("-----------");
        test(" -----------");
        test("---abc");
    }
    
    
    public void testImage() throws WikiParserException {
    	
    	test("[[Image:Yahoo Headquarters.jpg|thumb|right|250px|Yahoo headquarters in Sunnyvale]]"); 
    	test("[[Image:Jerry Yang and David Filo.jpg|thumb|right|250px|Yahoo! co-founders [[Jerry Yang (entrepreneur)|Jerry Yang]] (left) and [[David Filo]] (right)]]");
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
        test(";a: b\n;c: d");

        test("*first");
        test("* first");
        test("** second");
        test("*item one\n"
            + "* item two\n"
            + "*#item three\n"
            + "*# item four\n"
            + "* item five - first line\n"
            + "   item five - second line\n"
            + "* item six\n"
            + "  is on multiple\n"
            + " lines");

        test("; second");
        test(";: second");

        test(";term:  definition");
        test(";:just definition");
        test(";just term");
        test(";:");
        test("*#;:");
        test("*unordered\n*#ordered\n*#;term\n*#:definition");

        test(";term one: definition one\n"
            + ";term two: definition two\n"
            + ";term three: definition three");

        test(";One,\ntwo,\nbucle my shoes...:\n"
            + "...Three\nfour,\nClose the door\n"
            + ";Five,\nSix: Pick up\n sticks\n\ntam-tam, pam-pam...");

        test(":definition: ''definition''");
        test(":a\n::b\n:::c\nnot a definition: ''definition''");
        test(";term: definition: but this is just a text...\n:and this is a definition again.\n*: and this as well");

        test(";term: ''definition''");
        test("; __term__ : ''definition''");
        test(";__term__: ''definition''");

        test("this is not a definition --\n"
            + " ;__not__ a term: ''not'' a definition\n"
            + "----toto");

        test("#list item A1\n"
            + "##list item B1\n"
            + "##list item B2\n"
            + "#:continuing list item A1\n"
            + "#list item A2");
        test("* ''Unordered lists'' are easy to do:\n"
            + "** Start every line with a star.\n"
            + "*** More stars indicate a deeper level.\n"
            + "*: Previous item continues.\n"
            + "** A newline\n"
            + "* in a list\n"
            + "marks the end of the list.\n"
            + "* Of course you can start again.");

        test("* You can even do mixed lists\r\n"
            + "*# and nest them\r\n"
            + "*# inside each other\r\n"
            + "*#* or break lines<br>in lists.\r\n"
            + "*#; definition lists\r\n"
            + "*#: can be \r\n"
            + "*#:; nested : too");

        test("*item1\n|table\n*item2");

    }

    /**
     * @throws WikiParserException
     */
    public void testParagraphs() throws WikiParserException {
        test("First paragraph.\n"
            + "Second line of the same paragraph.\n"
            + "\n"
            + "The second paragraph");

        test("\n<toto");
    }

    /**
     * @throws WikiParserException
     */
    public void testProperties() throws WikiParserException {
        test("#toto hello  world\n123");
        test("#prop1 value1\n#prop2 value2");
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
    public void testSpecialSymbols() throws WikiParserException {
        test(":)");
    }

    /**
     * @throws WikiParserException
     */
    public void testVerbatimeBlocks() throws WikiParserException {
        test("abc \n<pre> 123\n  CDE\n   345 </pre> efg");
        test("abc <nowiki> 123\n  CDE\n   345 </nowiki> efg");
        test("abc\n<math>\n {{{ 123 \n}\\}} \n</math> efg");
        test("inline<math>verbatime</math>block");
        test("</math>just like this...");
        test("<math>just like this...");
        test("just like this...</math>");
        test("just like this...<math>");
    }

}
