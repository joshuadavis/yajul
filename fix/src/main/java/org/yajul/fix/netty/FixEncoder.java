package org.yajul.fix.netty;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import org.yajul.fix.message.RawFixMessage;

/**
 * Transforms RawFixMessage objects into ChannelBuffer bytes.
 * <br>
 * User: josh
 * Date: Jul 13, 2009
 * Time: 3:54:02 PM
 */
public class FixEncoder implements ChannelDownstreamHandler
{
    public void handleDownstream(ChannelHandlerContext context, ChannelEvent evt) throws Exception
    {
        if (!(evt instanceof MessageEvent))
        {
            context.sendDownstream(evt);
            return;
        }

        MessageEvent e = (MessageEvent) evt;
        Object o = e.getMessage();
        if (o instanceof RawFixMessage)
        {
            RawFixMessage rawFixMessage = (RawFixMessage) o;
            byte[] bytes = rawFixMessage.getBytes();
            write(context, e.getChannel(), e.getFuture(), copiedBuffer(bytes));
        }
        else
        {
            context.sendDownstream(evt);
        }

    }
}
