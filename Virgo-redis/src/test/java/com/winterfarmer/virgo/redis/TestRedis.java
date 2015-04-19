package com.winterfarmer.virgo.redis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.common.util.MathUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.Tuple;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangtianhang on 15-4-9.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/virgo-test-redis-context.xml"})
public class TestRedis {
    Vedis vedis;

    @Before
    public void setUp() {
        vedis = VedisFactory.getVedis(RedisBiz.Test);
    }

    @After
    public void tearDown() {
    }

    private static final String OK = "OK";

    private static String randomValue() {
        return "val." + RandomStringUtils.randomAlphabetic(5);
    }

    private static String randomKey() {
        return "key." + RandomStringUtils.randomAlphabetic(5);
    }


    static class ThisJson implements Serializable {
        private static final long serialVersionUID = -4246880037612823262L;
        private String name;
        private int num;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    // test redis.clients.jedis.JedisCommands: get(class java.lang.String)
    @Test
    public void test_get() {
        String value = randomValue();
        String key = randomKey();
        Assert.assertEquals(OK, vedis.set(key, value));
        Assert.assertEquals(value, vedis.get(key));
    }

    // test redis.clients.jedis.JedisCommands: type(class java.lang.String)
    @Test
    public void test_type() {
        vedis.set("key1", "value1");
        vedis.lpush("key2", "value1");
        vedis.sadd("key3", "value1");

        Assert.assertEquals("string", vedis.type("key1"));
        Assert.assertEquals("list", vedis.type("key2"));
        Assert.assertEquals("set", vedis.type("key3"));
    }

    // test redis.clients.jedis.JedisCommands: append(class java.lang.String, class java.lang.String)
    @Test
    public void test_append() {
        vedis.del("test_append");
        Assert.assertFalse(vedis.exists("test_append"));
        Assert.assertEquals(5L, (long) vedis.append("test_append", "Hello"));
        Assert.assertEquals(11L, (long) vedis.append("test_append", " World"));
        Assert.assertEquals("Hello World", vedis.get("test_append"));
    }

    // test redis.clients.jedis.JedisCommands: set(class java.lang.String, class java.lang.String)
    @Test
    public void test_set() {
        String value = randomValue();
        String key = randomKey();
        Assert.assertEquals(OK, vedis.set(key, value));
        Assert.assertEquals(value, vedis.get(key));
    }

    // test redis.clients.jedis.JedisCommands: set(class java.lang.String, class java.lang.String, class java.lang.String, class java.lang.String, long)
    @Test
    public void test_set1() {
        Assert.fail();
    }

    // test redis.clients.jedis.JedisCommands: exists(class java.lang.String)
    @Test
    public void test_exists() {
        vedis.del("test_exists");
        Assert.assertFalse(vedis.exists("test_exists"));
        vedis.set("test_exists", randomValue());
        Assert.assertTrue(vedis.exists("test_exists"));
    }

    // test redis.clients.jedis.JedisCommands: sort(class java.lang.String)
    @Test
    public void test_sort() {
        vedis.del("test_sort");
        vedis.lpush("test_sort", "531", "312", "1", "123");
        List<String> list = vedis.sort("test_sort");
        Assert.assertEquals(4, list.size());
        Assert.assertEquals("1", list.get(0));
        Assert.assertEquals("123", list.get(1));
        Assert.assertEquals("312", list.get(2));
        Assert.assertEquals("531", list.get(3));
    }

    // test redis.clients.jedis.JedisCommands: sort(class java.lang.String, class redis.clients.jedis.SortingParams)
    @Test
    public void test_sort1() {
        Assert.fail();
    }

    // test redis.clients.jedis.JedisCommands: setbit(class java.lang.String, long, class java.lang.String)
    @Test
    public void test_setbit() {
        vedis.del("test_setbit");
        Assert.assertFalse(vedis.setbit("test_setbit", 7, true));
        Assert.assertTrue(vedis.setbit("test_setbit", 7, false));
    }

    // test redis.clients.jedis.JedisCommands: setbit(class java.lang.String, long, boolean)
    @Test
    public void test_setbit1() {
        Assert.fail();
    }

    // test redis.clients.jedis.JedisCommands: setrange(class java.lang.String, long, class java.lang.String)
    @Test
    public void test_setrange() {
        Assert.assertEquals(OK, vedis.set("test_setrange", "Hello World"));
        Assert.assertEquals(11L, (long) vedis.setrange("test_setrange", 6, "Redis"));
        Assert.assertEquals("Hello Redis", vedis.get("test_setrange"));
    }

    // test redis.clients.jedis.JedisCommands: substr(class java.lang.String, int, int)
    @Test
    public void test_substr() {
        vedis.set("test_substr", "test_substr");
        Assert.assertEquals("est_s", vedis.substr("test_substr", 1, 5));
    }

    // test redis.clients.jedis.JedisCommands: persist(class java.lang.String)
    @Test
    public void test_persist() {
        String key = getLocalKey();
        vedis.set(key, "Hello");
        Assert.assertEquals(1L, (long) vedis.expire(key, 10));
        Assert.assertEquals(10L, (long) vedis.ttl(key));
        Assert.assertEquals(1L, (long) vedis.persist(key));
        Assert.assertEquals(-1L, (long) vedis.ttl(key));
    }

    // test redis.clients.jedis.JedisCommands: getSet(class java.lang.String, class java.lang.String)
    @Test
    public void test_getSet() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.incr(key));
        Assert.assertEquals("1", vedis.getSet(key, "0"));
        Assert.assertEquals("1", vedis.get(key));
    }

    // test redis.clients.jedis.JedisCommands: setex(class java.lang.String, int, class java.lang.String)
    @Test
    public void test_setex() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.setex(key, 10, "Hello"));
        Assert.assertEquals(10L, (long) vedis.ttl(key));
        Assert.assertEquals("Hello", vedis.get(key));
    }

    // test redis.clients.jedis.JedisCommands: decrBy(class java.lang.String, long)
    @Test
    public void test_decrBy() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.set(key, "10"));
        Assert.assertEquals(7L, (long) vedis.decrBy(key, 3));
    }

    // test redis.clients.jedis.JedisCommands: decr(class java.lang.String)
    @Test
    public void test_decr() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.set(key, "10"));
        Assert.assertEquals(9L, (long) vedis.decr(key));
    }

    // test redis.clients.jedis.JedisCommands: incrBy(class java.lang.String, long)
    @Test
    public void test_incrBy() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.set(key, "10"));
        Assert.assertEquals(13L, (long) vedis.incrBy(key, 3));
    }

    // test redis.clients.jedis.JedisCommands: incr(class java.lang.String)
    @Test
    public void test_incr() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.set(key, "10"));
        Assert.assertEquals(11L, (long) vedis.incr(key));
    }

    // test redis.clients.jedis.JedisCommands: hset(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_hset() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.hset(key, "field1", "Hello"));
        Assert.assertEquals("Hello", vedis.hget(key, "field1"));
    }

    // test redis.clients.jedis.JedisCommands: hmset(class java.lang.String, interface java.util.Map)
    @Test
    public void test_hmset() {
        String key = getLocalKey();
        Assert.assertEquals(OK, vedis.hmset(key, ImmutableMap.of("field1", "Hello", "field2", "World")));
        Assert.assertEquals("Hello", vedis.hget(key, "field1"));
        Assert.assertEquals("World", vedis.hget(key, "field2"));
    }

    // test redis.clients.jedis.JedisCommands: hincrBy(class java.lang.String, class java.lang.String, long)
    @Test
    public void test_hincrBy() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.hset(key, "field1", "5"));
        Assert.assertEquals(6L, (long) vedis.hincrBy(key, "field1", 1));
        Assert.assertEquals(5L, (long) vedis.hincrBy(key, "field1", -1));
        Assert.assertEquals(-5L, (long) vedis.hincrBy(key, "field1", -10));
    }

    // test redis.clients.jedis.JedisCommands: hdel(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_hdel() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.hset(key, "field1", "foo"));
        Assert.assertEquals(1L, (long) vedis.hdel(key, "field1"));
        Assert.assertEquals(0L, (long) vedis.hdel(key, "field1"));
    }

    // test redis.clients.jedis.JedisCommands: rpush(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_rpush() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.rpush(key, "hello"));
        Assert.assertEquals(2L, (long) vedis.rpush(key, "word"));
        List<String> list = vedis.lrange(key, 0, -1);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("hello", list.get(0));
        Assert.assertEquals("word", list.get(1));
    }

    // test redis.clients.jedis.JedisCommands: setnx(class java.lang.String, class java.lang.String)
    @Test
    public void test_setnx() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.setnx(key, "hello"));
        Assert.assertEquals(0L, (long) vedis.setnx(key, "world"));
        Assert.assertEquals("hello", vedis.get(key));
    }

    // test redis.clients.jedis.JedisCommands: lpush(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_lpush() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.lpush(key, "word"));
        Assert.assertEquals(2L, (long) vedis.lpush(key, "hello"));
        List<String> list = vedis.lrange(key, 0, -1);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("hello", list.get(0));
        Assert.assertEquals("word", list.get(1));
    }

    // test redis.clients.jedis.JedisCommands: ltrim(class java.lang.String, long, long)
    @Test
    public void test_ltrim() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.rpush(key, "one"));
        Assert.assertEquals(2L, (long) vedis.rpush(key, "two"));
        Assert.assertEquals(3L, (long) vedis.rpush(key, "three"));

        Assert.assertEquals(OK, vedis.ltrim(key, 1, -1));

        List<String> list = vedis.lrange(key, 0, -1);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("two", list.get(0));
        Assert.assertEquals("three", list.get(1));
    }

    // test redis.clients.jedis.JedisCommands: lset(class java.lang.String, long, class java.lang.String)
    @Test
    public void test_lset() {
        String key = getLocalKey();
        Assert.assertEquals(1L, (long) vedis.rpush(key, "one"));
        Assert.assertEquals(2L, (long) vedis.rpush(key, "two"));
        Assert.assertEquals(3L, (long) vedis.rpush(key, "three"));
        Assert.assertEquals(OK, vedis.lset(key, 0, "four"));
        Assert.assertEquals(OK, vedis.lset(key, -2, "five"));
        assertEquals(vedis.lrange(key, 0, -1), "four", "five", "three");
    }

    // test redis.clients.jedis.JedisCommands: lrem(class java.lang.String, long, class java.lang.String)
    @Test
    public void test_lrem() {
        String key = getLocalKey();
        assertEquals(1, vedis.rpush(key, "hello"));
        assertEquals(2, vedis.rpush(key, "hello"));
        assertEquals(3, vedis.rpush(key, "foo"));
        assertEquals(4, vedis.rpush(key, "hello"));
        assertEquals(2, vedis.lrem(key, -2, "hello"));
        assertEquals(vedis.lrange(key, 0, -1), "hello", "foo");
    }

    // test redis.clients.jedis.JedisCommands: lpop(class java.lang.String)
    @Test
    public void test_lpop() {
        String key = getLocalKey();
        assertEquals(1, vedis.rpush(key, "one"));
        assertEquals(2, vedis.rpush(key, "two"));
        assertEquals(3, vedis.rpush(key, "three"));
        Assert.assertEquals("one", vedis.lpop(key));
        assertEquals(vedis.lrange(key, 0, -1), "two", "three");
    }

    // test redis.clients.jedis.JedisCommands: rpop(class java.lang.String)
    @Test
    public void test_rpop() {
        String key = getLocalKey();
        assertEquals(1, vedis.rpush(key, "one"));
        assertEquals(2, vedis.rpush(key, "two"));
        assertEquals(3, vedis.rpush(key, "three"));
        assertEquals("three", vedis.rpop(key));
        assertEquals(vedis.lrange(key, 0, -1), "one", "two");
    }

    // test redis.clients.jedis.JedisCommands: sadd(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_sadd() {
        String key = getLocalKey();
        assertEquals(1, vedis.sadd(key, "hello"));
        assertEquals(1, vedis.sadd(key, "world"));
        assertEquals(0, vedis.sadd(key, "world"));
        assertEquals(vedis.smembers(key), "world", "hello");
    }

    // test redis.clients.jedis.JedisCommands: srem(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_srem() {
        String key = getLocalKey();
        assertEquals(1, vedis.sadd(key, "one"));
        assertEquals(1, vedis.sadd(key, "two"));
        assertEquals(1, vedis.sadd(key, "three"));
        assertEquals(1, vedis.srem(key, "one"));
        assertEquals(0, vedis.srem(key, "four"));
        assertEquals(vedis.smembers(key), "three", "two");
    }

    // test redis.clients.jedis.JedisCommands: spop(class java.lang.String)
    @Test
    public void test_spop() {
        String key = getLocalKey();
        assertEquals(1, vedis.sadd(key, "one"));
        assertEquals(1, vedis.sadd(key, "two"));
        assertEquals(1, vedis.sadd(key, "three"));
        vedis.spop(key);
        assertEquals(vedis.smembers(key), "three", "two");
    }

    // test redis.clients.jedis.JedisCommands: zadd(class java.lang.String, interface java.util.Map)
    @Test
    public void test_zadd() {
        String key = getLocalKey();
        assertEquals(1, vedis.zadd(key, 1, "one"));
        assertEquals(1, vedis.zadd(key, 1, "uno"));
        assertEquals(2, vedis.zadd(key, ImmutableMap.of("two", 2.0, "three", 3.0)));
        assertEquals(vedis.zrangeWithScores(key, 0, -1),
                ImmutableMap.of("one", 1.0, "uno", 1.0, "two", 2.0, "three", 3.0));
    }

    // test redis.clients.jedis.JedisCommands: zadd(class java.lang.String, double, class java.lang.String)
    @Test
    public void test_zadd1() {
        String key = getLocalKey();
        assertEquals(1, vedis.zadd(key, 1, "one"));
        assertEquals(1, vedis.zadd(key, 1, "uno"));
        assertEquals(2, vedis.zadd(key, ImmutableMap.of("two", 2.0, "three", 3.0)));
        assertEquals(vedis.zrangeWithScores(key, 0, -1),
                ImmutableMap.of("one", 1.0, "uno", 1.0, "two", 2.0, "three", 3.0));
    }

    // test redis.clients.jedis.JedisCommands: zrem(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_zrem() {
        String key = getLocalKey();
        assertEquals(1, vedis.zadd(key, 1, "one"));
        assertEquals(1, vedis.zadd(key, 2, "two"));
        assertEquals(1, vedis.zadd(key, 3, "three"));
        assertEquals(1, vedis.zrem(key, "two"));
        assertEquals(vedis.zrangeWithScores(key, 0, -1),
                ImmutableMap.of("one", 1.0, "three", 3.0));
    }

    // test redis.clients.jedis.JedisCommands: expire(class java.lang.String, int)
    @Test
    public void test_expire() {

//        redis> SET mykey "Hello"
//        OK
//        redis> EXPIRE mykey 10
//        (integer) 1
//        redis> TTL mykey
//                (integer) 10
//        redis> SET mykey "Hello World"
//        OK
//        redis> TTL mykey
//                (integer) -1
//        redis>

    }

    // test redis.clients.jedis.JedisCommands: expireAt(class java.lang.String, long)
    @Test
    public void test_expireAt() {
    }

    // test redis.clients.jedis.JedisCommands: zincrby(class java.lang.String, double, class java.lang.String)
    @Test
    public void test_zincrby() {
    }

    // test redis.clients.jedis.JedisCommands: zremrangeByRank(class java.lang.String, long, long)
    @Test
    public void test_zremrangeByRank() {
    }

    // test redis.clients.jedis.JedisCommands: zremrangeByScore(class java.lang.String, double, double)
    @Test
    public void test_zremrangeByScore() {
    }

    // test redis.clients.jedis.JedisCommands: zremrangeByScore(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zremrangeByScore1() {
    }

    // test redis.clients.jedis.JedisCommands: zremrangeByLex(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zremrangeByLex() {
    }

    // test redis.clients.jedis.JedisCommands: linsert(class java.lang.String, class redis.clients.jedis.BinaryClient$LIST_POSITION, class java.lang.String, class java.lang.String)
    @Test
    public void test_linsert() {
    }

    // test redis.clients.jedis.JedisCommands: lpushx(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_lpushx() {
    }

    // test redis.clients.jedis.JedisCommands: rpushx(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_rpushx() {
    }

    // test redis.clients.jedis.JedisCommands: blpop(int, class java.lang.String)
    @Test
    public void test_blpop() {
    }

    // test redis.clients.jedis.JedisCommands: blpop(class java.lang.String)
    @Test
    public void test_blpop1() {
    }

    // test redis.clients.jedis.JedisCommands: brpop(class java.lang.String)
    @Test
    public void test_brpop() {
    }

    // test redis.clients.jedis.JedisCommands: brpop(int, class java.lang.String)
    @Test
    public void test_brpop1() {
    }

    // test redis.clients.jedis.JedisCommands: del(class java.lang.String)
    @Test
    public void test_del() {
    }

    // test redis.clients.jedis.JedisCommands: pfadd(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_pfadd() {
    }

    // test redis.clients.jedis.JedisCommands: hexists(class java.lang.String, class java.lang.String)
    @Test
    public void test_hexists() {
    }

    // test redis.clients.jedis.JedisCommands: hlen(class java.lang.String)
    @Test
    public void test_hlen() {
    }

    // test redis.clients.jedis.JedisCommands: hkeys(class java.lang.String)
    @Test
    public void test_hkeys() {
    }

    // test redis.clients.jedis.JedisCommands: hvals(class java.lang.String)
    @Test
    public void test_hvals() {
    }

    // test redis.clients.jedis.JedisCommands: hget(class java.lang.String, class java.lang.String)
    @Test
    public void test_hget() {
    }

    // test redis.clients.jedis.JedisCommands: ttl(class java.lang.String)
    @Test
    public void test_ttl() {
    }

    // test redis.clients.jedis.JedisCommands: hmget(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_hmget() {
    }

    // test redis.clients.jedis.JedisCommands: getbit(class java.lang.String, long)
    @Test
    public void test_getbit() {
    }

    // test redis.clients.jedis.JedisCommands: getrange(class java.lang.String, long, long)
    @Test
    public void test_getrange() {
    }

    // test redis.clients.jedis.JedisCommands: hgetAll(class java.lang.String)
    @Test
    public void test_hgetAll() {
    }

    // test redis.clients.jedis.JedisCommands: llen(class java.lang.String)
    @Test
    public void test_llen() {
    }

    // test redis.clients.jedis.JedisCommands: lrange(class java.lang.String, long, long)
    @Test
    public void test_lrange() {
    }

    // test redis.clients.jedis.JedisCommands: lindex(class java.lang.String, long)
    @Test
    public void test_lindex() {
    }

    // test redis.clients.jedis.JedisCommands: smembers(class java.lang.String)
    @Test
    public void test_smembers() {
    }

    // test redis.clients.jedis.JedisCommands: scard(class java.lang.String)
    @Test
    public void test_scard() {
    }

    // test redis.clients.jedis.JedisCommands: sismember(class java.lang.String, class java.lang.String)
    @Test
    public void test_sismember() {
    }

    // test redis.clients.jedis.JedisCommands: srandmember(class java.lang.String, int)
    @Test
    public void test_srandmember() {
    }

    // test redis.clients.jedis.JedisCommands: srandmember(class java.lang.String)
    @Test
    public void test_srandmember1() {
    }

    // test redis.clients.jedis.JedisCommands: strlen(class java.lang.String)
    @Test
    public void test_strlen() {
    }

    // test redis.clients.jedis.JedisCommands: zrange(class java.lang.String, long, long)
    @Test
    public void test_zrange() {
    }

    // test redis.clients.jedis.JedisCommands: zrank(class java.lang.String, class java.lang.String)
    @Test
    public void test_zrank() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrank(class java.lang.String, class java.lang.String)
    @Test
    public void test_zrevrank() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrange(class java.lang.String, long, long)
    @Test
    public void test_zrevrange() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeWithScores(class java.lang.String, long, long)
    @Test
    public void test_zrangeWithScores() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeWithScores(class java.lang.String, long, long)
    @Test
    public void test_zrevrangeWithScores() {
    }

    // test redis.clients.jedis.JedisCommands: zcard(class java.lang.String)
    @Test
    public void test_zcard() {
    }

    // test redis.clients.jedis.JedisCommands: zscore(class java.lang.String, class java.lang.String)
    @Test
    public void test_zscore() {
    }

    // test redis.clients.jedis.JedisCommands: zcount(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zcount() {
    }

    // test redis.clients.jedis.JedisCommands: zcount(class java.lang.String, double, double)
    @Test
    public void test_zcount1() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScore(class java.lang.String, double, double, int, int)
    @Test
    public void test_zrangeByScore() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScore(class java.lang.String, double, double)
    @Test
    public void test_zrangeByScore1() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScore(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zrangeByScore2() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScore(class java.lang.String, class java.lang.String, class java.lang.String, int, int)
    @Test
    public void test_zrangeByScore3() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScore(class java.lang.String, double, double)
    @Test
    public void test_zrevrangeByScore() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScore(class java.lang.String, class java.lang.String, class java.lang.String, int, int)
    @Test
    public void test_zrevrangeByScore1() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScore(class java.lang.String, double, double, int, int)
    @Test
    public void test_zrevrangeByScore2() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScore(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zrevrangeByScore3() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScoreWithScores(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zrangeByScoreWithScores() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScoreWithScores(class java.lang.String, double, double, int, int)
    @Test
    public void test_zrangeByScoreWithScores1() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScoreWithScores(class java.lang.String, double, double)
    @Test
    public void test_zrangeByScoreWithScores2() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByScoreWithScores(class java.lang.String, class java.lang.String, class java.lang.String, int, int)
    @Test
    public void test_zrangeByScoreWithScores3() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScoreWithScores(class java.lang.String, double, double)
    @Test
    public void test_zrevrangeByScoreWithScores() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScoreWithScores(class java.lang.String, class java.lang.String, class java.lang.String, int, int)
    @Test
    public void test_zrevrangeByScoreWithScores1() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScoreWithScores(class java.lang.String, double, double, int, int)
    @Test
    public void test_zrevrangeByScoreWithScores2() {
    }

    // test redis.clients.jedis.JedisCommands: zrevrangeByScoreWithScores(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zrevrangeByScoreWithScores3() {
    }

    // test redis.clients.jedis.JedisCommands: zlexcount(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zlexcount() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByLex(class java.lang.String, class java.lang.String, class java.lang.String, int, int)
    @Test
    public void test_zrangeByLex() {
    }

    // test redis.clients.jedis.JedisCommands: zrangeByLex(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_zrangeByLex1() {
    }

    // test redis.clients.jedis.JedisCommands: echo(class java.lang.String)
    @Test
    public void test_echo() {
    }

    // test redis.clients.jedis.JedisCommands: bitcount(class java.lang.String)
    @Test
    public void test_bitcount() {
    }

    // test redis.clients.jedis.JedisCommands: bitcount(class java.lang.String, long, long)
    @Test
    public void test_bitcount1() {
    }

    // test redis.clients.jedis.JedisCommands: hscan(class java.lang.String, class java.lang.String)
    @Test
    public void test_hscan() {
    }

    // test redis.clients.jedis.JedisCommands: hscan(class java.lang.String, int)
    @Test
    public void test_hscan1() {
    }

    // test redis.clients.jedis.JedisCommands: sscan(class java.lang.String, class java.lang.String)
    @Test
    public void test_sscan() {
    }

    // test redis.clients.jedis.JedisCommands: sscan(class java.lang.String, int)
    @Test
    public void test_sscan1() {
    }

    // test redis.clients.jedis.JedisCommands: zscan(class java.lang.String, class java.lang.String)
    @Test
    public void test_zscan() {
    }

    // test redis.clients.jedis.JedisCommands: zscan(class java.lang.String, int)
    @Test
    public void test_zscan1() {
    }

    // test redis.clients.jedis.JedisCommands: hsetnx(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_hsetnx() {
    }

    // test redis.clients.jedis.JedisCommands: pfcount(class java.lang.String)
    @Test
    public void test_pfcount() {
    }

    // test redis.clients.jedis.JedisCommands: move(class java.lang.String, int)
    @Test
    public void test_move() {
    }

    // test redis.clients.jedis.MultiKeyCommands: keys(class java.lang.String)
    @Test
    public void test_keys() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sort(class java.lang.String, class redis.clients.jedis.SortingParams, class java.lang.String)
    @Test
    public void test_sort2() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sort(class java.lang.String, class java.lang.String)
    @Test
    public void test_sort3() {
    }

    // test redis.clients.jedis.MultiKeyCommands: rename(class java.lang.String, class java.lang.String)
    @Test
    public void test_rename() {
    }

    // test redis.clients.jedis.MultiKeyCommands: mset(class [Ljava.lang.String;)
    @Test
    public void test_mset() {
    }

    // test redis.clients.jedis.MultiKeyCommands: watch(class [Ljava.lang.String;)
    @Test
    public void test_watch() {
    }

    // test redis.clients.jedis.MultiKeyCommands: blpop(int, class [Ljava.lang.String;)
    @Test
    public void test_blpop2() {
    }

    // test redis.clients.jedis.MultiKeyCommands: blpop(class [Ljava.lang.String;)
    @Test
    public void test_blpop3() {
    }

    // test redis.clients.jedis.MultiKeyCommands: brpop(int, class [Ljava.lang.String;)
    @Test
    public void test_brpop2() {
    }

    // test redis.clients.jedis.MultiKeyCommands: brpop(class [Ljava.lang.String;)
    @Test
    public void test_brpop3() {
    }

    // test redis.clients.jedis.MultiKeyCommands: del(class [Ljava.lang.String;)
    @Test
    public void test_del1() {
    }

    // test redis.clients.jedis.MultiKeyCommands: msetnx(class [Ljava.lang.String;)
    @Test
    public void test_msetnx() {
    }

    // test redis.clients.jedis.MultiKeyCommands: renamenx(class java.lang.String, class java.lang.String)
    @Test
    public void test_renamenx() {
    }

    // test redis.clients.jedis.MultiKeyCommands: smove(class java.lang.String, class java.lang.String, class java.lang.String)
    @Test
    public void test_smove() {
    }

    // test redis.clients.jedis.MultiKeyCommands: rpoplpush(class java.lang.String, class java.lang.String)
    @Test
    public void test_rpoplpush() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sdiffstore(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_sdiffstore() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sinterstore(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_sinterstore() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sunionstore(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_sunionstore() {
    }

    // test redis.clients.jedis.MultiKeyCommands: unwatch()
    @Test
    public void test_unwatch() {
    }

    // test redis.clients.jedis.MultiKeyCommands: zinterstore(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_zinterstore() {
    }

    // test redis.clients.jedis.MultiKeyCommands: zinterstore(class java.lang.String, class redis.clients.jedis.ZParams, class [Ljava.lang.String;)
    @Test
    public void test_zinterstore1() {
    }

    // test redis.clients.jedis.MultiKeyCommands: zunionstore(class java.lang.String, class redis.clients.jedis.ZParams, class [Ljava.lang.String;)
    @Test
    public void test_zunionstore() {
    }

    // test redis.clients.jedis.MultiKeyCommands: zunionstore(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_zunionstore1() {
    }

    // test redis.clients.jedis.MultiKeyCommands: brpoplpush(class java.lang.String, class java.lang.String, int)
    @Test
    public void test_brpoplpush() {
    }

    // test redis.clients.jedis.MultiKeyCommands: bitop(class redis.clients.jedis.BitOP, class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_bitop() {
    }

    // test redis.clients.jedis.MultiKeyCommands: pfmerge(class java.lang.String, class [Ljava.lang.String;)
    @Test
    public void test_pfmerge() {
    }

    // test redis.clients.jedis.MultiKeyCommands: mget(class [Ljava.lang.String;)
    @Test
    public void test_mget() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sdiff(class [Ljava.lang.String;)
    @Test
    public void test_sdiff() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sinter(class [Ljava.lang.String;)
    @Test
    public void test_sinter() {
    }

    // test redis.clients.jedis.MultiKeyCommands: sunion(class [Ljava.lang.String;)
    @Test
    public void test_sunion() {
    }

    // test redis.clients.jedis.MultiKeyCommands: randomKey()
    @Test
    public void test_randomKey() {
    }

    // test redis.clients.jedis.MultiKeyCommands: subscribe(class redis.clients.jedis.JedisPubSub, class [Ljava.lang.String;)
    @Test
    public void test_subscribe() {
    }

    // test redis.clients.jedis.MultiKeyCommands: psubscribe(class redis.clients.jedis.JedisPubSub, class [Ljava.lang.String;)
    @Test
    public void test_psubscribe() {
    }

    // test redis.clients.jedis.MultiKeyCommands: pfcount(class [Ljava.lang.String;)
    @Test
    public void test_pfcount1() {
    }

    // test redis.clients.jedis.MultiKeyCommands: publish(class java.lang.String, class java.lang.String)
    @Test
    public void test_publish() {
    }

    // test redis.clients.jedis.MultiKeyCommands: scan(int)
    @Test
    public void test_scan() {
    }

    // test redis.clients.jedis.MultiKeyCommands: scan(class java.lang.String)
    @Test
    public void test_scan1() {
    }

    @Test
    @Ignore
    public void generateTestCase() {
        Map<String, Integer> counts = Maps.newHashMap();

        generate(JedisCommands.class, counts);
        generate(MultiKeyCommands.class, counts);
    }

    private void generate(Class c, Map<String, Integer> counts) {
        for (Method method : c.getMethods()) {
            String name = method.getName();
            Integer count = counts.get(name);
            if (count == null) {
                counts.put(name, 1);
            } else {
                counts.put(name, count + 1);
            }

            System.out.println("// test " + method.getDeclaringClass().getName() + ": " + getIdentifier(method));
            System.out.println("@Test");
            int coun = counts.get(name);
            if (coun == 1) {
                System.out.println("public void test_" + name + "() {");
            } else {
                System.out.println("public void test_" + name + (coun - 1) + "() {");
            }

            System.out.println("}");
        }
    }

    private static String getIdentifier(Method method) {
        String name = method.getName();
        String paramsString = StringUtils.join(method.getParameterTypes(), ", ");
        return name + "(" + paramsString + ")";
    }

    private String getLocalKey() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String key = stackTraceElement.getMethodName();
        vedis.del(key);
        return key;
    }

    private void assertEquals(List<String> list, String... expected) {
        Assert.assertEquals(expected.length, list.size());
        for (int i = 0; i < expected.length; ++i) {
            Assert.assertEquals(expected[i], list.get(i));
        }
    }

    private void assertEquals(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }

    private void assertEquals(String expected, String actual) {
        Assert.assertEquals(expected, actual);
    }

    private void assertEquals(Set<Tuple> set, ImmutableMap<String, Double> expected) {
        Assert.assertEquals(expected.size(), set.size());
        for (Tuple tuple : set) {
            Assert.assertTrue(expected.containsKey(tuple.getElement()));
            Assert.assertTrue(MathUtil.isEquale(tuple.getScore(), expected.get(tuple.getElement())));
        }
    }

    private void assertEquals(Set<String> set, String... expected) {
        Assert.assertEquals(expected.length, set.size());
        for (String s : expected) {
            Assert.assertTrue(set.contains(s));
        }
    }
}
