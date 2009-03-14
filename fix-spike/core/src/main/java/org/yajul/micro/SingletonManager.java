package org.yajul.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.ReflectionUtil;

import com.google.inject.*;

/**
 * One singleton to rule them all.  This is actually a single level hierarchy of micro-containers.
 * The parent contains all the YAJUL singletons, and any 'bootstrap' singletons.  A child container
 * named '_DEFAULT' has all the singletons registered using the getDefaultSingleton() method.
 * Other child containers will be created on demand via the getSingletonContainer(String) method.
 * <br>
 * User: josh
 * Date: Mar 5, 2008
 * Time: 10:37:22 AM
 */
public class SingletonManager {

    private Logger log = LoggerFactory.getLogger(SingletonManager.class);

    /**
     * The resource that the bootstrapper looks for.
     */
    public static final String BOOTSTRAP_RESOURCE_NAME = "singleton-bootstrap.properties";

    private static SingletonManager INSTANCE = new SingletonManager();

    /**
     * The parent MicroContainer, contains child containers so you can have different
     * sets of singletons.
     */
    private MicroContainer container;

    public static SingletonManager getInstance() {
        return INSTANCE;
    }

    public static <T> T getSingleton(Class<T> componentType) {
        return getInstance().getComponent(componentType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return getContainer().getComponent(componentType);
    }

    public static MicroContainer container() {
        return getInstance().getContainer();
    }

    private SingletonManager() {
    }

    /**
     * @return the MicroContainer where all the components are registered.
     */
    public MicroContainer getContainer() {
        synchronized (this)
        {
            if (container == null) {
                log.info("Creating micro container...");
                ModuleList modules = new ModuleList();
                modules.addInstance(SingletonManager.class,this);
                modules.add(new ResourceModule(
                        "org/yajul/micro/" + BOOTSTRAP_RESOURCE_NAME, 
                        ReflectionUtil.getCurrentClassLoader(),
                        Scopes.SINGLETON));
                modules.add(new ResourceModule(
                        BOOTSTRAP_RESOURCE_NAME,
                        ReflectionUtil.getCurrentClassLoader(),
                        Scopes.SINGLETON));
                Injector injector = modules.createInjector();
                container = new MicroContainer(injector);
            }
        }
        return container;
    }
}
