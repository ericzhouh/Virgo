package com.winterfarmer.virgo.common.util;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Created by yangtianhang on 15-2-26.
 */
public class ArrayUtil {
    @Nullable
    public static <T> T find(T[] array, Predicate<? super T> predicate, @Nullable T defaultValue) {
        if (array == null || array.length == 0) {
            return null;
        }

        for (int i = 0; i < array.length; ++i) {
            if (predicate.apply(array[i])) {
                return array[i];
            }
        }

        return null;
    }
}
