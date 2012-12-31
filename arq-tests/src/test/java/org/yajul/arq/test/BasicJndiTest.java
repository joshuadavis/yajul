package org.yajul.arq.test;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.*;

/**
 * Test basic JNDI operation for the Java EE container (managed by Arquillian).
 * <br>
 * User: josh
 * Date: 12/31/12
 * Time: 1:36 PM
 */
@RunWith(Arquillian.class)
public class BasicJndiTest {
    @Test
    public void showJNDI() throws Exception {
        String name = "";
        InitialContext context = new InitialContext();
        StringBuilder sb = new StringBuilder();
        sb.append("Listing for ").append(name).append("\n");
        listContext(" ",  context, name, sb);
        System.out.println(sb.toString());
    }

    private void listContext(String prefix, Context context, String name, StringBuilder sb)
            throws NamingException {
        NamingEnumeration<Binding> bindings = context.listBindings(name);
        while (bindings.hasMore()) {
            Binding binding = bindings.next();
            sb.append(prefix).append(name).append(binding.getName()).append(" -> ")
                    .append(binding.getClassName()).append("\n");
            Object obj = binding.getObject();
            if (obj instanceof Context) {
                Context nestedContext = (Context) obj;
                listContext(prefix + binding.getName() + "/",nestedContext,"",sb);
            }
        }
    }
}
