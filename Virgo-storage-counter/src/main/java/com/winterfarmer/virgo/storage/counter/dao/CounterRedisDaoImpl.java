package com.winterfarmer.virgo.storage.counter.dao;

import com.alibaba.druid.util.StringUtils;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.redis.RedisBiz;
import com.winterfarmer.virgo.redis.Vedis;
import com.winterfarmer.virgo.redis.VedisFactory;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class CounterRedisDaoImpl implements CounterDao {
    private final String keyPrefix;
    private final Vedis vedis;

    public CounterRedisDaoImpl(RedisBiz redisBiz, Vedis vedis) {
        this.keyPrefix = redisBiz.name().toLowerCase() + ".";
        this.vedis = VedisFactory.getVedis(redisBiz);
    }

    @Override
    public Integer getCount(long id, int type) {
        String key = getKey(id, type);
        String countStr = vedis.get(key);
        if (countStr == null) {
            return 0;
        }

        try {
            return Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            VirgoLogger.error("Counter Invalid value: {}, key: {}", countStr, key);
            return null;
        }
    }

    @Override
    public boolean setCount(long id, int type, int count) {
        String key = getKey(id, type);
        String result = vedis.set(key, Integer.toString(count));
        return StringUtils.equals(result, "OK");
    }

    private String getKey(long id, int type) {
        return this.keyPrefix + type + "." + id;
    }
}
