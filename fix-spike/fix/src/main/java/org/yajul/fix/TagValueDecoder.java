package org.yajul.fix;

import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * MINA Decoder that finds FIX tag/value pairs in the incoming byte stream.   The syntax is:
 * <pre>
 * tagValuePair -> tagNum '=' value SOH
 * tagNum -> ['0'..'9']+
 * value -> [^SOH]*
 * </pre>
 * <ul>
 * <li>Tag numbers are a sequence of digits.</li>
 * <li>The tag number is separated from the value by an '='</li>
 * <li>Values are any byte except 0x01 (ASCII SOH).</li>
 * <li>Terminated by ASCII SOH byte (0x01).</li>
 * </ul>
 *
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 8:54:50 AM
 */
public class TagValueDecoder implements ProtocolDecoder {

    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
             
    }

    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }

    public void dispose(IoSession session) throws Exception {
    }
}
