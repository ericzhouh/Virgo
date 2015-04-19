package com.winterfarmer.virgo.redis.command;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.ZParams;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-7.
 */
public interface MultiKeyWriteCommands {
    Long del(String... keys);

    List<String> blpop(int timeout, String... keys);

    List<String> brpop(int timeout, String... keys);

    List<String> blpop(String... args);

    List<String> brpop(String... args);

    String mset(String... keysvalues);

    Long msetnx(String... keysvalues);

    String rename(String oldkey, String newkey);

    Long renamenx(String oldkey, String newkey);

    String rpoplpush(String srckey, String dstkey);

    Long sdiffstore(String dstkey, String... keys);

    Long sinterstore(String dstkey, String... keys);

    Long smove(String srckey, String dstkey, String member);

    Long sort(String key, SortingParams sortingParameters, String dstkey);

    Long sort(String key, String dstkey);

    Long sunionstore(String dstkey, String... keys);

    String watch(String... keys);

    String unwatch();

    Long zinterstore(String dstkey, String... sets);

    Long zinterstore(String dstkey, ZParams params, String... sets);

    Long zunionstore(String dstkey, String... sets);

    Long zunionstore(String dstkey, ZParams params, String... sets);

    String brpoplpush(String source, String destination, int timeout);

//    不支持发布订阅模式
//    Long publish(String channel, String message);
//
//    void subscribe(JedisPubSub jedisPubSub, String... channels);
//
//    void psubscribe(JedisPubSub jedisPubSub, String... patterns);

    Long bitop(BitOP op, final String destKey, String... srcKeys);

    String pfmerge(final String destkey, final String... sourcekeys);
}
