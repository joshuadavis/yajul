package org.yajul.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.ReflectionUtil;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.Collections;
import java.util.Map;

/**
 * Useful methods for decoding JTA transaction status.  'Borrowed' from Hibernate's JTAHelper class.
 * <br>
 * User: josh
 * Date: Jan 12, 2010
 * Time: 5:18:49 PM
 */
public class TransactionStatusHelper {

    private static final Logger log = LoggerFactory.getLogger(TransactionStatusHelper.class);

    private static Map<Integer, String> STATUS_TO_NAME_MAP;

    /**
     * @param status JTA transaction status code
     * @return true if the transaction is being rolled back, or is already rolled back
     */
    public static boolean isRollback(int status) {
        switch (status) {
            case Status.STATUS_MARKED_ROLLBACK:
            case Status.STATUS_ROLLING_BACK:
            case Status.STATUS_ROLLEDBACK:
                return true;
            default:
                return false;
        }
    }

    /**
     * @param status JTA transaction status code
     * @return true if the transaction is in progress
     */
    public static boolean isInProgress(int status) {
        return status == Status.STATUS_ACTIVE ||
                status == Status.STATUS_MARKED_ROLLBACK;
    }

    /**
     * @param status JTA transaction status code
     * @return the string name of the status
     */
    public static String asName(int status) {

        synchronized (TransactionStatusHelper.class) {
            if (STATUS_TO_NAME_MAP == null)
                STATUS_TO_NAME_MAP = Collections.unmodifiableMap(ReflectionUtil.getConstantNameMap(Status.class));
        }
        return STATUS_TO_NAME_MAP.get(status);
    }

    /**
     * @param txm the transaction manager
     * @return the string name of the current transaction's status
     */
    public static String asName(TransactionManager txm) {
        return asName(getStatus(txm));
    }

    public static boolean isInProgress(TransactionManager txm) {
        return isInProgress(getStatus(txm));
    }

    public static int getStatus(TransactionManager txm) {
        try {
            return txm.getStatus();
        }
        catch (SystemException e) {
            log.error("Unexpected: " + e, e);
            return Status.STATUS_UNKNOWN;
        }
    }
}
