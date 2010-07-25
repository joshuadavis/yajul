/**
 * 
 */
package org.wikimodel.wem.test;

import org.wikimodel.wem.EventDumpListener;
import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.IWikiPrinter;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.common.CommonWikiParser;

/**
 * @author kotelnikov
 */
public class EventDumpListenerTest extends AbstractWikiParserTest {

    public EventDumpListenerTest(String name) {
        super(name);
    }

    @Override
    protected IWikiParser newWikiParser() {
        return new CommonWikiParser();
    }

    /**
     * @param buf
     * @return
     */
    protected IWemListener newParserListener(final StringBuffer buf) {
        IWikiPrinter printer = newPrinter(buf);
        IWemListener listener = new EventDumpListener(printer);
        return listener;
    }

    public void test() throws WikiParserException {
        test("%worksIn (((\n"
            + "   %type [Company]\n"
            + "   %name Cognium *Systems*\n"
            + "   %address (((\n"
            + "   )))");
        /**
         * The trace, how it *should* be:
         * 
         * <pre>       
            beginDocument()
                beginPropertyBlock('worksIn',doc=true)
                    beginPropertyBlock('type',doc=false)
                        beginParagraph()
                            beginFormat([])
                                onReference('Company')
                            endFormat([])
                        endParagraph()
                    endPropertyBlock('type', doc=false)
                    beginPropertyBlock('name',doc=false)
                        beginParagraph()
                            beginFormat([])
                                onWord('Cognium')
                                onSpace(' ')
                                onWord('Systems')
                            endFormat([])
                        endParagraph()
                    endPropertyBlock('name', doc=false)
                    beginPropertyBlock('address',doc=true)
                    endPropertyBlock('address', doc=true)
                endPropertyBlock('worksIn', doc=true)
            endDocument()
            </pre>
         */

    }

}
