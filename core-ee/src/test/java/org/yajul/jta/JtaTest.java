package org.yajul.jta;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.jndi.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.yajul.util.Callable;
import static org.junit.Assert.*;

/**
 * Unit test for the JTA classes.
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 5:50:03 AM
 */
@RunWith(Arquillian.class)
public class JtaTest {

    private static final Logger log = LoggerFactory.getLogger(JtaTest.class);

    @Inject
    private UserTransaction userTransaction;

    @EJB
    private JtaTestBean testBean;

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(JtaTestBean.class)
                .addAsManifestResource(
                        EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml")
                );
    }

    @Test
    public void testTransactionTemplates() throws Exception {
        // UserTransaction is available inside EJBs in all containers.
        // TransactionManager is not always available.
        testBean.transactionTemplateCheck();
    }
}
