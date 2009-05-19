package org.yajul.fix.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.yajul.fix.decoder.FixDecoder;
import org.yajul.fix.RawFixMessage;
import org.yajul.fix.util.Bytes;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Test the decoder classes.
 * <br>User: Josh
 * Date: May 3, 2009
 * Time: 2:49:32 PM
 */
public class DecoderTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(DecoderTest.class);

    public DecoderTest(String n) {
        super(n);
    }

    public void testDecoderState() {
        IoBuffer buf = createBuffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
        FixDecoder state = new FixDecoder();
        assertEquals(state.getState(), FixDecoder.ParserState.INITIAL);
        MockProtocolDecoderOutput out = new MockProtocolDecoderOutput();
        boolean keepParsing = state.doDecode(buf,out);
        assertFalse(keepParsing);
        assertEquals(state.getMessageStart(),0);
        assertEquals(state.getState(), FixDecoder.ParserState.BEGINSTRING);
        if (log.isDebugEnabled())
           log.debug("[1] testDecoderState() : keepParsing=" + keepParsing + " " + state);
        keepParsing = state.doDecode(buf,out);
        if (log.isDebugEnabled())
           log.debug("[2] testDecoderState() : keepParsing=" + keepParsing + " " + state);
        assertEquals(state.getState(), FixDecoder.ParserState.BODYLENGTH);        
        keepParsing = state.doDecode(buf,out);
        if (log.isDebugEnabled())
           log.debug("[3] testDecoderState() : keepParsing=" + keepParsing + " " + state);
        assertEquals(state.getState(), FixDecoder.ParserState.BODY);
        keepParsing = state.doDecode(buf,out);
        if (log.isDebugEnabled())
           log.debug("[4] testDecoderState() : keepParsing=" + keepParsing + " " + state);
        assertEquals(state.getState(), FixDecoder.ParserState.CHECKSUM);        
        keepParsing = state.doDecode(buf,out);
        if (log.isDebugEnabled())
           log.debug("[5] testDecoderState() : keepParsing=" + keepParsing + " " + state);
        assertEquals(state.getState(), FixDecoder.ParserState.INITIAL);

        assertEquals(out.getMessages().size(), 1);
        assertEquals(out.getMessages().get(0).getClass(), RawFixMessage.class);
    }

    private IoBuffer createBuffer(String s) {
        return IoBuffer.wrap(Bytes.getBytes(s));
    }

    public static Test suite() {
        return new TestSuite(DecoderTest.class);
    }
}
