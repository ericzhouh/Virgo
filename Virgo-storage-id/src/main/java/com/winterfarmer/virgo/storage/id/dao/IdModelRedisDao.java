package com.winterfarmer.virgo.storage.id.dao;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.redis.RedisBiz;
import com.winterfarmer.virgo.redis.Vedis;
import com.winterfarmer.virgo.redis.VedisFactory;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public class IdModelRedisDao<T extends BaseIdModel> implements IdModelDao<T> {
    private static final String OK = "OK";

    private final String keyPrefix;
    private final Vedis vedis;
    private final Class<T> clazz;
    private final Function<String, T> transFunc;

    public IdModelRedisDao(RedisBiz redisBiz, final Class<T> clazz) {
        this.vedis = VedisFactory.getVedis(redisBiz);
        this.clazz = clazz;
        this.keyPrefix = redisBiz.name().toLowerCase() + ".id.";
        this.transFunc = new Function<String, T>() {
            @Override
            public T apply(String s) {
                return s == null ? null : (T) JSON.parseObject(s, IdModelRedisDao.this.clazz);
            }
        };
    }

    public String getKey(long id) {
        return this.keyPrefix + id;
    }

    public Vedis getVedis() {
        return vedis;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public T get(long id) {
        String value = vedis.get(getKey(id));
        return value == null ? null : transFunc.apply(value);
    }

    @Override
    public List<T> list(long... ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }

        String[] keys = new String[ids.length];
        int i = 0;
        for (long id : ids) {
            keys[i++] = getKey(id);
        }

        List<String> values = vedis.mget(keys);
        return Lists.transform(values, transFunc);
    }

    @Override
    public T insert(T object) {
        if (object == null) {
            return null;
        }

        String value = JSON.toJSONString(object);
        return StringUtils.equals(vedis.set(getKey(object.getId()), value), OK) ?
                object : null;
    }

    @Override
    public T update(T object) {
        return insert(object);
    }
}
