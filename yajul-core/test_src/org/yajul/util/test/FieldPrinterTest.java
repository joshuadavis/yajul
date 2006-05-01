package org.yajul.util.test;

import java.util.Date;

import junit.framework.TestCase;

import org.yajul.util.FieldPrinter;

/**
 * Test the field printer class.
 * <hr>
 * User: jdavis<br>
 * Date: May 28, 2004<br>
 * Time: 3:48:15 PM<br>
 * @author jdavis
 */
public class FieldPrinterTest extends TestCase
{
    public FieldPrinterTest(String name)
    {
        super(name);
    }

    /**
     * Test the field printer.
     */
    public void testFieldPrinter() throws Exception
    {
        Foo o = new Foo();
        String s = FieldPrinter.toString(o);
        FieldPrinter fp = new FieldPrinter();
        fp.append(o);
        fp.append(null);
        s = fp.toString();
        System.out.println(s);
        Foo.getSomfin(); // Just to exercise the method.
        fp.append(new Nope());
        fp.append(new Kaboom());
    }

    public static class Foo
    {
        private int i = 42;
        private double d = 3.1415;
        private Date theDate = new Date(0);
        private String s = "test";
        private Bar bar = new Bar();
        private Bar other = null;
        private Baz baz = new Baz();

        public static int getSomfin()
        {
            return 999;
        }

        public Baz getBaz()
        {
            return baz;
        }

        public boolean isFoo()
        {
            return true;
        }

        public Bar getOther()
        {
            return other;
        }

        public int getI()
        {
            return i;
        }

        public double getD()
        {
            return d;
        }

        public Date getTheDate()
        {
            return theDate;
        }

        public String getS()
        {
            return s;
        }

        public Bar getBar()
        {
            return bar;
        }
    }

    public static class Bar
    {
        public int[] quimby = new int[] { 1, 2, 3 };
        public boolean bart = false;
    }

    private static class Nope
    {
        public int[] quimby = new int[] { 1, 2, 3 };
        public boolean bart = false;
    }

    public static class Baz
    {
        private int duh = 444;

        public String toString()
        {
            return "duh = " + duh;
        }
    }

    public static class Kaboom
    {

        public String toString()
        {
            throw new RuntimeException("boom!");
        }
    }


}
