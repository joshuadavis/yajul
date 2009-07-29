package org.yajul.fix.netty;

import org.jboss.netty.channel.*;
import org.yajul.fix.RawFixMessage;
import static org.yajul.fix.netty.ChannelBufferHelper.buffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A FIX server handler.
 * <br>
 * User: josh
 * Date: Jul 15, 2009
 * Time: 1:50:27 AM
 */
@ChannelPipelineCoverage("one")
public class FixServerHandler extends SimpleChannelHandler {
    private static final Logger log = LoggerFactory.getLogger(FixServerHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log.info("SERVER - messageReceived() " + e);
        Object obj = e.getMessage();
        if (obj instanceof RawFixMessage) {
            RawFixMessage rawFixMessage = (RawFixMessage) obj;
            switch (rawFixMessage.getMessageTypeEnum()) {
                case LOGON:  onLogon(rawFixMessage);
            }
            log.info(rawFixMessage.toString());
            Channel channel = e.getChannel();
            if (channel.isWritable()) {
                e.getChannel().write(buffer("OK"));
            }
        }
    }

    private void onLogon(RawFixMessage rawFixMessage) {
        log.info("LOGON: " + rawFixMessage);
        // Respond with a logon message.

    }

}
