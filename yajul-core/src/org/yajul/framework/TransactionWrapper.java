package org.yajul.framework;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.apache.log4j.Logger;

/**
 * A template bean that can be used to associate business logic with a transaction.
 * Depending on the type of Spring PlatformTransactionManager used, this can be used
 * in EJBs or in Spring's transaction framework.
 * <br>
 * TODO: Provide a useage example.
 * <br>
 * User: jdavis
 * Date: Mar 11, 2004
 * Time: 5:19:17 PM
 * @author jdavis
 */
public class TransactionWrapper
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(TransactionWrapper.class.getName());

    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    public TransactionWrapper()
    {
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        if (log.isDebugEnabled())
            log.debug("setTransactionManager()");
        this.transactionManager = transactionManager;
        TransactionDefinition td = new DefaultTransactionDefinition();
        transactionStatus = transactionManager.getTransaction(td);
    }

    public void commit()
    {
        if (log.isDebugEnabled())
            log.debug("commit()");
        transactionManager.commit(transactionStatus);
    }

    public void rollback()
    {
        if (log.isDebugEnabled())
            log.debug("rollback()");
        transactionManager.rollback(transactionStatus);
    }

}
