package org.yajul.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.LinkedList;

/**
 * A classpath scanner that collects all resource names in the directory or archive where the 'tag resource'
 * is located.
 * <br>
 * User: josh
 * Date: Mar 10, 2008
 * Time: 12:22:34 PM
 */
public class BasicScanner extends AbstractScanner {
    private static Logger log = LoggerFactory.getLogger(BasicScanner.class);
    private List<String> items = new LinkedList<String>();

    public BasicScanner(String resourceName) {
        super(resourceName);
    }

    public BasicScanner(String resourceName, ClassLoader classLoader) {
        super(resourceName, classLoader);
    }

    @Override
    protected void handleItem(String name) {
        if (log.isTraceEnabled())
            log.trace("handleItem('" + name + "')");
        items.add(name);
    }

    public List<String> getItems() {
        scan();
        return items;
    }
}
