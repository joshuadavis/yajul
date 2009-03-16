package org.yajul.fix;

import junit.framework.TestCase;
import org.junit.Test;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.SocketAddress;

/**
 * <br>User: Josh
 * Date: Mar 14, 2009
 * Time: 1:07:37 PM
 */
public class SimpleMinaTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(SimpleMinaTest.class);

    @Test
    public void testFixDecoder() throws Exception {
        if (log.isDebugEnabled())
            log.debug("testFixDecoder()");
        FixDecoder decoder = new FixDecoder();
        DummySession dummySession = new DummySession();
        String message = "8=FIX.4.4\0019=12\00135=X\001108=30\00110=036\001";
        IoBuffer buf = IoBuffer.wrap(CodecHelper.getBytes("z8=F33x" + message));
        TestDecoderOutput output = new TestDecoderOutput();
        decoder.decode(dummySession, buf, output);

        // Fragmented
        byte bytes[] = CodecHelper.getBytes(message);
        int fragsize = 3;

        dummySession = new DummySession();
        dummySession.setTransportMetadata(
                new DefaultTransportMetadata(
                        "mina", "dummy", false, true,
                        SocketAddress.class, IoSessionConfig.class, Object.class)
        );
        assertEquals(true, dummySession.getTransportMetadata().hasFragmentation());
        for (int i = 0; i < bytes.length; i += fragsize) {
            int rem = bytes.length - i;
            int sz = rem > fragsize ? fragsize : rem;
            buf = IoBuffer.wrap(bytes,i,sz);
            if (log.isDebugEnabled())
                log.debug("testFixDecoder() : fragment " + i + "->" + (i+sz));
            decoder.decode(dummySession, buf, output);
        }
    }

/*
    @Test
    public void testMockSocket() throws InterruptedException, IOException {
        // Create a 'server'.
        IoAcceptor acceptor = new VmPipeAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("fieldvaluecodec", new ProtocolCodecFilter(new TagValueEncoder(),new TagValueDecoder()));
        acceptor.setHandler(new SimpleServer());
        acceptor.bind(new VmPipeAddress(1000));

        // Create a 'client'.
        IoConnector connector = new VmPipeConnector();
        connector.setHandler(new SimpleHandler("CLI"));
        ConnectFuture connectFuture = connector.connect(new VmPipeAddress(1000));
        IoSession session = connectFuture.await().getSession();
        session.write("thing1").await();
        session.write("thing2").await();
        session.close(true).await();
    }

    private class SimpleServer implements IoHandler {
        public void sessionCreated(IoSession session) throws Exception {
        }

        public void sessionOpened(IoSession session) throws Exception {
        }

        public void sessionClosed(IoSession session) throws Exception {
        }

        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        }

        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        }

        public void messageReceived(IoSession session, Object message) throws Exception {
            // Send back some tag/value pairs.
            session.write(new TagValuePair(8,"FIX 4.2".getBytes()));
        }

        public void messageSent(IoSession session, Object message) throws Exception {
        }
    }

    private class SimpleHandler implements IoHandler {

        private String name;

        SimpleHandler(String name) {
            this.name = name;
        }

        public void sessionCreated(IoSession session) throws Exception {
            log("sessionCreated()");
        }

        private void log(String msg) {
            log.info(name + ":" + msg);
        }

        public void sessionOpened(IoSession session) throws Exception {
            log("sessionOpened()");
        }

        public void sessionClosed(IoSession session) throws Exception {
            log("sessionClosed()");
        }

        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            log("sessionIdle()");
        }

        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            log("exceptionCaught()");
        }

        public void messageReceived(IoSession session, Object message) throws Exception {
            if (session instanceof IoBuffer) {
                IoBuffer ioBuffer = (IoBuffer) session;
                message = ioBuffer.getString(CodecConstants.CHARSET.newDecoder());
            }
            log("messageReceived() : " + message);
        }

        public void messageSent(IoSession session, Object message) throws Exception {
            log("messageSent()");
        }
    }
*/

    private static class TestDecoderOutput implements ProtocolDecoderOutput {
        public void write(Object message) {
            if (log.isDebugEnabled())
                log.debug("write() : " + message);
        }

        public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
            if (log.isDebugEnabled())
                log.debug("flush()");
        }
    }
}
