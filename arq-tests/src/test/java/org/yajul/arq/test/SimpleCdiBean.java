package org.yajul.arq.test;

import javax.naming.*;

/**
 * A very simple CDI bean for testing.
 * <br>
 * User: Josh
 * Date: 4/13/11
 * Time: 6:07 AM
 */
public class SimpleCdiBean {
    public void doSomething() {
        System.out.println("hello there");
        StringBuilder sb = null;
        try {
            InitialContext context = new InitialContext();
            String name = "";
            sb = new StringBuilder();
            sb.append("Listing for ").append(name).append("\n");
            listContext(" ",  context, name, sb);
        } catch (NamingException e) {
            e.printStackTrace();
        }
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
