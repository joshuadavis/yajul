package org.yajul.fix.netty;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;

/**
 * Netty PipelineFactory for a FIX client (initiator).
 * <br>
 * User: josh
 * Date: Jul 13, 2009
 * Time: 3:50:43 PM
 */
public class FixClientPipelineFactory implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder",new FixFrameDecoder());
        pipeline.addLast("encoder",new FixEncoder());
        return pipeline;
    }
}
