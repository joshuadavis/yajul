package org.yajul.fix.decoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.yajul.fix.RawFixMessage;
import org.yajul.fix.util.CodecConstants;
import org.yajul.fix.util.Bytes;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Decodes FIX messages from an 'accumulating' IoBuffer.
 * <br>User: Josh
 * Date: May 3, 2009
 * Time: 1:52:11 PM
 */
public class FixDecoder {
    private final static Logger log = LoggerFactory.getLogger(FixDecoder.class);

    private static final byte[] BEGINSTRING_TOKEN = Bytes.getBytes("8=FIX");
    private static final byte[] BODYLENGTH_TOKEN = Bytes.getBytes("\0019=");
    private static final byte[] CHECKSUM_TOKEN = Bytes.getBytes("10=");

    public static enum ParserState {
        INITIAL,
        BEGINSTRING,
        BODYLENGTH,
        BODY, CHECKSUM,
    }

    private ParserState state = ParserState.INITIAL;

    private int messageStart;
    private int beginStringStart;
    private int beginStringLength;
    private byte[] beginString;
    private int bodyLengthStart;
    private int bodyLength;
    private int bodyEnd;
    private int checksumStart;
    private int checksum;
    public byte separator = CodecConstants.SOH;
    
    public FixDecoder() {
        reset();
    }

    public void reset() {
        state = ParserState.INITIAL;
        messageStart = -1;
        beginStringStart = -1;
        beginStringLength = 0;
        beginString = null;
        bodyLengthStart = -1;
        bodyLength = 0;
        bodyEnd = -1;
        checksumStart = -1;
        checksum = 0;
    }

    /**
     * @param in  the cumulative buffer
     * @param out the ouptut
     * @return <tt>true</tt> if and only if there's more to decode in the buffer
     *         and you want to have <tt>doDecode</tt> method invoked again.
     *         Return <tt>false</tt> if remaining data is not enough to decode,
     *         then this method will be invoked again when more data is cumulated.
     */
    public boolean doDecode(IoBuffer in, ProtocolDecoderOutput out) {
        int index;
        switch (state) {
            case INITIAL:
                // Look for the beginstring "8=FIX"
                if (in.remaining() < BEGINSTRING_TOKEN.length)
                    return false;   // There is not enough data to decode.
                index = CodecHelper.indexOf(in, in.position(), BEGINSTRING_TOKEN);
                if (log.isDebugEnabled())
                   log.debug("doDecode() : " + state+ " index=" + index);
                if (index == -1) {
                    // Consume the stuff in the buffer up until limit - token.length
                    // (there may be an unfinished token at the end of the buffer).
                    in.position(in.limit() - BEGINSTRING_TOKEN.length);
                    return false;   // There is not enough data to decode.
                }
                in.position(index); // Consume data before the token.
                messageStart = 0;   // The message starts at offset zero from the position.
                state = ParserState.BEGINSTRING;    // Were in the beginstring.
                return false;   // The message is incomplete, read more.
            case BEGINSTRING:
                // We got the "8=FIX".  Skip until we find "<SOH>9="
                beginStringStart = messageStart + 2;
                // Look for a separator followed by '9='.
                if (in.remaining() < BODYLENGTH_TOKEN.length)
                    return false;   // Not enough bytes for the token, get more.
                index = CodecHelper.indexOf(in, beginStringStart, BODYLENGTH_TOKEN);
                if (log.isDebugEnabled())
                   log.debug("doDecode() : " + state+ " index=" + index);
                if (index == -1)
                    return false;   // Read more.
                beginStringLength = index - beginStringStart;
                beginString = CodecHelper.copyBytes(in,beginStringStart,beginStringLength);
                if (log.isDebugEnabled())
                   log.debug("doDecode() : beginString=" + new String(beginString));
                bodyLengthStart = index + BODYLENGTH_TOKEN.length;
                state = ParserState.BODYLENGTH;
                return false;
            case BODYLENGTH:
                // Read digits until the separator.
                index = bodyLengthStart;
                while (index < in.limit()) {
                    if (in.get(index) == separator) {
                        // Stop, parse the integer.
                        bodyLength = CodecHelper.parseDigits(in,bodyLengthStart, index - bodyLengthStart);
                        bodyEnd = index + bodyLength;   // Calculate the offset of the end of the body.
                        state = ParserState.BODY;
                        return false;
                    }
                    index ++;
                }
                return false;
            case BODY:
                // We read the body length field.  Skip and keep acculmulating until 'bodyLength' has been read.
                if (in.limit() < bodyEnd)
                    return false;
                // Begin reading the footer.
                state = ParserState.CHECKSUM;
                return false;
            case CHECKSUM:
                // Body skipped, read the checksum "10=nnnn<SOH>"
                index = CodecHelper.indexOf(in,bodyEnd,CHECKSUM_TOKEN);
                if (index == -1) {
                    return false;
                }
                checksumStart = index + 3;
                if (log.isDebugEnabled())
                   log.debug("doDecode() : checksumStart=" + checksumStart);
                index = checksumStart;
                while (index < in.limit()) {
                    if (in.get(index) == separator) {
                        // Stop, parse the integer.
                        checksum = CodecHelper.parseDigits(in,checksumStart,index - checksumStart);
                        if (log.isDebugEnabled())
                           log.debug("doDecode() : checksum=" + checksum);                        
                        int length = index - in.position();
                        byte[] bytes = new byte[length];
                        // Consume the whole thing.
                        in.get(bytes);
                        // Write the RawFixMessage to the output.
                        out.write(new RawFixMessage(bytes,beginStringStart,new String(beginString),bodyLengthStart,bodyLength,bodyEnd,checksum));
                        reset();
                        // If there are more bytes in the buffer, keep parsing it.
                        return in.remaining() > 0;
                    }
                    index++;
                }
                // Something is wrong.
        }
        return false;
    }

    public int getMessageStart() {
        return messageStart;
    }

    public int getBeginStringStart() {
        return beginStringStart;
    }

    public int getBeginStringLength() {
        return beginStringLength;
    }

    public ParserState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "FixDecoder{" +
                "state=" + state +
                ", messageStart=" + messageStart +
                ", beginStringStart=" + beginStringStart +
                ", beginStringLength=" + beginStringLength +
                ", beginString=" + (beginString == null ? "null" : new String(beginString)) +
                ", bodyLengthStart=" + bodyLengthStart +
                ", bodyLength=" + bodyLength +
                ", bodyEnd=" + bodyEnd +
                ", checksumStart=" + checksumStart +
                ", checksum=" + checksum +
                ", separator=" + separator +
                '}';
    }
}
