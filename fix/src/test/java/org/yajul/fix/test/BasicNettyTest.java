package org.yajul.fix.test;

import junit.framework.TestCase;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.yajul.fix.netty.ChannelBufferHelper;
import org.yajul.fix.util.LockHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.net.InetSocketAddress;

/**
 * Basic NETTY test, just as an example.
 * <br>
 * User: josh
 * Date: Jul 10, 2009
 * Time: 8:26:34 AM
 */
public class BasicNettyTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(BasicNettyTest.class);


    public void testEcho() throws Exception {
        String host = "localhost";
        int port = 19998;

        // Configure the server.
        ChannelFactory serverFactory =
            new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

        ServerBootstrap serverBootstrap = new ServerBootstrap(serverFactory);
        EchoServerHandler serverHandler = new EchoServerHandler();

        serverBootstrap.getPipeline().addLast("handler", serverHandler);
        serverBootstrap.setOption("child.tcpNoDelay", true);
        serverBootstrap.setOption("child.keepAlive", true);

        // Bind and start to accept incoming connections.
        serverBootstrap.bind(new InetSocketAddress(host,port));

        // Configure the client.
        ChannelFactory clientFactory =
            new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

        ClientBootstrap clientBootstrap = new ClientBootstrap(clientFactory);
        EchoClientHandler clientHandler = new EchoClientHandler();

        clientBootstrap.getPipeline().addLast("handler", clientHandler);
        clientBootstrap.setOption("tcpNoDelay", true);
        clientBootstrap.setOption("keepAlive", true);

        // Start the connection attempt.
        clientBootstrap.connect(new InetSocketAddress(host, port));

        clientHandler.waitUntilFinished();

    }

    @ChannelPipelineCoverage("all")    
    public class EchoServerHandler extends SimpleChannelHandler {
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            log.info("SERVER - messageReceived() " + e);
            e.getChannel().write(e.getMessage());
            // super.messageReceived(ctx, e);
        }
    }

    @ChannelPipelineCoverage("all")
    public class EchoClientHandler extends SimpleChannelHandler {

        private ReentrantLock lock = new ReentrantLock();
        private Condition finished = lock.newCondition();

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
            sendData(e);
        }

        @Override
        public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) {
            sendData(e);
        }

        private void sendData(ChannelStateEvent e) {
            log.info("Sending data...");
            Channel channel = e.getChannel();
            if (channel.isWritable()) {
                ChannelBuffer buf = ChannelBufferHelper.buffer("hello!");
                channel.write(buf);
            }
        }


        @Override
        public void messageReceived(
                ChannelHandlerContext ctx, final MessageEvent e) {
            log.info("CLIENT - messageRecieved() " + e.getMessage());
            LockHelper.signal(lock,finished);
        }

        @Override
        public void exceptionCaught(
                ChannelHandlerContext ctx, ExceptionEvent e) {
            log.warn(
                    "Unexpected exception from downstream.",
                    e.getCause());
            e.getChannel().close();
        }



        public void waitUntilFinished() throws InterruptedException {
            log.info("CLIENT - waiting...");
            LockHelper.await(lock,finished,10,TimeUnit.SECONDS);
        }
    }
}
