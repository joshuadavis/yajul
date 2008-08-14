package org.yajul.log;

import junit.framework.TestCase;

import java.io.*;

/**
 * Test the Log4JDebugProxy.
 * <br>
 * User: josh
 * Date: Aug 12, 2008
 * Time: 12:37:39 PM
 */
public class Log4JDebugProxyTest extends TestCase {

    public static class Thingie implements Serializable {
        private int one;
        private String two;
        public Thingie(int one, String two) {
            this.one = one;
            this.two = two;
        }

        public int getOne() {
            return one;
        }

        public String getTwo() {
            return two;
        }


        public String toString() {
            return "Thingie{" +
                    "one=" + one +
                    ", two='" + two + '\'' +
                    '}';
        }
    }
    public void testObjectStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        ObjectOutput out = Log4JDebugProxy.create(ObjectOutput.class,oos);
        Thingie t = new Thingie(42,"fourty two");
        out.writeObject(t);
        out.close();
    }
}
