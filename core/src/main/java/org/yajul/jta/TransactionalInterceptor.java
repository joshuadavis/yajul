package org.yajul.jta;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.transaction.TransactionManager;
import org.yajul.util.Callable;

/**
 * Wraps a transaction around every method call.
 * <br>
 * User: josh
 * Date: Jan 14, 2010
 * Time: 12:23:09 PM
 */
public class TransactionalInterceptor implements MethodInterceptor {
    private TransactionManager transactionManager;

    @Inject
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object invoke(final MethodInvocation invocation) throws Throwable {

        JtaHelper.doInTx(transactionManager,new Callable<Object>() {
            public Object call() throws Throwable {
                    return invocation.proceed();
            }
        });
        return null;
    }
}
