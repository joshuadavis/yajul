package org.yajul.fix.test;

import junit.framework.TestCase;
import org.yajul.fix.dictionary.Dictionary;
import org.yajul.fix.dictionary.DictionaryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Test dictinary code.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 9:30:33 AM
 */
public class DictionaryTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(DictionaryTest.class);

    public void testDictionaryLoader() throws Exception {
        // Make sure we can load 4.0 and 4.4 dictionaries.
        Dictionary d40 = DictionaryLoader.load("FIX40.xml");
        Dictionary d44 = DictionaryLoader.load("FIX44.xml");

        // Similated parsing.
        Dictionary.ElementList list = d44.getHeader();
        log.info("list=" + list);
        FieldListParser parser = new FieldListParser(list);
        parser.parse(8);
        Dictionary.Element matched = parser.getMatched();
        assert matched != null;
        assertEquals(matched.getElementType(),Dictionary.ElementType.FIELD);
        log.info("matched: " + parser.getMatched());
        parser.parse(9);
        matched = parser.getMatched();
        assert matched != null;
        assertEquals(matched.getElementType(),Dictionary.ElementType.FIELD);
        log.info("matched: " + parser.getMatched());
        parser.parse(35);
        log.info("matched: " + parser.getMatched());
        parser.parse(49);
        log.info("matched: " + parser.getMatched());
        parser.parse(56);
        log.info("matched: " + parser.getMatched());
        parser.parse(34);
        log.info("matched: " + parser.getMatched());
        parser.parse(52);
        log.info("matched: " + parser.getMatched());
        parser.parse(627);
        log.info("matched: " + parser.getMatched());
    }

    class FieldListParser {
        private Dictionary.ElementList list;
        private Iterator<Dictionary.Element> iter;
        private Dictionary.Element e;
        private Dictionary.Element matched;
        private LinkedList<Iterator<Dictionary.Element>> stack;

        public FieldListParser(Dictionary.ElementList list) {
            this.list = list;
            this.iter = list.getElements().iterator();
            this.stack = new LinkedList<Iterator<Dictionary.Element>>();
        }

        public void parse(int tag) {
            // Get the next element if needed.
            if (e == null)
                e = iter.next();
            // Skip non-required elements if they don't match this tag.
            boolean match = e.matchesTag(tag);
            while (!match && !e.isRequired()) {
                if (log.isDebugEnabled())
                    log.debug("parse() : skip optional "  + e);
                e = iter.next();
                match = e.matchesTag(tag);
            }
            if (match) {
                matched = e;
                switch (matched.getElementType()) {
                    case GROUP:
                        if (log.isDebugEnabled())
                            log.debug("parse() : group matched.");
                        stack.addLast(iter);    // Remember this iterator.
                        iter = ((Dictionary.Group)matched).getElements().iterator();
                        break;
                    case COMPONENT:
                    case FIELD:
                }
                e = iter.next();
            }
            else {
                if (log.isDebugEnabled())
                    log.debug("parse() : expected " + e + " got " + list.getDictionary().findFieldDefinition(tag));
                matched = null;
            }
        }

        public Dictionary.Element getMatched() {
            return matched;
        }
    }
}
