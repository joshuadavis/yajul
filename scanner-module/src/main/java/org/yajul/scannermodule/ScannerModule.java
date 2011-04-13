package org.yajul.scannermodule;

import com.google.inject.*;

/**
 * Guice module that scans the classpath.
 * Usage:
 * <ol>
 *     <li>
 *         Add "guice-beans.xml" to the root directory of any JAR that you want scanned for Guice components.
 *     </li>
 *     <li>
 *         Annotate implementation classes and providers with @{@link Bind}.
 *         <ul>
 *             <li>Specify singleton scope by adding the @{@link com.google.inject.Singleton} annotation on the class.</li>
 *         </ul>
 *     </li>
 *     <li>
 *         In the code that creates the Guice injector, create an instance of ScannerModule and add it to the list of
 *         Modules in the call to {@link Guice#createInjector(com.google.inject.Module...)}.
 *     </li>
 * </ol>
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
