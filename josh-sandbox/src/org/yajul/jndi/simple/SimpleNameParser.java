/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 12, 2002
 * Time: 5:18:02 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jndi.simple;

import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import java.util.Properties;

class SimpleNameParser implements NameParser
{
    static Properties syntax = new Properties();

    static
    {
        syntax.put("jndi.syntax.direction", "flat");
        syntax.put("jndi.syntax.ignorecase", "false");
    }

    public Name parse(String name) throws NamingException
    {
        return new CompoundName(name, syntax);
    }
}
