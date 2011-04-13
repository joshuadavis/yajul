package org.yajul.scannermodule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test ScannerModule.
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
        ExampleInterface one = injector.getInstance(ExampleInterface.class);
        assertNotNull(one);
        log.info("one=" + one);
        ExampleInterface two = injector.getInstance(Key.get(ExampleInterface.class, Two.class));
        Foo foo = injector.getInstance(Foo.class);
        log.info("foo=" + foo);
        assertNotNull(foo);
    }
}
