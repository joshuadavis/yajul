package org.yajul.fix.netty;

import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelPipelineCoverage;

/**
 * Netty FIX handler.
 * <br>
 * User: josh
 * Date: May 18, 2009
 * Time: 4:26:03 PM
 */
@ChannelPipelineCoverage("all")
public class FixHandler extends SimpleChannelHandler {
    
}
