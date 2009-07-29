package org.yajul.fix.dictionary;

/**
 * TODO: Add class level comments.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 10:42:57 AM
 */
public class ConfigError extends RuntimeException {
    public ConfigError() {
    }

    public ConfigError(String message) {
        super(message);
    }

    public ConfigError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigError(Throwable cause) {
        super(cause);
    }
}
