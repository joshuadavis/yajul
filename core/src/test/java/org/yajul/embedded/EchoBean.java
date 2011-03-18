package org.yajul.embedded;

import javax.ejb.Stateless;

/**
 * Simple echo bean.
 * <br>
 * User: josh
 * Date: Apr 2, 2009
 * Time: 6:42:16 PM
 */
@Stateless
public class EchoBean implements Echo {
    public String echo(String msg) {
        return "msg=" + msg;    
    }
}
