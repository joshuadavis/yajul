package eg;

import org.jboss.seam.annotations.Name;

import javax.ejb.Stateful;

/**
 * Example SFSB
 * <br>User: Joshua Davis
 * Date: Sep 15, 2007
 * Time: 10:10:08 AM
 */
@Name("helloWorld")
@Stateful
public class HelloWorldBean implements HelloWorld {

    public String sayHello()
    {
        return "hello";
    }
}
