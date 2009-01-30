package org.yajul.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Suppresses Log4J categories temporarily.
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 3:22:23 PM
 */
public class LogSuppressor {
    private Map<String, Level> oldLevels = new HashMap<String, Level>();

    public LogSuppressor(String... categories) {
        for (String category : categories) {
            Logger logger = Logger.getLogger(category);
            oldLevels.put(category, logger.getLevel());
            logger.setLevel(Level.FATAL);
        }
    }

    public void restore() {
        for (Map.Entry<String, Level> entry : oldLevels.entrySet()) {
            String category = entry.getKey();
            Logger logger = Logger.getLogger(category);
            logger.setLevel(entry.getValue());
        }
    }
}
