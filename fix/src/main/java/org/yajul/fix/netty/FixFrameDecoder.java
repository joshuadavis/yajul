package org.yajul.fix.netty;

import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.yajul.fix.util.CodecConstants;
import org.yajul.fix.util.Bytes;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import org.yajul.fix.RawFixMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Parses FIX messages from incoming stream.  The input is a Netty ChannelBuffer, and the output
 * is a RawFixMessage.  
 * Uses Netty FrameDecoder to handle fragmentation, and ignores any garbage before and after
 * FIX messages.
 * <br>
 *  User: josh
 * Date: May 19, 2009
 * Time: 1:23:44 PM
 */
public class FixFrameDecoder extends FrameDecoder {
    private final static Logger log = LoggerFactory.getLogger(FixFrameDecoder.class);

    private static final byte[] BEGINSTRING_TOKEN = Bytes.getBytes("8=FIX");
    private static final byte[] BODYLENGTH_TOKEN = Bytes.getBytes("\0019=");
    private static final byte[] CHECKSUM_TOKEN = Bytes.getBytes("10=");

    private ParserState state;

    private int messageStart;
    private int beginStringStart;
    private int beginStringLength;
    private byte[] beginString;
    private int bodyLengthStart;
    private int bodyLength;
    private int bodyEnd;
    private int checksumStart;
    private int checksum;
    public final byte separator;
    private byte[] bodyLengthToken;

    public FixFrameDecoder() {
        this(CodecConstants.SOH);
    }

    public FixFrameDecoder(byte separator) {
        super();
        this.separator = separator;
        this.bodyLengthToken = new byte[BODYLENGTH_TOKEN.length];
        System.arraycopy(BODYLENGTH_TOKEN,0,this.bodyLengthToken,0,BODYLENGTH_TOKEN.length);
        // Copy the body length token and replace the sep
        this.bodyLengthToken[0] = separator;
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
        return "FixMessageDecoder{" +
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
    protected Object decodeLast(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (log.isDebugEnabled())
            log.debug("decodeLast() : " + buffer);
        return super.decodeLast(ctx, channel, buffer);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (log.isDebugEnabled())
           log.debug("channelConnected()");
        super.channelConnected(ctx, e);
        reset();
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (log.isDebugEnabled())
           log.debug("channelDisconnected()");
        super.channelDisconnected(ctx, e);
        reset();
        state = null;
    }

    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception {
        if (state == null)
            throw new IllegalStateException("Decoder not initialized.");
        if (log.isDebugEnabled())
            log.debug("** decode() : state=" + state +
                    " [" + new String(ChannelBufferHelper.copyBytes(
                    buffer, buffer.readerIndex(), buffer.readableBytes())) + "]");
        int index, end;
        switch (state) {
            case INITIAL:
                // Look for the beginstring "8=FIX"
                index = indexOf(buffer, buffer.readerIndex(), BEGINSTRING_TOKEN);
                if (log.isDebugEnabled())
                    log.debug("decode() : " + state + " index=" + index);
                if (index == -1) {
                    // Consume the stuff in the buffer up until limit - token.length
                    // (there may be an unfinished token at the end of the buffer).
                    buffer.skipBytes(buffer.readableBytes() - BEGINSTRING_TOKEN.length);
                    return null;   // There is not enough data to decode.
                }
                buffer.readerIndex(index);  // Consume data before the token.
                messageStart = 0;           // The message starts at offset zero from the position.
                state = ParserState.BEGINSTRING;    // Were in the beginstring.
                // Flow into the next state.
            case BEGINSTRING:
                // We got the "8=FIX".  Skip until we find "<SOH>9="
                beginStringStart = buffer.readerIndex() + 2;    // The location just after '8='.
                // Look for a separator followed by '9='.
                index = indexOf(buffer, beginStringStart, bodyLengthToken);
                if (log.isDebugEnabled())
                    log.debug("decode() : " + state + " index=" + index);
                if (index == -1)
                    return null;   // Read more.
                beginStringLength = index - beginStringStart;
                beginString = ChannelBufferHelper.copyBytes(buffer, beginStringStart, beginStringLength);
                if (log.isDebugEnabled())
                    log.debug("decode() : beginString=" + new String(beginString));
                bodyLengthStart = index + bodyLengthToken.length;
                state = ParserState.BODYLENGTH;
                // Flow into the next state.
            case BODYLENGTH:
                // Read digits until the separator.
                index = bodyLengthStart;
                end = buffer.readerIndex() + buffer.readableBytes();
                while (index < end && state == ParserState.BODYLENGTH) {
                    if (buffer.getByte(index) == separator) {
                        // Stop, parse the integer.
                        bodyLength = ChannelBufferHelper.parseDigits(buffer, bodyLengthStart, index - bodyLengthStart);
                        bodyEnd = index + bodyLength;   // Calculate the offset of the end of the body.
                        if (log.isDebugEnabled())
                            log.debug("decode() : bodyLength=" + bodyLength + " bodyEnd=" + bodyEnd);
                        state = ParserState.BODY;
                    } else {
                        index++;
                    }
                }
                if (state != ParserState.BODY)
                    return null;
                // Flow into the next state.
            case BODY:
                if (log.isDebugEnabled())
                    log.debug("decode() : " + state + " readableBytes=" + buffer.readableBytes() + " bodyEnd=" + bodyEnd);
                // We read the body length field.  Skip and keep acculmulating until 'bodyLength' has been read.
                if (buffer.readableBytes() < bodyEnd)
                    return null;
                // Begin reading the footer.
                state = ParserState.CHECKSUM;
                // Flow into the next state.
            case CHECKSUM:
                // Body skipped, read the checksum "10=nnnn<SOH>"
                index = indexOf(buffer, bodyEnd, CHECKSUM_TOKEN);
                if (log.isDebugEnabled())
                    log.debug("decode() : " + state + " index=" + index);
                if (index == -1) {
                    return null;
                }
                checksumStart = index + 3;
                index = checksumStart;
                end = buffer.readerIndex() + buffer.readableBytes();
                while (index < end) {
                    if (buffer.getByte(index) == separator) {
                        // Stop, parse the integer.
                        checksum = ChannelBufferHelper.parseDigits(buffer, checksumStart, index - checksumStart);
                        if (log.isDebugEnabled())
                            log.debug("decode() : checksum=" + checksum);
                        int length = (index - buffer.readerIndex()) + 1;
                        byte[] bytes = new byte[length];
                        // Consume the whole thing.
                        buffer.readBytes(bytes);
                        // Write the RawFixMessage to the output.
                        RawFixMessage rv = new RawFixMessage(
                                bytes,
                                new String(beginString), bodyLength,
                                bodyEnd, checksum, separator);
                        reset();
                        if (log.isDebugEnabled())
                            log.debug("decode() : returning " + rv);
                        return rv;
                    }
                    index++;
                }
                // Something is wrong.
                if (log.isDebugEnabled())
                    log.debug("decode() : separator not found.");
                return null;
        }
        return null;
    }
}
