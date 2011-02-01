/**
 * TODO: Add class level javadoc.
 * <br>User: Josh
 * Date: Sep 16, 2008
 * Time: 5:36:50 AM
 */
package eg;

import javax.ejb.Stateless;

@Stateless(name = "HelloWorldEJB")
public class HelloWorldBean implements HelloWorld {
    public HelloWorldBean() {
    }

    public String sayHello() {
        System.out.println("Saying hello...");
        return "Hello world!";
    }
}
