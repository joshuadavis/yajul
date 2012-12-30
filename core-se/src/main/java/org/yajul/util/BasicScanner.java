package org.yajul.util;

import java.util.List;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A classpath scanner that collects all resource names in the directory or archive where the 'tag resource'
 * is located.
 * <br>
 * User: josh
 * Date: Mar 10, 2008
 * Time: 12:22:34 PM
 */
public class BasicScanner extends AbstractScanner {
    private static Logger log = Logger.getLogger(BasicScanner.class.getName());
    private List<String> items = new LinkedList<String>();

    public BasicScanner(String resourceName) {
        super(resourceName);
    }

    @SuppressWarnings("UnusedDeclaration")
    public BasicScanner(String resourceName, ClassLoader classLoader) {
        super(resourceName, classLoader);
    }

    @Override
    protected void handleItem(String name) {
        if (log.isLoggable(Level.FINER))
            log.log(Level.FINER, "handleItem('" + name + "')");
        items.add(name);
    }

    public List<String> getItems() {
        scan();
        return items;
    }
}
