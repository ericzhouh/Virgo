package com.winterfarmer.virgo.common.util;

import com.google.common.base.MoreObjects;

/**
 * Created by yangtianhang on 15-2-26.
 */
public class EnumUtil {
    public static String toString(Class<?> clazz, String name, int index) {
        return MoreObjects.toStringHelper(clazz).
                add("name", name).
                add("index", index).
                toString();
    }
}
