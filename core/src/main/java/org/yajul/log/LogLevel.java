package org.yajul.log;

import java.util.logging.Level;

/**
 * Logging level enum, used for switch statements.
 * <br>User: Josh
 * Date: Dec 11, 2008
 * Time: 6:50:53 AM
 */
public enum LogLevel {
    TRACE(Level.FINER.intValue()),
    DEBUG(Level.FINE.intValue()),
    INFO(Level.INFO.intValue()),
    WARN(Level.WARNING.intValue()),
    ERROR(Level.SEVERE.intValue()),
    OFF(Level.OFF.intValue()),
    ;
    private int juliPriority;

    LogLevel(int juliPriority) {
        this.juliPriority = juliPriority;
    }

    public static LogLevel toLogLevel(Level level) {
        int juliPriority = level.intValue();
        LogLevel[] levels = LogLevel.values();
        for (LogLevel logLevel : levels) {
            if (logLevel.juliPriority >= juliPriority)
                return logLevel;
        }
        return OFF;
    }
}
