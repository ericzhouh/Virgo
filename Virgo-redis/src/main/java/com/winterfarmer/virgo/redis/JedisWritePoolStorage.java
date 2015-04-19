package com.winterfarmer.virgo.redis;

import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by yangtianhang on 15-4-8.
 */
public class JedisWritePoolStorage implements JedisPoolStorage {
    private final JedisPool jedisPool;

    public JedisWritePoolStorage(JedisPoolConfig config, Pair<String, Integer> connection, int timeout, String password) {
        jedisPool = new JedisPool(config, connection.getLeft(), connection.getRight(), timeout, password);
    }

    @Override
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
