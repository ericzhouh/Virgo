package com.winterfarmer.virgo.redis.command;

import redis.clients.jedis.Client;

import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-7.
 * copy from JedisCommands, only write commands
 */
public interface JedisWriteCommands {
    String set(String key, String value);

    String set(String key, String value, String nxxx, String expx, long time);

    Long persist(String key);

    Long expire(String key, int seconds);

    Long expireAt(String key, long unixTime);

    Boolean setbit(String key, long offset, boolean value);

    Boolean setbit(String key, long offset, String value);

    Long setrange(String key, long offset, String value);

    String getSet(String key, String value);

    Long setnx(String key, String value);

    String setex(String key, int seconds, String value);

    Long decrBy(String key, long integer);

    Long decr(String key);

    Long incrBy(String key, long integer);

    Long incr(String key);

    Long append(String key, String value);

    Long hset(String key, String field, String value);

    String hmset(String key, Map<String, String> hash);

    Long hincrBy(String key, String field, long value);

    Long hdel(String key, String... field);

    Long rpush(String key, String... string);

    Long lpush(String key, String... string);

    String ltrim(String key, long start, long end);

    String lset(String key, long index, String value);

    Long lrem(String key, long count, String value);

    String lpop(String key);

    String rpop(String key);

    Long sadd(String key, String... member);

    Long srem(String key, String... member);

    String spop(String key);

    Long zadd(String key, double score, String member);

    Long zadd(String key, Map<String, Double> scoreMembers);

    Long zrem(String key, String... member);

    Double zincrby(String key, double score, String member);

    Long zremrangeByRank(String key, long start, long end);

    Long zremrangeByScore(String key, double start, double end);

    Long zremrangeByScore(String key, String start, String end);

    Long zremrangeByLex(final String key, final String min, final String max);

    Long linsert(String key, Client.LIST_POSITION where, String pivot, String value);

    Long lpushx(String key, String... string);

    Long rpushx(String key, String... string);

    /**
     * @deprecated unusable command, this will be removed in 3.0.0.
     */
    @Deprecated
    List<String> blpop(String arg);

    List<String> blpop(int timeout, String key);

    /**
     * @deprecated unusable command, this will be removed in 3.0.0.
     */
    @Deprecated
    List<String> brpop(String arg);

    List<String> brpop(int timeout, String key);

    Long del(String key);

    Long move(String key, int dbIndex);

    Long pfadd(final String key, final String... elements);
}
