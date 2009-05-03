package org.yajul.fix.test;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock protocol decoder output for unit testing.
 * <br>User: Josh
 * Date: May 3, 2009
 * Time: 2:54:31 PM
 */
public class MockProtocolDecoderOutput implements ProtocolDecoderOutput {
    private final static Logger log = LoggerFactory.getLogger(MockProtocolDecoderOutput.class);

    private final List<Object> messages = new ArrayList<Object>();

    public void write(Object message) {
        log.info("message=" + message);
        messages.add(message);
    }

    public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
    }

    public List<Object> getMessages() {
        return messages;
    }
}
