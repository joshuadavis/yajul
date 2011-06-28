package org.yajul.jndi;

import org.yajul.embedded.EmbeddedJBossTestCase;

import static org.yajul.embedded.UnitTestJndiConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

/**
 * JNDI test
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 4:07:28 PM
 */
public class JndiTest extends EmbeddedJBossTestCase {

    private final static Logger log = LoggerFactory.getLogger(JndiTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testJndiHelper() throws Exception {
        InitialContext ic = getInitialContextProvider().getObject();
        String listing = JndiHelper.listBindings(ic,"java:/");
        log.info("*** LISTING OF java:/ *****\n" + listing);
        listing = JndiHelper.listBindings(ic,"/");
        log.info("*** LISTING OF / *****\n" + listing);
        DataSource ds = JndiHelper.lookup(ic, DataSource.class, DEFAULT_DATA_SOURCE);
        assertNotNull(ds);
    }

    public void testJndiReference() throws Exception {
        final JndiLookup lookup = getJndiLookup();
        JndiNames names = new JBossJndiNames();
        JndiObjectProvider<TransactionManager> tmr = new JndiObjectProvider<TransactionManager>(lookup,TransactionManager.class,names.getTransactionManagerName());
        TransactionManager tm = tmr.getObject();
        assertNotNull(tm);
        JndiObjectProvider<ConnectionFactory> cfp = new JndiObjectProvider<ConnectionFactory>(lookup,ConnectionFactory.class,names.getConnectionFactoryName());
        ConnectionFactory cf = cfp.getObject();
        assertNotNull(cf);
    }
}
