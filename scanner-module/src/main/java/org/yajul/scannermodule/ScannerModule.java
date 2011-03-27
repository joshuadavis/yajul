package org.yajul.scannermodule;

import com.google.inject.*;

/**
 * Guice module that scans the classpath.
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:23 PM
 */
public class ScannerModule extends AbstractModule {
    public static final String RESOURCE_NAME = "guice-beans.xml";

    private ClassLoader classLoader;

    public ScannerModule(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected void configure() {
        // Create the annotation scanner.
        BindingScanner scanner = new BindingScanner(binder(), classLoader);
        // Scan for all components and bind them.
        scanner.go();
    }

}
