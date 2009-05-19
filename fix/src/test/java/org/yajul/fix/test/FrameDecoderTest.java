package org.yajul.fix.test;

import junit.framework.TestCase;
import org.jboss.netty.buffer.ChannelBuffer;
import static org.yajul.fix.netty.ChannelBufferHelper.buffer;
import static org.yajul.fix.netty.ChannelBufferHelper.indexOf;
import static org.yajul.fix.util.Bytes.getBytes;

/**
 * Test the Netty frame decoder for FIX messages.
 * <br>
 * User: josh
 * Date: May 19, 2009
 * Time: 1:22:03 PM
 */
public class FrameDecoderTest extends TestCase {
    public void testFrameDecoder() {
        ChannelBuffer buf = buffer("8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001");
        assertEquals(buf.readerIndex(),0);
        assertEquals(buf.readableBytes(),34);
        int index = indexOf(buf,0, getBytes("8=FIX"));
        assertEquals(index,0);
        index = indexOf(buf,0, getBytes("10="));
        assertEquals(index,27);
        index = indexOf(buf,0, getBytes("blarg"));
        assertEquals(index,-1);
    }
}
