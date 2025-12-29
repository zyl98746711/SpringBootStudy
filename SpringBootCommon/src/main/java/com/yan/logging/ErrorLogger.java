package com.yan.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error日志
 */
public class ErrorLogger {

    private static final String NAME = "errorLogger";

    private static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void log(String msg) {
        LOGGER.error(msg);
    }

    public static void log(String format, Object obj) {
        LOGGER.error(format, obj);
    }
}
