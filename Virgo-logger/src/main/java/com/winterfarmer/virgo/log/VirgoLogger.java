package com.winterfarmer.virgo.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by yangtianhang on 15-1-6.
 */
public class VirgoLogger {
    private static final Logger debugLog = LogManager.getLogger("debug");
    private static final Logger infoLog = LogManager.getLogger("info");
    private static final Logger warnLog = LogManager.getLogger("warn");
    private static final Logger errorLog = LogManager.getLogger("error");
    private static final Logger fatalLog = LogManager.getLogger("fatal");
    private static final Logger requestLog = LogManager.getLogger("request");
    private static final Logger httpClientLog = LogManager.getLogger("httpClient");

//    static {
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                //shutdown log4j2
//                if (LogManager.getContext() instanceof LoggerContext) {
//                    infoLog.info("Shutting down log4j2");
//                    Configurator.shutdown((LoggerContext) LogManager.getContext());
//                } else {
//                    warnLog.warn("Unable to shutdown log4j2");
//                }
//            }
//        });
//    }

    public static boolean isTraceEnabled() {
        return debugLog.isTraceEnabled();
    }

    public static boolean isDebugEnabled() {
        return debugLog.isDebugEnabled();
    }

    public static void trace(String logFormat, Object... arguments) {
        debugLog.trace(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void debug(String logFormat, Object... arguments) {
        if (debugLog.isDebugEnabled()) {
            debugLog.debug(LogHelper.formatLogString(logFormat), arguments);
        }
    }

    public static void info(String logFormat, Object... arguments) {
        if (infoLog.isInfoEnabled()) {
            infoLog.info(LogHelper.formatLogString(logFormat), arguments);
        }
    }

    public static void warn(String msg, Throwable t) {
        warnLog.warn(LogHelper.formatLogString(msg), t);
    }

    public static void warn(String logFormat, Object... arguments) {
        warnLog.warn(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void error(String msg, Throwable t) {
        errorLog.error(LogHelper.formatLogString(msg), t);
    }

    public static void error(String logFormat, Object... arguments) {
        errorLog.error(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void fatal(String msg, Throwable t) {
        fatalLog.error(LogHelper.formatLogString(msg), t);
    }

    public static void fatal(String logFormat, Object... arguments) {
        fatalLog.error(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void logRequest(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void logResponse(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void httpDebug(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void httpInfo(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void httpWarn(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void httpError(String logFormat, Object... arguments) {
        requestLog.info(LogHelper.formatLogString(logFormat), arguments);
    }

    public static void httpWarn(String msg, Throwable t) {
        fatalLog.error(LogHelper.formatLogString(msg), t);
    }

    public static void httpError(String msg, Throwable t) {
        fatalLog.error(LogHelper.formatLogString(msg), t);
    }
}