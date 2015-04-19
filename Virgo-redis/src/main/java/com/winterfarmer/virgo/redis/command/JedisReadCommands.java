package com.winterfarmer.virgo.redis.command;

import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangtianhang on 15-4-7.
 * copy from JedisCommands, only read commands
 */
public interface JedisReadCommands {
    String get(String key);

    Boolean exists(String key);

    String type(String key);

    Long ttl(String key);

    Boolean getbit(String key, long offset);

    String getrange(String key, long startOffset, long endOffset);

    String substr(String key, int start, int end);

    String hget(String key, String field);

    List<String> hmget(String key, String... fields);

    Boolean hexists(String key, String field);

    Long hlen(String key);

    Set<String> hkeys(String key);

    List<String> hvals(String key);

    Map<String, String> hgetAll(String key);

    Long llen(String key);

    List<String> lrange(String key, long start, long end);

    String lindex(String key, long index);

    Set<String> smembers(String key);

    Long scard(String key);

    Boolean sismember(String key, String member);

    String srandmember(String key);

    List<String> srandmember(String key, int count);

    Long strlen(String key);

    Set<String> zrange(String key, long start, long end);

    Long zrank(String key, String member);

    Long zrevrank(String key, String member);

    Set<String> zrevrange(String key, long start, long end);

    Set<Tuple> zrangeWithScores(String key, long start, long end);

    Set<Tuple> zrevrangeWithScores(String key, long start, long end);

    Long zcard(String key);

    Double zscore(String key, String member);

    List<String> sort(String key);

    List<String> sort(String key, SortingParams sortingParameters);

    Long zcount(String key, double min, double max);

    Long zcount(String key, String min, String max);

    Set<String> zrangeByScore(String key, double min, double max);

    Set<String> zrangeByScore(String key, String min, String max);

    Set<String> zrevrangeByScore(String key, double max, double min);

    Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

    Set<String> zrevrangeByScore(String key, String max, String min);

    Set<String> zrangeByScore(String key, String min, String max, int offset, int count);

    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

    Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max);

    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min);

    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count);

    Long zlexcount(final String key, final String min, final String max);

    Set<String> zrangeByLex(final String key, final String min, final String max);

    Set<String> zrangeByLex(final String key, final String min, final String max, final int offset,
                            final int count);

    String echo(String string);

    Long bitcount(final String key);

    Long bitcount(final String key, long start, long end);

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    ScanResult<Map.Entry<String, String>> hscan(final String key, int cursor);

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    ScanResult<String> sscan(final String key, int cursor);

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    ScanResult<Tuple> zscan(final String key, int cursor);

    ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor);

    ScanResult<String> sscan(final String key, final String cursor);

    ScanResult<Tuple> zscan(final String key, final String cursor);
}
