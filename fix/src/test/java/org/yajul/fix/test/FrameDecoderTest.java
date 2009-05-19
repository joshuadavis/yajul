package org.yajul.fix.test;

import junit.framework.TestCase;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.yajul.fix.netty.ChannelBufferHelper.buffer;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import org.yajul.fix.netty.FixFrameDecoder;
import org.yajul.fix.netty.FixHandler;
import static org.yajul.fix.util.Bytes.getBytes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Test the Netty frame decoder for FIX messages.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:22:03 PM
 */
public class FrameDecoderTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(FrameDecoderTest.class);

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
    }

    public void testNetty() throws IOException, InterruptedException {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        FixHandler handler = new FixHandler();
        FixFrameDecoder decoder = new FixFrameDecoder();
        bootstrap.getPipeline().addLast("decoder", decoder);
        bootstrap.getPipeline().addLast("handler", handler);
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        InetSocketAddress address = new InetSocketAddress("localhost", 9999);
        bootstrap.bind(address);

        log.debug("Writing message to socket (two fragments)...");
        Socket client = new Socket("localhost",9999);
        OutputStream outputStream = client.getOutputStream();
        outputStream.write(getBytes("garbage8=F"));
        outputStream.write(getBytes("IX.4.2\0019=12\00135=X\001108=30\00110=049\001"));
        outputStream.write(getBytes("schmutz>8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001"));
        outputStream.flush();
        Thread.sleep(100);
        log.debug("Closing socket...");
        client.close();
    }
}
