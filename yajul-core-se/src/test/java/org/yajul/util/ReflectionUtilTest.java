// $Id$

package org.yajul.util;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.net.URL;

/**
 * Tests ReflectionUtil
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ReflectionUtilTest extends TestCase {
    private static final Class<?> THING_CLASS = ObjectFactoryTest.Thing.class;
    private static final String THING_CLASS_NAME = THING_CLASS.getName();

    public ReflectionUtilTest(String name) {
        super(name);
    }

    public static interface Face {
        void one();

        void two();
    }

    public static class Bar implements Face {
        public void one() {
        }

        public void two() {
        }

        public void specific() {
        }
    }

    public static class Baz {
        public void one() {
        }
    }

    public void testImplementationOf() throws Exception {
        Method[] methods = Bar.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("one") || method.getName().equals("two"))
                assertTrue(ReflectionUtil.isDefinedIn(method, Face.class));
            else
                assertFalse(ReflectionUtil.isDefinedIn(method, Face.class));
        }

        methods = Baz.class.getMethods();
        for (Method method : methods) {
            assertFalse(ReflectionUtil.isDefinedIn(method, Face.class));
        }
    }

    public void testConstantMap() throws Exception {
        Map map = ReflectionUtil.getConstantNameMap(Constants.class);
        assertEquals(3, map.size());
        assertEquals("ONE", map.get(1));
        assertEquals("TWO", map.get(2));
        assertEquals("THREE", map.get(3));
        map = ReflectionUtil.getConstantNameMap(Foo.class);
        assertEquals(0, map.size());
    }

    public void testPropertyGetter() throws Exception {
        Foo.getSomfin();
        Foo.setSomfin(999);
        Foo f = new Foo();
        f.getD();
        f.getI();
        f.getS();
        f.getTheDate();
        f.isFoo();
        f.setI(33);

        Method[] methods = Foo.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            // Exercise the functions on all methods.
            ReflectionUtil.getterPropertyName(method);
            ReflectionUtil.setterPropertyName(method);
            // Make specific assertions.
            if (method.getName().equals("getI")) {
                String s = ReflectionUtil.getterPropertyName(method);
                assertTrue(ReflectionUtil.isPropertyGetter(method));
                assertEquals("i", s);
            }
            else if (method.getName().equals("setI")) {
                String s = ReflectionUtil.setterPropertyName(method);
                assertEquals("i", s);
            }
        }
    }

    public void testLoadClass() {
        Class<?> c = ReflectionUtil.loadClass(THING_CLASS_NAME);
        assertSame(THING_CLASS,c);
    }
    
    public void testObjectCreation() {
        ObjectFactoryTest.Thing t = (ObjectFactoryTest.Thing) ReflectionUtil.createInstance(THING_CLASS_NAME);
        TestCase.assertEquals(42, t.getValue());

        t = (ObjectFactoryTest.Thing) ReflectionUtil.createInstance(THING_CLASS_NAME);
        TestCase.assertEquals(42, t.getValue());

        URL url = ReflectionUtil.findClassURL(THING_CLASS);
        assertNotNull(url);
    }

    public static interface Constants {
        public static final int ONE = 1;
        public static final int TWO = 2;
        public static final int THREE = 3;
        public static final String DUH = "duh!";

        void dummy();
    }

    public static class Foo {
        public int i = 42;
        public double d = 3.1415;
        private Date theDate = new Date(0);
        private String s = "test";

        public static int getSomfin() {
            return 999;
        }

        @SuppressWarnings({"UnusedDeclaration"})
        public static void setSomfin(int bogus) {
        }

        public boolean isFoo() {
            return true;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public double getD() {
            return d;
        }

        public Date getTheDate() {
            return theDate;
        }

        public String getS() {
            return s;
        }
    }
}
