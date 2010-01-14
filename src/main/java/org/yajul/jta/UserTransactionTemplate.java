package org.yajul.jta;

import org.yajul.util.Callable;

import javax.transaction.UserTransaction;

/**
 * IoC template for bean managed transactions or for using JTA outside of the EJB container.
 * (IoC = Inversion of Control for those of you who don't know what IoC is)
 * <br>
 * User: josh
 * Date: Nov 30, 2007
 * Time: 4:01:17 PM
 */
public class UserTransactionTemplate {

    private UserTransaction ut;

    public UserTransactionTemplate(UserTransaction ut) {
        this.ut = ut;
    }

    public <T> T doAction(Callable<T> action) {
        return JtaHelper.doInTx(ut,action);
    }
}
