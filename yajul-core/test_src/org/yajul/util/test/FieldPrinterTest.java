package org.yajul.util.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Date;

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
        System.out.println("\n" + s);
        FieldPrinter fp = new FieldPrinter();
        fp.append(o);
        System.out.println("\n" + fp.toString());

    }

    public static class Foo
    {
        public int i = 42;
        public double d = 3.1415;
        public Date theDate = new Date(0);
        public String s = "test";
        public Bar bar = new Bar();

        public int getI()
        {
            return i;
        }

        public void setI(int i)
        {
            this.i = i;
        }

        public double getD()
        {
            return d;
        }

        public void setD(double d)
        {
            this.d = d;
        }

        public Date getTheDate()
        {
            return theDate;
        }

        public void setTheDate(Date theDate)
        {
            this.theDate = theDate;
        }

        public String getS()
        {
            return s;
        }

        public void setS(String s)
        {
            this.s = s;
        }

        public Bar getBar()
        {
            return bar;
        }

        public void setBar(Bar bar)
        {
            this.bar = bar;
        }
    }

    public static class Bar
    {
        public int[] quimby = new int[] { 1, 2, 3 };
    }
}
