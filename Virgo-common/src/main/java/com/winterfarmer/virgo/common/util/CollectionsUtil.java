package com.winterfarmer.virgo.common.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-17.
 */
public class CollectionsUtil {
    public static <K, V> void putMapList(Map<K, List<V>> map, K key, V val) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<V>());
        }

        List<V> list = map.get(key);
        list.add(val);
    }
}
