package org.yajul.io;

import junit.framework.TestCase;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests object serialization utilities.
 * <br>
 * User: josh
 * Date: Aug 21, 2009
 * Time: 4:31:41 PM
 */
public class SerializationTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(SerializationTest.class);

    public void testObjectSerialization() throws Exception {
        Foo f = new Foo("one",1);
        byte[] bytes = SerializationUtil.toByteArray(f);
        assertNotNull(bytes);
        Foo f2 = (Foo) SerializationUtil.fromByteArray(bytes);
        assertNotNull(f2);
        assertEquals(f,f2);
        assertNotSame(f,f2);
        int size = SerializationUtil.sizeOf(f);
        log.debug("size = " + size);    
        assertEquals(size, bytes.length);
    }

    private static class Foo implements Serializable {
        private String name;
        private int number;

        private Foo(String name, int number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Foo foo = (Foo) o;

            if (number != foo.number) return false;
            if (name != null ? !name.equals(foo.name) : foo.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + number;
            return result;
        }
    }
}
