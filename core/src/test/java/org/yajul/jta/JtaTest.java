package org.yajul.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.embedded.EmbeddedJBossTestCase;
import org.yajul.jndi.JBossJndiNames;
import org.yajul.jndi.JndiLookup;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.yajul.util.Callable;

/**
 * Unit test for the JTA classes.
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 5:50:03 AM
 */
public class JtaTest extends EmbeddedJBossTestCase {

    private static final Logger log = LoggerFactory.getLogger(JtaTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTransactionTemplates() throws Exception {
        final JndiLookup lookup = getJndiLookup();
        // Use the special JBoss UserTransaction.  Inside EJBS, SessionContext.getUserTransaction() should be used.
        UserTransaction ut = lookup.lookup(UserTransaction.class, JBossJndiNames.LOCAL_USER_TRANSACTION);
        showStatus("before");
        ensureNoTx();
        new UserTransactionTemplate(ut).doAction(new TxAction());
        showStatus("after");
        ensureNoTx();
        JtaHelper.doInTx(getTxm(),new TxAction());
        ensureNoTx();
    }

    private void ensureNoTx() throws SystemException {
        assertEquals(Status.STATUS_NO_TRANSACTION,getTxm().getStatus());
    }

    private void ensureActive() {
        final TransactionManager txm = getTxm();
        assertEquals(Status.STATUS_ACTIVE, TransactionStatusHelper.getStatus(txm));
        assertTrue(TransactionStatusHelper.isInProgress(txm));
    }

    private void showStatus(String msg) {
        TransactionManager txm = getTxm();
        log.info(msg + " : status = " + TransactionStatusHelper.asName(txm));
    }

    private TransactionManager getTxm() {
        final JndiLookup lookup = getJndiLookup();
        return lookup.lookup(TransactionManager.class, JBossJndiNames.TRANSACTION_MANAGER);
    }

    private class TxAction implements Callable<Object> {
        public Object call() throws Exception {
            showStatus("during");
            ensureActive();
            return null;
        }
    }
}