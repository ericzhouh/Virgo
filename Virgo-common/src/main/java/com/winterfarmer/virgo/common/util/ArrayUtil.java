package com.winterfarmer.virgo.common.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

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

    public static long[] deduplicate(long[] numbers) {
        if (numbers == null) {
            return null;
        }

        Set<Long> set = Sets.newTreeSet();
        for (long number : numbers) {
            set.add(number);
        }
        if (set.size() == numbers.length) {
            return numbers;
        }

        long[] deduplicated = new long[set.size()];
        int i = 0;
        for (Long number : set) {
            deduplicated[i++] = number;
        }

        return deduplicated;
    }

    public static long[] toLongArray(Collection<Long> collection) {
        long[] array = new long[collection.size()];
        int i = 0;
        for (long item : collection) {
            array[i++] = item;
        }
        return array;
    }
}
