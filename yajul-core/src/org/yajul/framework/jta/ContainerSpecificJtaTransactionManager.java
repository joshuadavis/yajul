// $Id$
package org.yajul.framework.jta;

import org.apache.log4j.Logger;

/**
 * Sub-classes specify the container-specific JNDI names of the TransactionManager and
 * UserTransaction objects.
 * @author josh Apr 2, 2004 8:08:34 AM
 */
public abstract class ContainerSpecificJtaTransactionManager extends JtaTransactionManager
{
    /**
     * A logger for this class.
     */
    private static final Logger log = Logger.getLogger(ContainerSpecificJtaTransactionManager.class);

    public ContainerSpecificJtaTransactionManager(String transactionManagerName, String userTransactionName)
    {
        setTransactionManagerName(transactionManagerName);
        setUserTransactionName(userTransactionName);
        log.info("TransactionManager name: " + transactionManagerName);
        log.info("   UserTransaction name: " + transactionManagerName);
    }

    protected boolean isExistingTransaction(Object transaction)
    {
        boolean existingTransaction = super.isExistingTransaction(transaction);
        if (log.isDebugEnabled())
            log.debug("isExistingTransaction() => " + existingTransaction);
        return existingTransaction;
    }
}
