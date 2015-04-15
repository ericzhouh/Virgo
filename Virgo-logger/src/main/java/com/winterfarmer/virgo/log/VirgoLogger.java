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
        format = LogHelper.formatLogString(format);
        requestLog.info(format, arguments);
    }

    public static void logResponse(String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        requestLog.info(format, arguments);
    }

    public static void httpDebug(String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.debug(String.format(format, arguments), arguments);
    }

    public static void httpInfo(String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.info(String.format(format, arguments), arguments);
    }

    public static void httpWarn(String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.warn(String.format(format, arguments), arguments);
    }

    public static void httpError(String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.error(String.format(format, arguments), arguments);
    }

    public static void httpWarn(Throwable t, String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.warn(String.format(format, arguments), arguments, t);
    }

    public static void httpError(Throwable t, String format, Object... arguments) {
        format = LogHelper.formatLogString(format);
        httpClientLog.error(String.format(format, arguments), arguments, t);
    }
}