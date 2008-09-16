package eg;

import javax.ejb.Local;

/**
 * TODO: Add class level javadoc.
 * <br>User: Josh
 * Date: Sep 16, 2008
 * Time: 5:43:37 AM
 */
@Local
public interface HelloWorld {
    String sayHello(String name);
}
