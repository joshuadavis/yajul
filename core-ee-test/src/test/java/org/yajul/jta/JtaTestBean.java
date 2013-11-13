package org.yajul.jta;


import org.yajul.jndi.JndiHelper;
import org.yajul.util.Callable;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.logging.Logger;

/**
 * Test bean for transactions.
 * <br>
 * User: josh
 * Date: 12/31/12
 * Time: 6:36 PM
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class JtaTestBean {
    private static final Logger log = Logger.getLogger(JtaTestBean.class.getName());

    @Resource
    private EJBContext context;

    public void transactionTemplateCheck() throws SystemException {
        log.info("bindings=" + JndiHelper.listBindings(JndiHelper.getDefaultInitialContext(), ""));
        if (context == null)
            throw new IllegalStateException("EJB context is null!");
        final UserTransaction utx = context.getUserTransaction();
        if (utx == null)
            throw new IllegalStateException("UserTransaction is null!");

        if (TransactionStatusHelper.isInProgress(utx.getStatus()))
            throw new IllegalStateException("UserTransaction is in progress! (1)");

        UserTransactionTemplate template = new UserTransactionTemplate(utx);
        template.doAction(new Callable<Object>() {
            public Object call() throws Throwable {
                if (!TransactionStatusHelper.isInProgress(utx.getStatus()))
                    throw new IllegalStateException("UserTransaction is NOT in progress!");
                return null;
            }
        });

        if (TransactionStatusHelper.isInProgress(utx.getStatus()))
            throw new IllegalStateException("UserTransaction is in progress! (2)");
    }
}
