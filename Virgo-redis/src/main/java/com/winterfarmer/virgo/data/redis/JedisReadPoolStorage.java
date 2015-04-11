package com.winterfarmer.virgo.data.redis;

import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yangtianhang on 15-4-8.
 */
public class JedisReadPoolStorage implements JedisPoolStorage {
    private final JedisPool[] jedisPools;
    private final int poolCount;
    private final AtomicInteger atomicIndex;
    private final int MAX_INDEX = 1073741824; // 2 ** 30

    public JedisReadPoolStorage(JedisPoolConfig config, List<Pair<String, Integer>> connections, int timeout, String password) {
        this.jedisPools = new JedisPool[connections.size()];
        for (int i = 0; i < connections.size(); ++i) {
            Pair<String, Integer> conn = connections.get(i);
            this.jedisPools[i] = new JedisPool(config, conn.getLeft(), conn.getRight(), timeout, password);
        }

        this.poolCount = jedisPools.length;
        this.atomicIndex = new AtomicInteger();
    }

    @Override
    public JedisPool getJedisPool() {
        int index = atomicIndex.getAndIncrement();

        if (index >= MAX_INDEX) {
            atomicIndex.set(0);
        }

        return jedisPools[index % poolCount];
    }
}
