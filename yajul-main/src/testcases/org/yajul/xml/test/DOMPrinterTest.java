package org.yajul.xml.test;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.yajul.xml.DOMPrinter;
import org.yajul.xml.DOMUtil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

/**
 * Unit test for the DOMUtil and DOMPrinter classes.
 */
public class DOMPrinterTest extends TestCase
{
    public DOMPrinterTest(String name)
    {
        super(name);
    }

    public void testPrinter() throws Exception
    {
        Document doc = DOMUtil.createDocument("TestDocument");
        Element root = doc.getDocumentElement();

        Element child = DOMUtil.addChild(doc,root,"NestedElement");
        child.setAttribute("test","one two three");
        Element grandchild = DOMUtil.addChildWithText(doc,child,"SomeText",
                "blah blah blah....");
        grandchild.setAttribute("funky","true");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMPrinter.printNode(doc,baos,true);
        System.out.println("\n" + new String(baos.toByteArray()));
        baos = new ByteArrayOutputStream();
        DOMPrinter.printNode(doc,new OutputStreamWriter(baos),true);
        System.out.println("\n" + new String(baos.toByteArray()));

        DOMPrinter.printNode(doc,System.out,true);

    }
}
