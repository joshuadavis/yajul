package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.Bytes;

import java.util.Arrays;

/**
 * Test the Bytes class.
 * <hr>
 * User: jdavis<br>
 * Date: May 28, 2004<br>
 * Time: 2:51:57 PM<br>
 * @author jdavis
 */
public class BytesTest extends TestCase
{
    public BytesTest(String name)
    {
        super(name);
    }

    /**
     * Test conversion to byte arrays.
     */
    public void testByteArrays() throws Exception
    {
        byte[] value = new byte[] { (byte)0x49, (byte)0x96, (byte)0x02, (byte)0xd2 };
        byte[] bytes = new byte[4];
        Bytes.toBytes(1234567890,bytes);
        assertTrue(Arrays.equals(value,bytes));
        value = new byte[] { (byte)0x11, (byte)0x22, (byte)0x10, (byte)0xf4, (byte)0x7d, (byte)0xe9, (byte)0x81, (byte)0x15 };
        bytes = new byte[8];
        Bytes.toBytes(1234567890123456789L,bytes);
        assertTrue(Arrays.equals(value,bytes));
        byte[] hexBytes = new byte[16];
        Bytes.hexBytes(Bytes.HEX_BYTES_UPPER,bytes,hexBytes,8);
        assertEquals("112210F47DE98115",new String(hexBytes));
        bytes = Bytes.parseHex("112210F47DE98115");
        assertTrue(Arrays.equals(value,bytes));
        byte[] b = new byte[2];
        Bytes.hexBytes(Bytes.HEX_BYTES_UPPER,0xfe,b);
        byte[] val = "FE".getBytes();
        assertTrue(Arrays.equals(val,b));
        bytes = Bytes.parseHex("");
        assertEquals(0,bytes.length);
    }

    public void testException()
    {
        IllegalArgumentException iae = null;
        try
        {
            Bytes.parseHex("qqq");
        }
        catch (IllegalArgumentException e)
        {
            iae = e;
        }
        assertNotNull(iae);
        iae = null;
        try
        {
            Bytes.parseHex("qq");
        }
        catch (IllegalArgumentException e)
        {
            iae = e;
        }
        assertNotNull(iae);
    }
}
