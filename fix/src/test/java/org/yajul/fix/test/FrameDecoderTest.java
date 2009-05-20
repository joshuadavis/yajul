package org.yajul.fix.test;

import junit.framework.TestCase;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.yajul.fix.netty.ChannelBufferHelper.buffer;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import org.yajul.fix.netty.FixFrameDecoder;
import org.yajul.fix.netty.FixHandler;
import org.yajul.fix.netty.ChannelBufferHelper;
import static org.yajul.fix.util.Bytes.getBytes;
import org.jmock.Mockery;
import org.jmock.Expectations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.Arrays;

/**
 * Test the Netty frame decoder for FIX messages.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:22:03 PM
 */
public class FrameDecoderTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(FrameDecoderTest.class);

    private Mockery context = new Mockery();

    public void testBufferHelper() {
        ChannelBuffer buf = buffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
        assertEquals(buf.readerIndex(),0);
        assertEquals(buf.readableBytes(),34);
        int index = indexOf(buf,0, getBytes("8=FIX"));
        assertEquals(index,0);
        index = indexOf(buf,0, getBytes("10="));
        assertEquals(index,27);
        index = indexOf(buf,0, getBytes("blarg"));
        assertEquals(index,-1);
        buf = buffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001whoops8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
        index = indexOf(buf,buf.readerIndex(),getBytes("whoops"));
        assertEquals(index,34);
        buf.readerIndex(34);
        index = indexOf(buf,buf.readerIndex(), getBytes("8=FIX"));
        assertEquals(index,40);

        ChannelBuffer b = ChannelBuffers.dynamicBuffer(32);
        byte[] first = getBytes("abcd");
        b.writeBytes(first);
        assertEquals(b.readableBytes(),4);
        byte[] bytes = ChannelBufferHelper.copyBytes(b);
        assertEquals(Arrays.equals(first,bytes),true);
        byte[] second = getBytes("EFG");
        b.writeBytes(second);
        byte[] bytes2 = ChannelBufferHelper.copyBytes(b);
        byte[] concat = getBytes("abcdEFG");
        assertEquals(Arrays.equals(concat,bytes2),true);
        b.readBytes(first.length);
        byte[] bytes3 = ChannelBufferHelper.copyBytes(b);
        assertEquals(Arrays.equals(second,bytes3),true);
    }

    public void testDecoder() throws Exception {
        // Create the decoder, mock out all the rest.
        FixFrameDecoder decoder = new FixFrameDecoder();
        final ChannelHandlerContext ctx = context.mock(ChannelHandlerContext.class);
        final ChannelStateEvent cse = context.mock(ChannelStateEvent.class);
        final ChannelBuffer buf = ChannelBuffers.dynamicBuffer(32);
        final MessageEvent e = context.mock(MessageEvent.class);

        context.checking(new Expectations() { {
            ignoring(ctx);  // We don't care about ctx.
            allowing(e).getChannel();   // We don't care about the channel.
            allowing(e).getRemoteAddress(); // We don't care about the remote address.
            // Three calls, return the buffer every time.  We'll change the state of buf
            // in the test.
            one(e).getMessage(); will(returnValue(buf));
            one(e).getMessage(); will(returnValue(buf));
            one(e).getMessage(); will(returnValue(buf)); 
        } });
        
        decoder.channelConnected(ctx,cse);

        // Simulate garbage on the front and a fragment in the middle.
        if (log.isDebugEnabled())
           log.debug("first fragment");
        buf.writeBytes(getBytes("garbage8=F"));
        decoder.messageReceived(ctx,e);

        if (log.isDebugEnabled())
           log.debug("second fragment");
        buf.writeBytes(getBytes("IX.4.2\0019"));
        decoder.messageReceived(ctx,e);

        if (log.isDebugEnabled())
           log.debug("third fragment");
        buf.writeBytes(getBytes("=12\00135=X\001108=30\00110=049\001"));
        decoder.messageReceived(ctx,e);

        decoder.channelDisconnected(ctx,cse);

        context.assertIsSatisfied();
    }
}
