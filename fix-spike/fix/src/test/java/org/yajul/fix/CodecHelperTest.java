package org.yajul.fix;

import junit.framework.TestCase;
import org.junit.Test;
import org.apache.mina.core.buffer.IoBuffer;
import static org.yajul.fix.CodecHelper.getBytes;

/**
 * Test the codec helper.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:51:47 PM
 */
public class CodecHelperTest extends TestCase {
    private static final String  MESSAGE = "8=FIX.4.4\0019=12\00135=X\001108=30\00110=036\001";

    @Test
    public void testCodecHelper() {
        IoBuffer buf = IoBuffer.wrap(getBytes("abcd" + MESSAGE));
        assertEquals(4, CodecHelper.indexOf(buf, getBytes("8=FIX")));
        assertEquals(14, CodecHelper.indexOf(buf, getBytes("9=")));
        assertEquals(-1, CodecHelper.indexOf(buf, getBytes(MESSAGE + "not here")));

        buf = IoBuffer.wrap(getBytes("9=12\001"));
        assertEquals(12,CodecHelper.parseDigits(buf,2,2));
        assertEquals(12,CodecHelper.parseDigits(buf,2,3));
        assertEquals(9,CodecHelper.parseDigits(buf,0,2));


        buf = IoBuffer.allocate(32);
        buf.setAutoExpand(true);
        buf.put(getBytes("howdy"));
        buf.put(getBytes(MESSAGE));
        byte[] out = CodecHelper.getBytes(buf);
        String x = new String(out);
        assertEquals("howdy" + MESSAGE,x);
    }

}
