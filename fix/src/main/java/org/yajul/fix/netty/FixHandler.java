package org.yajul.fix.netty;

import org.jboss.netty.channel.*;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

/**
 * Netty FIX handler.
 * <br>
 * User: josh
 * Date: May 18, 2009
 * Time: 4:26:03 PM
 */
@ChannelPipelineCoverage("all")
public class FixHandler extends SimpleChannelHandler {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(FixHandler.class);

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
        if (log.isDebugEnabled())
           log.debug("channelOpen() : " + e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
        if (log.isDebugEnabled())
           log.debug("channelClosed() : " + e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        if (log.isDebugEnabled())
           log.debug("channelConnected() : " + e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        if (log.isDebugEnabled())
           log.debug("channelDisconnected() : " + e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
        if (log.isDebugEnabled())
           log.debug("messageReceived() : " + e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.warn("exeptionCaught() : " + e);
        super.exceptionCaught(ctx, e);
    }
}
