package com.winterfarmer.virgo.log;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yangtianhang on 15-1-7.
 */
public class LogHelper {
    enum LogType {
        request,
        response,
        business
    }

    private static final String LOGGER_NAME = "TraceableLogger";

    private static final String EVENT_ID = "event_id";
    private static final String IS_REST = "is_rest";
    private static final String TRACE_ID = "trace_id";
    private static final String START_TIME = "start_time";
    private static final String URL = "url";
    private static final String HTTP_STATUS = "http_status";

    private static AtomicLong lastTimer = new AtomicLong(0L);
    private static AtomicInteger traceIdSeed = new AtomicInteger(0);

    private static Integer ipInt = null;
    private static String ipStr = null;
    private static String instanceName = null;

    public static String formatLogString(String format) {
        return formatLogString(format, LogType.business);
    }

    public static String formatLogString(String format, LogType type) {
        boolean isRestLog = isRestLog();
        if (!isRestLog) {
            startTraceMDC();
        }

        increaseEventId();

        StackTraceElement logPoint = getLogPoint();
        String[] classNames = logPoint.getClassName().split("\\.");
        String className = classNames[classNames.length - 1];
        String methodName = logPoint.getMethodName();
        int lineNumber = logPoint.getLineNumber();

        if (!isRestLog) {
            endTraceMDC();
        }

        return String.format("[%s %s %s %s:%d %s] - [%s]", org.apache.log4j.MDC.get(TRACE_ID), type, org.apache.log4j.MDC.get(EVENT_ID), className, lineNumber, methodName, format);
    }

    public static void startRestLog() {
        org.apache.log4j.MDC.put(IS_REST, Boolean.TRUE);
        startTraceMDC();
    }

    public static void endRestLog() {
        endTraceMDC();
    }

    public static void setUrl(String url) {
        org.apache.log4j.MDC.put(URL, url);
    }

    public static String getUrl() {
        return (String) org.apache.log4j.MDC.get(URL);
    }

    public static void setHttpStatus(int status) {
        org.apache.log4j.MDC.put(HTTP_STATUS, status);
    }

    public static int getHttpStatus() {
        return (int) org.apache.log4j.MDC.get(HTTP_STATUS);
    }

    public static long getStartTime() {
        return (long) org.apache.log4j.MDC.get(START_TIME);
    }

    private static String startTraceMDC() {
        String traceId = newTraceId();
        org.apache.log4j.MDC.put(TRACE_ID, traceId);
        org.apache.log4j.MDC.put(EVENT_ID, new AtomicInteger(0));
        org.apache.log4j.MDC.put(START_TIME, System.currentTimeMillis());

        return traceId;
    }

    private static void endTraceMDC() {
        org.apache.log4j.MDC.clear();
    }

    private static boolean isRestLog() {
        return org.apache.log4j.MDC.get(IS_REST) != null;
    }

    private static void increaseEventId() {
        ((AtomicInteger) (org.apache.log4j.MDC.get(EVENT_ID))).incrementAndGet();
    }

    private static String newTraceId() {
        long current = System.currentTimeMillis();
        int seq = 0;

        long lastTime = lastTimer.get();
        if (lastTime == current) {
            seq = traceIdSeed.incrementAndGet();
            if (seq > 9999) {
                traceIdSeed.set(0);
                seq = traceIdSeed.incrementAndGet();
            }
        }

        lastTimer.compareAndSet(lastTime, current);
        return "T" + getInstanceName() + '_' + current + String.format("%04d", seq);
    }

    private static String getInstanceName() {
        if (instanceName != null) {
            return instanceName;
        }

        try {
            InetAddress inetAdress = InetAddress.getLocalHost();
            byte lastField = inetAdress.getAddress()[3];
            instanceName = Byte.toString(lastField);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return instanceName;
    }

    private static Integer getIpInt() {
        if (ipInt != null) {
            return ipInt;
        }

        try {
            InetAddress inetAdress = InetAddress.getLocalHost();
            int result = 0;
            for (byte b : inetAdress.getAddress()) {
                result = result << 8 | (b & 0xFF);
            }
            ipInt = result;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipInt;
    }

    private static String getIpStr() {
        if (StringUtils.isNotBlank(ipStr)) {
            return ipStr;
        }

        try {
            InetAddress inetAdress = InetAddress.getLocalHost();
            ipStr = inetAdress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipStr;
    }

    private static StackTraceElement getLogPoint() {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        StackTraceElement s = null;

        for (int i = 0; i < stack.length; i++) {
            s = stack[i];
            if (s.getClassName().indexOf(LOGGER_NAME) != -1) {
                s = stack[i + 1];
                break;
            }
        }

        return s;
    }
}
