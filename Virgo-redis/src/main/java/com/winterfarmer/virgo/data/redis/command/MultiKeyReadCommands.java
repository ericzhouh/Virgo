package com.winterfarmer.virgo.data.redis.command;

import redis.clients.jedis.*;

import java.util.List;
import java.util.Set;

/**
 * Created by yangtianhang on 15-4-7.
 */
public interface MultiKeyReadCommands {
    Set<String> keys(String pattern);

    List<String> mget(String... keys);

    Set<String> sdiff(String... keys);

    Set<String> sinter(String... keys);

    Set<String> sunion(String... keys);

    String brpoplpush(String source, String destination, int timeout);

//    不支持发布订阅模式
//    Long publish(String channel, String message);
//
//    void subscribe(JedisPubSub jedisPubSub, String... channels);
//
//    void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    String randomKey();

    @Deprecated
    /**
     * This method is deprecated due to bug (scan cursor should be unsigned long)
     * And will be removed on next major release
     * @see https://github.com/xetorthio/jedis/issues/531
     */
    ScanResult<String> scan(int cursor);

    ScanResult<String> scan(final String cursor);

    String pfmerge(final String destkey, final String... sourcekeys);

    long pfcount(final String... keys);
}
