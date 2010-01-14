package org.yajul.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.UserTransaction;
import java.util.concurrent.Callable;

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
