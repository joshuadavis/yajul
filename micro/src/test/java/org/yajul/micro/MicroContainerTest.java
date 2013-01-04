package org.yajul.micro;

import java.io.IOException;
import java.util.*;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.yajul.micro.annotations.Component;
import com.google.inject.*;
import com.google.inject.name.Names;
import static org.junit.Assert.*;

/**
 * Test microcontainer behavior.
 * <br>User: Joshua Davis
 * Date: Mar 6, 2008
 * Time: 6:34:05 AM
 */
public class MicroContainerTest {

    @Test
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

        assertSame(one, two);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testAnnotations() {
        AnnotationScanner scanner = new AnnotationScanner("test-bootstrap.properties");
        scanner.addAnnotation(Component.class);
        Collection<String> names = scanner.getNames();
        System.out.println(names);
        Assert.assertTrue(names.contains("org/yajul/micro/AnnotatedComponent.class"));
    }

    @Test
    public void testComponentScanner() {
        ComponentScanner scanner = new ComponentScanner("test-bootstrap.properties");
        Injector injector = scanner.createInjector();
        assertNotNull(injector.getInstance(AnnotatedComponent.class));
        assertNotNull(injector.getInstance(TestThing.class));
    }

    @Test
    public void testSingletonManager() {
        SingletonManager sm = SingletonManager.getInstance();
        SingletonManager other = sm.getComponent(SingletonManager.class);
        assertSame(sm,other);
    }

    @Test
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
        private FluxCapacitor defaultFlux;

        @Inject
        public void setDefaultFlux(FluxCapacitor defaultFlux) {
            this.defaultFlux = defaultFlux;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return super.invoke(invocation);
        }
    }

    @Test
    public void testBeforeAndAfterMethods() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(Delorian.class).in(Scopes.SINGLETON);
                bind(TimeMachine.class).to(Delorian.class);
                final DelorianInterceptor interceptor = new DelorianInterceptor();
                ModuleHelper.bindAndInjectInterceptor(binder(),interceptor,TimeMachine.class,Delorian.class);
            }
        });

        TimeMachine timeMachine = injector.getInstance(TimeMachine.class);
        timeMachine.getDestinationYear();

        Delorian delorian = injector.getInstance(Delorian.class);
        assertEquals(1,delorian.getBefore());
        assertEquals(1,delorian.getAfter());
        assertEquals(0,delorian.getExcep());
    }
}
