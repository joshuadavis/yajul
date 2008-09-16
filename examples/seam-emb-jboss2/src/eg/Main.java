package eg;

import org.jboss.embedded.Bootstrap;

import javax.naming.InitialContext;

/**
 * TODO: Add class level javadoc.
 * <br>User: Josh
 * Date: Sep 16, 2008
 * Time: 5:47:03 AM
 */
public class Main {
    public static void main(String[] args) {
        try {
            Bootstrap bootstrap = Bootstrap.getInstance();
            System.out.println("***** bootstrap *****");
            bootstrap.bootstrap();
            System.out.println("***** deploy *****");
            bootstrap.deployResourceBases("seam.properties");
            InitialContext ic = new InitialContext();
            HelloWorld hello = (HelloWorld) ic.lookup("HelloWorldEJB/local");
            String response = hello.sayHello("embedded jboss user");
            System.out.println("EJB says: " + response);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
