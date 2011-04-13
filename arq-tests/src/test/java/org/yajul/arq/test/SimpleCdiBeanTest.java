package org.yajul.arq.test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * Simple CDI bean test
 * <br>
 * User: Josh
 * Date: 4/13/11
 * Time: 6:05 AM
 */
@RunWith(Arquillian.class)
public class SimpleCdiBeanTest {
    @Inject
    private SimpleCdiBean bean;

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(SimpleCdiBean.class)
                .addAsManifestResource(
                        EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml")
                );
    }

    @Test
    public void testSimpleBean() {
        bean.doSomething();
    }
}
