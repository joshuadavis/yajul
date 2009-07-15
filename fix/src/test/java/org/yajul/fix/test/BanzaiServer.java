package org.yajul.fix.test;

import org.yajul.fix.FixAddress;
import org.yajul.fix.netty.FixServerPipelineFactory;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

/**
 * Server for the QFJ Banzai client.
 * <br>
 * User: josh
 * Date: Jul 15, 2009
 * Time: 1:43:01 AM
 */
public class BanzaiServer {
    private static final Logger log = LoggerFactory.getLogger(BanzaiServer.class);

    private FixAddress fix40 = new FixAddress("localhost",9876,"FIX.4.0");
    private FixAddress fix41 = new FixAddress("localhost",9877,"FIX.4.1");

    public void go() {
        ChannelFactory serverFactory =
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());
        ServerBootstrap serverBootstrap = new ServerBootstrap(serverFactory);
        FixServerPipelineFactory pipelineFactory = new FixServerPipelineFactory();
        serverBootstrap.setPipelineFactory(pipelineFactory);
        serverBootstrap.setOption("child.tcpNoDelay", true);
        serverBootstrap.setOption("child.keepAlive", true);
        serverBootstrap.bind(fix40.getSocketAddress());
        serverBootstrap.bind(fix41.getSocketAddress());
        log.info("bound");

    }
    public static void main(String[] args) {
        BanzaiServer server = new BanzaiServer();
        server.go();
    }
}
