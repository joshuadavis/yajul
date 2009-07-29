package org.yajul.fix.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Jul 15, 2009
 * Time: 1:48:40 AM
 */
public class FixServerPipelineFactory implements ChannelPipelineFactory {
    private static final Logger log = LoggerFactory.getLogger(FixServerPipelineFactory.class);

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        FixServerHandler serverHandler = new FixServerHandler();
        FixFrameDecoder decoder = new FixFrameDecoder();
        pipeline.addLast("decoder", decoder);
        pipeline.addLast("handler", serverHandler);
        return pipeline;
    }
}
