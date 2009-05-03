package org.yajul.fix.decoder;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static org.yajul.fix.decoder.CodecHelper.getBytes;

/**
 * Decodes FIX messages into RawFixMessage objects.  The real work is done by FixDecoder.  This is just
 * a stateles delegator (as per MINA design guidelines).
 * <br>
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 5:28:59 PM
 */
public class FixMessageDecoder extends CumulativeProtocolDecoder {
    private static final AttributeKey STATE = new AttributeKey(FixMessageDecoder.class, "state");

    /**
     * @param in the cumulative buffer
     * @return <tt>true</tt> if and only if there's more to decode in the buffer
     *         and you want to have <tt>doDecode</tt> method invoked again.
     *         Return <tt>false</tt> if remaining data is not enough to decode,
     *         then this method will be invoked again when more data is cumulated.
     * @throws Exception if cannot decode <tt>in</tt>.
     */
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        FixDecoder decoder = getDecoderState(session);
        // Delegate to the stateful object.
        return decoder.doDecode(in,out);
    }

    private FixDecoder getDecoderState(IoSession session) {
        // Create a new decoder state if there isn't one already.
        FixDecoder decoder = (FixDecoder) session.getAttribute(STATE);
        if (decoder == null) {
            decoder = new FixDecoder();
            session.setAttribute(STATE, decoder);
        }
        return decoder;
    }

}