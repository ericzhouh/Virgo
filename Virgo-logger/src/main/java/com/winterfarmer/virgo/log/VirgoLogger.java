package com.winterfarmer.virgo.log;

import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yangtianhang on 15-1-6.
 */
public class VirgoLogger {
    private static final Logger debugLog = LoggerFactory.getLogger("debug");
    private static final Logger infoLog = LoggerFactory.getLogger("info");
    private static final Logger warnLog = LoggerFactory.getLogger("warn");
    private static final Logger errorLog = LoggerFactory.getLogger("error");
    private static final Logger fatalLog = LoggerFactory.getLogger("fatal");
    private static final Logger requestLog = LoggerFactory.getLogger("request");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LogManager.shutdown();
            }
        });
    }

    public static boolean isTraceEnabled() {
        return debugLog.isTraceEnabled();
    }

    public static boolean isDebugEnabled() {
        return debugLog.isDebugEnabled();
    }

    public static void trace(String format, Object... arguments) {
        debugLog.trace(LogHelper.formatLogString(format), arguments);
    }

    public static void debug(String format, Object... arguments) {
        if (debugLog.isDebugEnabled()) {
            debugLog.debug(LogHelper.formatLogString(format), arguments);
        }
    }

    public static void info(String format, Object... arguments) {
        if (infoLog.isInfoEnabled()) {
            infoLog.info(LogHelper.formatLogString(format), arguments);
        }
    }

    public static void warn(Throwable t, String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        warnLog.warn(String.format(format, arguments), t);
    }

    public static void warn(String format, Object... arguments) {
        warnLog.warn(LogHelper.formatLogString(format), arguments);
    }

    public static void error(Throwable t, String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        errorLog.error(String.format(format, arguments), t);
    }

    public static void error(String format, Object... arguments) {
        errorLog.error(LogHelper.formatLogString(format), arguments);
    }

    public static void fatal(Throwable t, String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        fatalLog.error(String.format(format, arguments), t);
    }

    public static void fatal(String format, Object... arguments) {
        fatalLog.error(LogHelper.formatLogString(format), arguments);
    }

    public static void logRequest(String format, Object... arguments) {
        format = LogHelper.formatLogString(format, LogHelper.LogType.request);
        requestLog.info(format, arguments);
    }

    public static void logResponse(String format, Object... arguments) {
        format = LogHelper.formatLogString(format, LogHelper.LogType.response);
        requestLog.info(format, arguments);
    }
}