/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 11:20:36 AM
 */
package org.yajul.io.test;

import junit.framework.TestCase;
import org.yajul.io.ObjectStreamHelper;

import java.io.IOException;

public class ObjectStreamHelperTest extends TestCase
{
    public ObjectStreamHelperTest(String name)
    {
        super(name);
    }

    public void testReadWrite() throws IOException, ClassNotFoundException
    {
        String x = "hello there!";
        byte[] bytes = ObjectStreamHelper.serialize(x);
        assertTrue(bytes != null);
        assertTrue(bytes.length > 0);
        String y = (String)ObjectStreamHelper.deserialize(bytes);
        assertEquals(x,y);
    }
}
