package dev.franek.customitems.shared.logger;

import java.util.logging.Logger;

public class FastLogger {
    private static Logger assignedLogger = null;

    public static void assignLogger(Logger logger) {
        assignedLogger = logger;
    }

    public static void info(String content) {
        if (assignedLogger == null) {
            assignedLogger = Logger.getLogger("FastLogger");
        }
        assignedLogger.info(content);
    }

    public static void warning(String content) {
        if (assignedLogger == null) {
            assignedLogger = Logger.getLogger("FastLogger");
        }
        assignedLogger.warning(content);
    }

    public static void error(String content) {
        if (assignedLogger == null) {
            assignedLogger = Logger.getLogger("FastLogger");
        }
        assignedLogger.severe(content);
    }
}

