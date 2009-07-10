package org.yajul.fix.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.yajul.fix.netty.ChannelBufferHelper.buffer;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import org.yajul.fix.netty.FixFrameDecoder;
import org.yajul.fix.netty.ChannelBufferHelper;
import org.yajul.fix.netty.FixHandler;
import static org.yajul.fix.util.Bytes.getBytes;
import org.yajul.fix.util.Bytes;
import static org.yajul.fix.util.CodecConstants.DEFAULT_SEPARATOR;
import static org.yajul.fix.util.CodecConstants.DEFAULT_TAG_SEPARATOR;
import org.yajul.fix.RawFixMessage;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Description;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * Test the Netty frame decoder for FIX messages.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:22:03 PM
 */
public class FrameDecoderTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(FrameDecoderTest.class);

    private Mockery mockery = new Mockery();

    public void testBufferHelper() {
        ChannelBuffer buf = buffer(Fix44Examples.EXAMPLE);
        assertEquals(buf.readerIndex(), 0);
        assertEquals(buf.readableBytes(), 34);
        int index = indexOf(buf, 0, getBytes("8=FIX"));
        assertEquals(index, 0);
        index = indexOf(buf, 0, getBytes("10="));
        assertEquals(index, 27);
        index = indexOf(buf, 0, getBytes("blarg"));
        assertEquals(index, -1);
        buf = buffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001whoops8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
        index = indexOf(buf, buf.readerIndex(), getBytes("whoops"));
        assertEquals(index, 34);
        buf.readerIndex(34);
        index = indexOf(buf, buf.readerIndex(), getBytes("8=FIX"));
        assertEquals(index, 40);

        ChannelBuffer b = ChannelBuffers.dynamicBuffer(32);
        byte[] first = getBytes("abcd");
        b.writeBytes(first);
        assertEquals(b.readableBytes(), 4);
        byte[] bytes = ChannelBufferHelper.copyBytes(b);
        assertEquals(Arrays.equals(first, bytes), true);
        byte[] second = getBytes("EFG");
        b.writeBytes(second);
        byte[] bytes2 = ChannelBufferHelper.copyBytes(b);
        byte[] concat = getBytes("abcdEFG");
        assertEquals(Arrays.equals(concat, bytes2), true);
        b.readBytes(first.length);
        byte[] bytes3 = ChannelBufferHelper.copyBytes(b);
        assertEquals(Arrays.equals(second, bytes3), true);
    }

    public void testBytes() throws Exception {
        assertEquals(1,Bytes.numdigits(8));
        assertEquals(2,Bytes.numdigits(12));
        assertEquals(3,Bytes.numdigits(128));
        assertEquals(1,Bytes.numdigits(0));
    }

    public void testRawTag() throws Exception {
        byte[] bytes1 = Bytes.getBytes(Fix44Examples.EXAMPLE);
        RawFixMessage message = new RawFixMessage(bytes1);
        List<RawFixMessage.RawTag> tags = message.getRawTags();
        log.info("tags=" + tags);
    }

    public void testDecoder() throws Exception {
        // Create the decoder, mock out all the rest.
        FixFrameDecoder decoder = new FixFrameDecoder();
        final ChannelHandlerContext ctx = mockery.mock(ChannelHandlerContext.class);
        final ChannelStateEvent cse = mockery.mock(ChannelStateEvent.class);
        final ChannelBuffer buf = ChannelBuffers.dynamicBuffer(32);
        final MessageEvent e = mockery.mock(MessageEvent.class);
        final Sequence sequence = mockery.sequence("seq");

        mockery.checking(new Expectations() {
            {
                // ignoring(ctx);  // We don't care about ctx.
                allowing(e).getChannel();   // We don't care about the channel.
                allowing(e).getRemoteAddress(); // We don't care about the remote address.
                // Three calls, return the buffer every time.  We'll change the state of buf
                // in the test.
                oneOf(ctx).sendUpstream(cse);
                inSequence(sequence);
                one(e).getMessage();
                will(returnValue(buf));
                inSequence(sequence);
                one(e).getMessage();
                will(returnValue(buf));
                inSequence(sequence);
            } });

        decoder.channelConnected(ctx, cse);

        // Simulate garbage on the front and a fragment in the middle.
        if (log.isDebugEnabled())
            log.debug("first fragment");
        buf.writeBytes(getBytes("garbage8=F"));
        decoder.messageReceived(ctx, e);

        if (log.isDebugEnabled())
            log.debug("second fragment");
        buf.writeBytes(getBytes("IX.4.2\0019"));
        decoder.messageReceived(ctx, e);

        mockery.assertIsSatisfied();

        if (log.isDebugEnabled())
            log.debug("third fragment");

        final DefaultMessageEventMatcher m = new DefaultMessageEventMatcher(49);

        mockery.checking(new Expectations() {
            {
                allowing(e).getChannel();   // We don't care about the channel.
                allowing(e).getRemoteAddress(); // We don't care about the remote address.
                one(e).getMessage();
                will(returnValue(buf));
                one(ctx).sendUpstream(with(m));
                one(ctx).sendUpstream(cse);
            } });

        buf.writeBytes(getBytes("=12\00135=X\001108=30\00110=049\001"));

        decoder.messageReceived(ctx, e);

        decoder.channelDisconnected(ctx, cse);

        mockery.assertIsSatisfied();
    }

    class DefaultMessageEventMatcher extends TypeSafeMatcher<DefaultMessageEvent> {
        private int expectedChecksum;

        public DefaultMessageEventMatcher(int expectedChecksum) {
            this.expectedChecksum = expectedChecksum;
        }

        @Override
        public boolean matchesSafely(DefaultMessageEvent e) {
            Assert.assertTrue("Unexpected message event: " + e,e.getMessage() instanceof RawFixMessage);
            RawFixMessage rawFixMessage = (RawFixMessage) e.getMessage();
            Assert.assertEquals(expectedChecksum,rawFixMessage.getChecksum());
            Assert.assertEquals(expectedChecksum,rawFixMessage.computeChecksum());
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    public void testServerDecoder() throws InterruptedException {
        InetSocketAddress localAddress = new InetSocketAddress(9876);

        ChannelFactory serverFactory =
            new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());
        ServerBootstrap serverBootstrap = new ServerBootstrap(serverFactory);
        FixFrameDecoder decoder = new FixFrameDecoder();
        ChannelPipeline pipeline = serverBootstrap.getPipeline();
        pipeline.addLast("decoder", decoder);
        ServerHandler serverHandler = new ServerHandler();
        pipeline.addLast("handler", serverHandler );
        serverBootstrap.setOption("child.tcpNoDelay", true);
        serverBootstrap.setOption("child.keepAlive", true);
        serverBootstrap.bind(localAddress);
        log.info("bound to " + localAddress);

        ChannelFactory clientFactory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ClientBootstrap clientBootstrap = new ClientBootstrap(clientFactory);
        ClientHandler clientHandler = new ClientHandler();
        clientBootstrap.getPipeline().addLast("handler", clientHandler);
        log.info("Connect...");
        ChannelFuture cf = clientBootstrap.connect(localAddress);
        cf.await();
        log.info("Connected.");
        Thread.sleep(1000);
    }


    @ChannelPipelineCoverage("all")
    public class ServerHandler extends SimpleChannelHandler {
        private final Logger log = LoggerFactory.getLogger(ServerHandler.class);

        @Override
        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            log.info("handleUpstream() " + e);
            super.handleUpstream(ctx, e);
        }

        @Override
        public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            log.info("handleDownstream() " + e);
            super.handleDownstream(ctx, e);
        }
    }

    @ChannelPipelineCoverage("all")
    public class ClientHandler extends SimpleChannelHandler {
        private final Logger log = LoggerFactory.getLogger(ClientHandler.class);

        @Override
        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            log.info("handleUpstream() " + e);
            super.handleUpstream(ctx, e);
        }

        @Override
        public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            log.info("handleDownstream() " + e);
            super.handleDownstream(ctx, e);
        }

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelConnected(ctx, e);
            log.info("channelConnected()");
            // Send some stuff...
            ChannelBuffer buf = buffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001whoops8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
            Channel channel = e.getChannel();
            ChannelFuture cf = channel.write(buf);
            log.info("waiting for write...");
            cf.await();
            log.info("done.");
        }
    }
}
