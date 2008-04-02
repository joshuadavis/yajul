package eg;

import javax.ejb.Local;

/**
 * Example SFSB local interface
 * <br>User: Joshua Davis
 * Date: Sep 15, 2007
 * Time: 10:17:34 AM
 */
@Local
public interface HelloWorld {
    String sayHello();
}
