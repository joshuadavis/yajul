// $Id$
package org.yajul.framework.hibernate;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author josh Apr 2, 2004 8:22:38 AM
 */
public class HibernateTemplate extends org.springframework.orm.hibernate.HibernateTemplate
{
    /**
     * A logger for this class. *
     */
    private static final Logger log = Logger.getLogger(HibernateTemplate.class);

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    private Object superExecute(HibernateCallback action) throws DataAccessException
    {
        if (log.isDebugEnabled())
            log.debug("superExecute() : ENTER");
        try
        {
            return super.execute(action);
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug("superExecute() : LEAVE");
        }
    }

    public Object execute(HibernateCallback action) throws DataAccessException
    {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        final HibernateCallback hibernateCallback = action;
        Object result = null;
        try
        {
            log.info("execute() : Invoking HibernateCallback in TX...");
            result = transactionTemplate.execute(
                    new TransactionCallback() {
                        public Object doInTransaction(TransactionStatus status)
                        {
                            if (log.isDebugEnabled())
                                log.debug("doInTransaction() : status = " + status);
                            return superExecute(hibernateCallback);
                        }
                    }
            );
            log.info("execute() : HibernateCallback completed.");
        }
        catch (TransactionException e)
        {
            log.error("execute() : HibernateCallback failed : " + e,e);
            throw e;
        }
        return result;
    }
}
