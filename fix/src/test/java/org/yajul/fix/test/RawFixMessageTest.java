package org.yajul.fix.test;

import junit.framework.TestCase;
import org.yajul.fix.util.Bytes;
import org.yajul.fix.util.ByteCharSequence;
import org.yajul.fix.RawFixMessage;

/**
 * Tests message parse/create with RawFixMessage.
 * <br>
 * User: josh
 * Date: Jul 17, 2009
 * Time: 8:00:42 AM
 */
public class RawFixMessageTest extends TestCase {
    public void testParseAndCreate() throws Exception {
        byte[] bytes1 = Bytes.getBytes(Fix44Examples.EXAMPLE);
        CharSequence cs = new ByteCharSequence(bytes1);
        assertEquals(cs.length(),bytes1.length);
        for (int i = 0 ; i < cs.length() ; i++) {
            char a = (char)bytes1[i];
            char b = cs.charAt(i);
            assertEquals(a,b);
        }

        cs = cs.subSequence(2,5);
        StringBuilder sb = new StringBuilder();
        sb.append(cs);
        String s = sb.toString();
        assertEquals(s,"FIX");
        RawFixMessage message = new RawFixMessage(bytes1);
        System.out.println("message: " + message);
        assertEquals(message.getMessageType().getStringValue(),"X");
    }
}
