package eg;

import org.jboss.embedded.Bootstrap;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;

/**
 * Example main program.  Very simple deployment.
 * <br>User: Josh
 * Date: Sep 16, 2008
 * Time: 5:47:03 AM
 */
public class Main {
    private static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Bootstrap bootstrap = Bootstrap.getInstance();
            log.info("***** bootstrap *****");
            bootstrap.bootstrap();
            log.info("***** deploy *****");
            bootstrap.deployResourceBases("seam.properties");
            InitialContext ic = new InitialContext();
            HelloWorld hello = (HelloWorld) ic.lookup("HelloWorldEJB/local");
            String response = hello.sayHello("embedded jboss user");
            log.info("EJB says: " + response);

            log.info("***** shutdown *****");
            bootstrap.shutdown();

        } catch (Throwable e) {
            log.error(e,e);
        }
        log.info("done.");
        //System.exit(0);
    }
}
