package com.winterfarmer.virgo.data.redis;

import redis.clients.jedis.JedisPool;

/**
 * Created by yangtianhang on 15-4-8.
 */
public interface JedisPoolStorage {
    JedisPool getJedisPool();
}
