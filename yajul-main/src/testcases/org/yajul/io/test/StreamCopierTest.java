/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 22, 2002
 * Time: 12:38:27 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.io.test;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.yajul.io.StreamCopier;

public class StreamCopierTest extends TestCase
{
    public StreamCopierTest(String name)
    {
        super(name);
    }

    public void testStreamCopy()
    {
        byte[] a = "12345678901234567890".getBytes();
        InputStream in = new ByteArrayInputStream(a);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamCopier copier = new StreamCopier(in,out);
        copier.run();
        Arrays.equals(a,out.toByteArray());
    }


}
