package org.yajul.jta;


import org.yajul.jndi.JndiHelper;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;

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

    public void check() {
        System.out.println("bindings=" + JndiHelper.listBindings(JndiHelper.getDefaultInitialContext(), ""));
    }
}
