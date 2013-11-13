package org.yajul.jndi;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yajul.util.ObjectProvider;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.logging.Logger;
import static org.junit.Assert.*;
/**
 * JNDI test
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 4:07:28 PM
 */
@RunWith(Arquillian.class)
public class JndiTest {

    private final static Logger log = Logger.getLogger(JndiTest.class.getName());

    @Test
    public void testJndiHelper() throws Exception {
        ObjectProvider<InitialContext> provider = new DefaultInitialContextProvider();
        InitialContext ic = provider.getObject();
        String listing = JndiHelper.listBindings(ic,"");
        log.info("*** LISTING OF / *****\n" + listing);
        DataSource ds = JndiHelper.lookup(ic, DataSource.class, UnitTestJndiConstants.DEFAULT_DATA_SOURCE);
        assertNotNull(ds);
    }

/*
    public void testJndiReference() throws Exception {
        final JndiLookup lookup = new DefaultJndiLookup();
        JndiNames names = new JBossJndiNames();
        JndiObjectProvider<TransactionManager> tmr = new JndiObjectProvider<TransactionManager>(lookup,TransactionManager.class,names.getTransactionManagerName());
        TransactionManager tm = tmr.getObject();
        TestCase.assertNotNull(tm);
        JndiObjectProvider<ConnectionFactory> cfp = new JndiObjectProvider<ConnectionFactory>(lookup,ConnectionFactory.class,names.getConnectionFactoryName());
        ConnectionFactory cf = cfp.getObject();
        TestCase.assertNotNull(cf);
    }
*/
}
