package com.winterfarmer.virgo.common.util;

/**
 * Created by yangtianhang on 15-3-4.
 */
public class ConfigUtil {
    public static final String SYS_PROP_UNIT_TEST_KEY = "com.winterfarmer.virgo.common.isunittest";

    public static boolean isUnitTesting() {
        return Boolean.valueOf(System.getProperty(SYS_PROP_UNIT_TEST_KEY));
    }
}
