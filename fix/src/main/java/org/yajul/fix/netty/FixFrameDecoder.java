package org.yajul.fix.netty;

import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.yajul.fix.util.CodecConstants;
import org.yajul.fix.util.Bytes;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import org.yajul.fix.RawFixMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Handles packet fagmentation.
 * User: josh
 * Date: May 19, 2009
 * Time: 1:23:44 PM
 */
public class FixFrameDecoder extends FrameDecoder {
    private final static Logger log = LoggerFactory.getLogger(FixFrameDecoder.class);

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

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (log.isDebugEnabled())
           log.debug("messageReceived() : " + e);
        super.messageReceived(ctx, e);
    }

    @Override
    protected Object decodeLast(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (log.isDebugEnabled())
           log.debug("decodeLast() : " + buffer);
        return super.decodeLast(ctx, channel, buffer);
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception {
        if (log.isDebugEnabled())
           log.debug("decode() : state=" + state +
                   "\n[" + new String(ChannelBufferHelper.copyBytes(
                       buffer,buffer.readerIndex(),buffer.readableBytes())) + "]");
        int index;
        switch (state) {
            case INITIAL:
                // Look for the beginstring "8=FIX"
                if (buffer.readableBytes() < BEGINSTRING_TOKEN.length)
                    return null;   // There is not enough data to decode.
                index = indexOf(buffer, buffer.readerIndex(), BEGINSTRING_TOKEN);
                if (log.isDebugEnabled())
                   log.debug("decode() : " + state+ " index=" + index);
                if (index == -1) {
                    // Consume the stuff in the buffer up until limit - token.length
                    // (there may be an unfinished token at the end of the buffer).
                    buffer.skipBytes(buffer.readableBytes() - BEGINSTRING_TOKEN.length);
                    return null;   // There is not enough data to decode.
                }
                buffer.readerIndex(index);  // Consume data before the token.
                messageStart = 0;           // The message starts at offset zero from the position.
                state = ParserState.BEGINSTRING;    // Were in the beginstring.
                return null;    // The message is incomplete, read more.
            case BEGINSTRING:
                // We got the "8=FIX".  Skip until we find "<SOH>9="
                beginStringStart = buffer.readerIndex() + 2;
                // Look for a separator followed by '9='.
                if (buffer.readableBytes() < BODYLENGTH_TOKEN.length)
                    return null;   // Not enough bytes for the token, get more.
                index = indexOf(buffer, beginStringStart, BODYLENGTH_TOKEN);
                if (log.isDebugEnabled())
                   log.debug("decode() : " + state+ " index=" + index);
                if (index == -1)
                    return null;   // Read more.
                beginStringLength = index - beginStringStart;
                beginString = ChannelBufferHelper.copyBytes(buffer,beginStringStart,beginStringLength);
                if (log.isDebugEnabled())
                   log.debug("decode() : beginString=" + new String(beginString));
                bodyLengthStart = index + BODYLENGTH_TOKEN.length;
                state = ParserState.BODYLENGTH;
                return null;
            case BODYLENGTH:
                // Read digits until the separator.
                index = bodyLengthStart;
                while (index < buffer.readableBytes()) {
                    if (buffer.getByte(index) == separator) {
                        // Stop, parse the integer.
                        bodyLength = ChannelBufferHelper.parseDigits(buffer,bodyLengthStart, index - bodyLengthStart);
                        bodyEnd = index + bodyLength;   // Calculate the offset of the end of the body.
                        state = ParserState.BODY;
                        return null;
                    }
                    index ++;
                }
                return null;
            case BODY:
                // We read the body length field.  Skip and keep acculmulating until 'bodyLength' has been read.
                if (buffer.readableBytes() < bodyEnd)
                    return null;
                // Begin reading the footer.
                state = ParserState.CHECKSUM;
                return null;
            case CHECKSUM:
                // Body skipped, read the checksum "10=nnnn<SOH>"
                index = indexOf(buffer,bodyEnd,CHECKSUM_TOKEN);
                if (index == -1) {
                    return null;
                }
                checksumStart = index + 3;
                if (log.isDebugEnabled())
                   log.debug("decode() : checksumStart=" + checksumStart);
                index = checksumStart;
                while (index < buffer.readableBytes()) {
                    if (buffer.getByte(index) == separator) {
                        // Stop, parse the integer.
                        checksum = ChannelBufferHelper.parseDigits(buffer,checksumStart,index - checksumStart);
                        if (log.isDebugEnabled())
                           log.debug("decode() : checksum=" + checksum);
                        int length = (index - buffer.readerIndex()) + 1;
                        byte[] bytes = new byte[length];
                        // Consume the whole thing.
                        buffer.readBytes(bytes);
                        // Write the RawFixMessage to the output.
                        RawFixMessage rv = new RawFixMessage(
                                bytes,beginStringStart,
                                new String(beginString),bodyLengthStart,bodyLength,
                                bodyEnd,checksum);
                        reset();
                        if (log.isDebugEnabled())
                           log.debug("decode() : returning " + rv);
                        return rv;
                    }
                    index++;
                }
                // Something is wrong.
                return null;
        }
        return null;
    }
}
