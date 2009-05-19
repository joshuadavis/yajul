package org.yajul.fix.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.yajul.fix.util.Bytes;

/**
 * Helper methods for Netty ChannelBuffer.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:27:41 PM
 */
public class ChannelBufferHelper {
    public static ChannelBuffer buffer(String s) {
        return ChannelBuffers.copiedBuffer(Bytes.getBytes(s));
    }
    
    public static int indexOf(ChannelBuffer buf,int start, byte[] bytes) {
        int end = buf.readableBytes() - bytes.length;
        if (end < 0)
            return -1;
        byte b = bytes[0];
        for (int i = start; i < end; i++) {
            byte b1 = buf.getByte(i);
            if (b1 == b && matchBytes(buf,i,bytes)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean matchBytes(ChannelBuffer buf, int offset, byte[] bytes) {
        for (int i = 0; i < bytes.length ; i++) {
            if (buf.getByte(offset + i) != bytes[i])
                return false;
        }
        return true;
    }
}
