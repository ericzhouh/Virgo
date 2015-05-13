package com.winterfarmer.virgo.base.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.model.BaseModel;
import com.winterfarmer.virgo.redis.Vedis;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


/**
 * Created by yangtianhang on 15/5/13.
 */
public class RedisUtil {
    public static <T extends BaseModel> List<T> mget(Vedis vedis, Class<T> clazz, String... keys) {
        List<String> values = vedis.mget(keys);
        if (CollectionUtils.isEmpty(values)) {
            return Lists.newArrayList();
        } else {
            List<T> result = Lists.newArrayListWithCapacity(keys.length);
            for (String val : values) {
                result.add(JSON.parseObject(val, clazz));
            }

            return result;
        }
    }

    public static <T extends BaseModel> String set(Vedis vedis, String key, T obj) {
        return vedis.set(key, obj.toJSONString());
    }

    public static <T extends BaseModel> T get(Vedis vedis, Class<T> clazz, String key) {
        String value = vedis.get(key);
        return value == null ? null : JSON.parseObject(value, clazz);
    }
}
