package org.yajul.weldse.example1;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * A simple Weld SE app.
 * <br>
 * User: Josh
 * Date: 1/20/11
 * Time: 11:19 PM
 */
public class SimpleApp
{
    public static void main(String[] args)
    {
        try
        {
            final Weld weld = new Weld();
            WeldContainer container = weld.initialize();
            SimpleApp app = container.instance().select(SimpleApp.class).get();
            app.sayHello();
            weld.shutdown();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void sayHello()
    {
        System.out.println("Hello world!");
    }
}
