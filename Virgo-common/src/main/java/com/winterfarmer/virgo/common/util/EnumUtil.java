package com.winterfarmer.virgo.common.util;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by yangtianhang on 15-2-26.
 */
public class EnumUtil {
    public interface VirgoEnum {
        String getName();

        int getIndex();
    }

    public static <T extends VirgoEnum> Map<Integer, T> getEnumMap(Class<T> clazz) {
        if (!clazz.isEnum()) {
            throw new RuntimeException("Invalid clazz, clazz must be a enum!");
        }

        T[] enums = clazz.getEnumConstants();
        Map<Integer, T> map = Maps.newHashMap();
        for (T enun : enums) {
            map.put(enun.getIndex(), enun);
        }

        return map;
    }

    public static String toString(Class<?> clazz, String name, int index) {
        return MoreObjects.toStringHelper(clazz).
                add("name", name).
                add("index", index).
                toString();
    }

    public static <T extends VirgoEnum> String toString(T enun) {
        return MoreObjects.toStringHelper(enun.getClass()).
                add("name", enun.getName()).
                add("index", enun.getIndex()).
                toString();
    }
}
