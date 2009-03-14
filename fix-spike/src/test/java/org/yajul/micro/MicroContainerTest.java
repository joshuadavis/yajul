package org.yajul.micro;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.io.IOException;
import java.util.*;

import org.yajul.micro.annotations.Component;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Test microcontainer behavior.
 * <br>User: Joshua Davis
 * Date: Mar 6, 2008
 * Time: 6:34:05 AM
 */
public class MicroContainerTest extends TestCase {
    public MicroContainerTest(String n) {
        super(n);
    }

    public void testSingleton()
    {
        ModuleList modules = new ModuleList();
        modules.add(new AbstractModule() {
            protected void configure() {
                bind(List.class).to(ArrayList.class).in(Scopes.SINGLETON);
            }
        });
        MicroContainer mc = new MicroContainer(modules.createInjector());

        List one = mc.getComponent(List.class);
        List two = mc.getComponent(List.class);

        Assert.assertSame(one,two);
    }

    public void testNamedConstant()
    {
        ModuleList modules = new ModuleList();
        modules.add(new AbstractModule() {
            protected void configure() {
                bind(Integer.class).annotatedWith(Names.named("magicNumber")).toInstance(42);
            }
        });
        MicroContainer mc = new MicroContainer(modules.createInjector());
        assertEquals(mc.getComponent(Integer.class,"magicNumber").intValue(),42);
    }

    public void testModuleList() throws Exception {
        ModuleList modules = new ModuleList();
        modules.addClassName("org.yajul.micro.TestConfig");
        TestThing theThing = new TestThing();
        modules.addInstance(TestThing.class,theThing);
        assertTrue(modules.size() > 0);
        MicroContainer mc = new MicroContainer(modules.createInjector());
        assertEquals(TreeSet.class,mc.getComponent(Set.class).getClass());
        assertSame(theThing,mc.getComponent(TestThing.class));
    }
    
    public void testResourceModule() throws IOException {
        // MicroContainer can bootstrap itself from properties files.
        ModuleList modules = new ModuleList();
        modules.add(new ResourceModule("test-bootstrap.properties"));
        MicroContainer mc = new MicroContainer(modules.createInjector());
        System.out.println(mc);
        assertEquals(HashSet.class,mc.getComponent(Collection.class).getClass()); 
        assertEquals(TreeSet.class,mc.getComponent(Set.class).getClass());
        assertEquals(Delorian.class,mc.getComponent(TimeMachine.class).getClass());
        assertEquals(1985,mc.getComponent(TimeMachine.class).getDestinationYear());
    }

    public void testAnnotations() {
        AnnotationScanner scanner = new AnnotationScanner("test-bootstrap.properties");
        scanner.addAnnotation(Component.class);
        Collection<String> names = scanner.getNames();
        System.out.println(names);
        Assert.assertTrue(names.contains("org/yajul/micro/AnnotatedComponent.class"));
    }

    public void testComponentScanner() {
        ComponentScanner scanner = new ComponentScanner("test-bootstrap.properties");
        Injector injector = scanner.createInjector();
        assertNotNull(injector.getInstance(AnnotatedComponent.class));
        assertNotNull(injector.getInstance(TestThing.class));
    }

    public void testSingletonManager() {
        SingletonManager sm = SingletonManager.getInstance();
        SingletonManager other = sm.getComponent(SingletonManager.class);
        assertSame(sm,other);
    }
    
    public static Test suite() {
        return new TestSuite(MicroContainerTest.class);
    }
}
