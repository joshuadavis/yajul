package org.yajul.scannermodule;

import com.google.inject.*;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Class level comments!
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:13 PM
 */
public class ScannerModuleTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(ScannerModuleTest.class);

    public void testAnnotationScanner() {
        Module module = new ScannerModule(this.getClass().getClassLoader());
        Injector injector = Guice.createInjector(module);
        ExampleImplOne one = injector.getInstance(ExampleImplOne.class);
        Foo foo = injector.getInstance(Foo.class);
    }
}
