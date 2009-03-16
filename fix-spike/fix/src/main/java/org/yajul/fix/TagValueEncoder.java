package org.yajul.fix;

import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharsetEncoder;

/**
 * Encodes FIX tag/value pairs onto the output stream.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:13:59 AM
 */
public class TagValueEncoder implements ProtocolEncoder {

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        TagValuePair pair = (TagValuePair) message;
        String tagNum = Integer.toString(pair.getTag());
        IoBuffer buf = IoBuffer.allocate(tagNum.length() + 1 + pair.getRawValue().length + 1);
        CharsetEncoder charsetEncoder = CodecConstants.CHARSET.newEncoder();
        buf.putString(tagNum,charsetEncoder);
        buf.putString("=",charsetEncoder);
        buf.put(pair.getRawValue());
        buf.put(CodecConstants.SOH);
        buf.flip();
        out.write(buf);
    }

    public void dispose(IoSession session) throws Exception {
    }
}
