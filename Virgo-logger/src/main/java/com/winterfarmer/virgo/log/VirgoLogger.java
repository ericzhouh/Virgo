package com.winterfarmer.virgo.log;

//import org.apache.log4j.LogManager;
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
    private static final Logger httpClientLog = LoggerFactory.getLogger("httpClient");

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