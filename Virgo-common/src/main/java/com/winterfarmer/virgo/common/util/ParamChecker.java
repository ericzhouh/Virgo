package com.winterfarmer.virgo.common.util;

/**
 * Created by yangtianhang on 15-3-7.
 */
public class ParamChecker {
    public static <T> T notNull(final T object, final String paramName) {
        if (object == null) {
            throw new NullPointerException(parameter(paramName) + " should not be null");
        }
        return object;
    }

    public static int ge(final int param, final String paramName, final int right) {
        if (param >= right) {
            return param;
        } else {
            throw new IllegalArgumentException(parameter(paramName, param) + " should be greater than or equal to " + right);
        }
    }

    private static String parameter(String paramName) {
        return "Param[" + paramName + "]";
    }

    private static <T> String parameter(String paramName, T value) {
        return "Param[" + paramName + ":" + value + "]";
    }
}
