package org.yajul.jndi;

import org.yajul.embedded.EmbeddedJBossTestCase;
import org.yajul.embedded.UnitTestJndiConstants;
import static org.yajul.embedded.UnitTestJndiConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        InitialContext ic = new InitialContext();
        String listing = JndiHelper.listBindings(ic,"java:/");
        log.info(listing);
        DataSource ds = JndiHelper.lookup(ic, DataSource.class, DEFAULT_DATA_SOURCE);
        assertNotNull(ds);
    }

    public void testJndiReference() throws Exception {
        InitialContext ic = new InitialContext();
        JndiReference<TransactionManager> tmr = new JndiReference<TransactionManager>(ic,TransactionManager.class,"java:/TransactionManager");
        TransactionManager tm = tmr.getObject();
        assertNotNull(tm);
    }
}
