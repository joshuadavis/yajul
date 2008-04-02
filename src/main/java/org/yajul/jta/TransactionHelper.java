package org.yajul.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.UserTransaction;

/**
 * IoC template for bean managed transactions or for using JTA outside of the EJB container.
 * (IoC = Inversion of Control for those of you who don't know what IoC is)
 * <br>
 * User: josh
 * Date: Nov 30, 2007
 * Time: 4:01:17 PM
 */
public class TransactionHelper {
    private static final Logger log = LoggerFactory.getLogger(TransactionHelper.class);

    public static Object doInTx(UserTransaction ut, Action action) {
        Object returnValue;
        try {
            ut.begin();
            returnValue = action.run();
            ut.commit();
        }
        catch (Exception e) {
            return handleException(ut, e);
        }
        return returnValue;
    }

    public static Object handleException(UserTransaction ut, Exception e) {
        try {
            ut.rollback();
        }
        catch (javax.transaction.SystemException e1) {
            log.error(e1.getMessage(), e1);
        }
        throw new RuntimeException(e);
    }

    public interface Action {
        Object run();
    }
}
