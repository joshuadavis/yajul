package org.yajul.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.*;
import java.util.concurrent.Callable;

/**
 * Helper methods for using JTA.
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 7:02:26 AM
 */
public class JtaHelper {
    private static final Logger log = LoggerFactory.getLogger(JtaHelper.class);

    /**
     * A simple transaction that makes TransactionManager (used outside of EJBs) and
     * UserTransaction (often used inside EJBs) look the same.
     * <br>
     * User: Josh
     * Date: Jan 14, 2010
     * Time: 7:22:04 AM
     */
    public static interface SimpleTransaction {
        void begin() throws NotSupportedException, SystemException;

        void commit() throws RollbackException,
                HeuristicMixedException,
                HeuristicRollbackException,
                SecurityException,
                IllegalStateException,
                SystemException;

        void rollback() throws IllegalStateException,
                SecurityException,
                SystemException;

        int getStatus() throws SystemException;
    }

    public static void rollback(UserTransaction ut) {
        rollback(new UserTransactionAdapter(ut));
    }

    public static void rollback(TransactionManager tm) {
        rollback(new TransactionManagerAdapter(tm));
    }

    public static void rollback(SimpleTransaction tx) {
        try {
            tx.rollback();
        }
        catch (javax.transaction.SystemException e1) {
            log.error(e1.getMessage(), e1);
        }
    }

    public static <T> T doInTx(UserTransaction ut, Callable<T> action) {
        return doInTx(new UserTransactionAdapter(ut),action);
    }

    public static <T> T doInTx(SimpleTransaction tx, Callable<T> action) {
        T returnValue;
        try {
            tx.begin();
            assert TransactionStatusHelper.isInProgress(tx.getStatus());
            returnValue = action.call();
            tx.commit();
        }
        catch (Exception e) {
            rollback(tx);
            throw new RuntimeException(e);
        }
        return returnValue;
    }

    public static <T> T doInTx(TransactionManager tm, Callable<T> action) {
        return doInTx(new TransactionManagerAdapter(tm),action);
    }

    /**
     * Adapts UserTransaction to SimpleTransaction to simplify JtaHelper code.
     * <br>
     * User: Josh
     * Date: Jan 14, 2010
     * Time: 7:28:30 AM
     */
    public static class UserTransactionAdapter implements SimpleTransaction {
        private UserTransaction ut;

        public UserTransactionAdapter(UserTransaction ut) {
            this.ut = ut;
        }

        public void begin() throws NotSupportedException, SystemException {
            ut.begin();
        }

        public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
                SecurityException, IllegalStateException, SystemException {
            ut.commit();
        }

        public void rollback() throws IllegalStateException, SecurityException, SystemException {
            ut.rollback();
        }

        public int getStatus() throws SystemException {
            return ut.getStatus();
        }
    }

    /**
     * Adapts TransactionManager to SimpleTransaction to avoid code repetition.
     * <br>
     * User: Josh
     * Date: Jan 14, 2010
     * Time: 7:30:21 AM
     */
    public static class TransactionManagerAdapter implements SimpleTransaction {
        private TransactionManager tm;

        public TransactionManagerAdapter(TransactionManager tm) {
            this.tm = tm;
        }

        public void begin() throws NotSupportedException, SystemException {
            tm.begin();
        }

        public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
                SecurityException, IllegalStateException, SystemException {
            tm.commit();
        }

        public void rollback() throws IllegalStateException, SecurityException, SystemException {
            tm.rollback();
        }

        public int getStatus() throws SystemException {
            return tm.getStatus();
        }
    }
}
