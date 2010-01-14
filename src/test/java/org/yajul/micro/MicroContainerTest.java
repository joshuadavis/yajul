package org.yajul.micro;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.aopalliance.intercept.MethodInvocation;
import org.yajul.micro.annotations.Component;
import com.google.inject.*;
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
        modules.add(new PropertiesResourceModule("test-bootstrap.properties"));
        MicroContainer mc = new MicroContainer(modules.createInjector());
        System.out.println(mc);
        assertEquals(HashSet.class,mc.getComponent(Collection.class).getClass()); 
        assertEquals(TreeSet.class,mc.getComponent(Set.class).getClass());
        assertEquals(Delorian.class,mc.getComponent(TimeMachine.class).getClass());
        assertEquals(1985,mc.getComponent(TimeMachine.class).getDestinationYear());
    }

    public void testXmlModule() throws IOException {
        // MicroContainer can bootstrap itself from properties files.
        ModuleList modules = new ModuleList();
        modules.add(new XmlResourceModule("test-modules.xml"));
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

    public void testAbstractCachingProvider() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(TestThing.class).toProvider(new AbstractCachingProvider<TestThing>() {
                    protected TestThing create() {
                        return new TestThing();
                    }
                });
            }
        });

        TestThing.counter.set(0);
        Provider<TestThing> provider =  injector.getProvider(TestThing.class);
        assertEquals(0,TestThing.counter.get());
        TestThing t = provider.get();
        assertEquals(1,TestThing.counter.get());
        TestThing u = provider.get();
        assertEquals(1,TestThing.counter.get());
        assertSame(t,u);
    }

    private class DelorianInterceptor extends MethodWrapperInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return super.invoke(invocation);
        }
    }

    public void testBeforeAndAfterMethods() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(Delorian.class).in(Scopes.SINGLETON);
                bind(TimeMachine.class).to(Delorian.class);
                bindInterceptor(
                        Matchers.identicalTo(Delorian.class),
                        MethodWrapperInterceptor.onlyDefinedIn(TimeMachine.class),
                        new DelorianInterceptor());
            }
        });

        TimeMachine timeMachine = injector.getInstance(TimeMachine.class);
        timeMachine.getDestinationYear();

        Delorian delorian = injector.getInstance(Delorian.class);
        assertEquals(1,delorian.getBefore());
        assertEquals(1,delorian.getAfter());
        assertEquals(0,delorian.getExcep());
    }
    
    public static Test suite() {
        return new TestSuite(MicroContainerTest.class);
    }
}
