package org.yajul.fix;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import static org.yajul.fix.CodecHelper.getBytes;
import static org.yajul.fix.CodecHelper.indexOf;

/**
 * Decodes FIX messages into RawFixMessage objects.
 * <br>
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 5:28:59 PM
 */
public class FixDecoder extends CumulativeProtocolDecoder {
    private final static Logger log = LoggerFactory.getLogger(FixDecoder.class);

    private static final AttributeKey STATE = new AttributeKey(FixDecoder.class, "state");

    private static final byte DELIMITER = getBytes("\001")[0];
    private static final byte[] BEGINSTRING_TOKEN = getBytes("8=FIX");
    private static final byte[] BODY_LENGTH_TOKEN = getBytes("\0019=");
    private static final byte[] BODY_LENGTH_END = getBytes("\001");
    private static final byte[] CHECKSUM_TOKEN = getBytes("10=");

    /**
     *
     *
     * @param in the cumulative buffer
     * @return <tt>true</tt> if and only if there's more to decode in the buffer
     *         and you want to have <tt>doDecode</tt> method invoked again.
     *         Return <tt>false</tt> if remaining data is not enough to decode,
     *         then this method will be invoked again when more data is cumulated.
     * @throws Exception if cannot decode <tt>in</tt>.
     */    
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        DecoderState decoderState = getDecoderState(session);
        return decoderState.doDecode(in,out);

        int start = in.position();

        if (decoderState.beginStringPosition == -1) {
            // We're looking for the BeginString tag (8=FIX...)
            // If there isn't enough in the buffer for the token, keep going.
            if (in.remaining() < BEGINSTRING_TOKEN.length) {
                if (log.isDebugEnabled())
                    log.debug("doDecode() : too short for BeginString token " + in);
                return false;
            }
            int index = indexOf(in, BEGINSTRING_TOKEN);
            if (index < 0) {
                // Skip to the end.  There is nothing more in this buffer.
                in.position(in.limit());
                return false;
            } else {
                decoderState.beginStringPosition = index;
                decoderState.bodyLengthPosition = -1;
                if (log.isDebugEnabled())
                    log.debug("doDecode() : BeginString found at " + index + " " + in);
            }
        }

        if (decoderState.bodyLengthPosition == -1) {
            int index = indexOf(in, BODY_LENGTH_TOKEN);
            if (index < 0) {
                // Keep accumulating until we find it.
                return false;
            } else {
                decoderState.bodyLengthPosition = index;
                if (log.isDebugEnabled())
                    log.debug("doDecode() : BodyLength found at " + index + " " + in);
            }
        }

        if (decoderState.bodyStartPosition == -1) {
            // Find digits followed by \001 (SOH)
            int digitsStart = decoderState.bodyLengthPosition + BODY_LENGTH_TOKEN.length;
            boolean found = false;
            for (int offset = digitsStart; offset < in.limit() && !found; offset++) {
                if (in.get(offset) == BODY_LENGTH_END[0]) {
                    decoderState.bodyLength = CodecHelper.parseDigits(in, digitsStart, offset - digitsStart);
                    decoderState.bodyCopied = false;
                    if (log.isDebugEnabled())
                        log.debug("doDecode() : bodyLength=" + decoderState.bodyLength + " bodyStart=" + offset);
                    // Copy every byte up until this point into the header buffer.
                    in.position(decoderState.beginStringPosition);
                    in.limit(offset);
                    decoderState.buffer.put(in);
                    // Get some more data.
                    in.position(offset);
                    decoderState.bodyStartPosition = offset;
                    return true;
                }
            }
            // Delimiter not found... keep looking.
            in.position(decoderState.bodyLengthPosition);
            return false;
        }

        if (decoderState.bodyStartPosition != -1 && !decoderState.bodyCopied) {
            // If the buffer does not contain the whole message body, get some more.
            if (in.remaining() < decoderState.bodyLength) {
                if (log.isDebugEnabled())
                   log.debug("doDecode() : buffer too short for body of length " + decoderState.bodyLength);
                return false;   // Accumulate some more.
            }
            else {
                // Buffer is big enough.  Store the message body.
                int end = decoderState.bodyStartPosition + decoderState.bodyLength;
                int oldLimit = in.limit();
                in.position(decoderState.bodyStartPosition);
                in.limit(end);
                if (log.isDebugEnabled())
                   log.debug("doDecode() : body copied from " + decoderState.bodyStartPosition + " to " + end);
                decoderState.buffer.put(in);
                decoderState.bodyCopied = true;
                // Get more data.
                in.position(end);
                in.limit(oldLimit);
            }
        }

        if (decoderState.bodyCopied && decoderState.checksumPosition == -1) {
            if (in.remaining() < CHECKSUM_TOKEN.length) {
                if (log.isDebugEnabled())
                    log.debug("doDecode() : too short for CheckSum token " + in);
                return false;
            }
            int index = indexOf(in,CHECKSUM_TOKEN);
            if (index != 0) {
                // No checksum token?
                if (log.isDebugEnabled())
                   log.debug("doDecode() : no checksum?");
            }
            decoderState.checksumPosition = index;
            if (log.isDebugEnabled())
               log.debug("doDecode() : CheckSum found at " + index + " " + in);
            // While not SOH, copy to buffer.
            while (in.hasRemaining()) {
                byte ch = in.get();
                decoderState.buffer.put(ch);
                if (ch == DELIMITER) {
                    byte[] rawBytes = CodecHelper.getBytes(decoderState.buffer);
                    RawFixMessage message = new RawFixMessage(rawBytes);
                    out.write(message);
                    decoderState.reset();
                    return true;
                }
            }
            return false;
        }
        in.position(start);
        return false;
    }

    private DecoderState getDecoderState(IoSession session) {
        // Create a new decoder state if there isn't one already.
        DecoderState decoderState = (DecoderState) session.getAttribute(STATE);
        if (decoderState == null) {
            decoderState = new DecoderState();
            session.setAttribute(STATE, decoderState);
        }
        return decoderState;
    }

    private class DecoderState {
        private int beginStringPosition;
        private int bodyLengthPosition;
        private int bodyLength;
        private int bodyStartPosition;
        private IoBuffer buffer;
        public boolean bodyCopied;
        public int checksumPosition;

        private DecoderState() {
            buffer = IoBuffer.allocate(32);
            buffer.setAutoExpand(true);
            reset();
        }

        public void reset() {
            beginStringPosition = -1;
            bodyLengthPosition = -1;
            bodyLength = -1;
            bodyStartPosition = -1;
            bodyCopied = false;
            checksumPosition = -1;
            buffer.clear();
        }
    }
}
