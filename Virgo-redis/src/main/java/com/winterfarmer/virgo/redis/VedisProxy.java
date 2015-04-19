package com.winterfarmer.virgo.redis;

import com.google.common.collect.Maps;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.redis.command.JedisReadCommands;
import com.winterfarmer.virgo.redis.command.JedisWriteCommands;
import com.winterfarmer.virgo.redis.command.MultiKeyReadCommands;
import com.winterfarmer.virgo.redis.command.MultiKeyWriteCommands;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-8.
 */
public class VedisProxy implements InvocationHandler {
    private final JedisWritePoolStorage jedisWritePoolFactory;
    private final JedisReadPoolStorage jedisReadPoolFactory;
    private final Map<String, Pair<Method, JedisPoolStorage>> methodJedisPoolFactoryMap;

    public VedisProxy(JedisPoolFactory jedisPoolFactory) {
        this.jedisWritePoolFactory = jedisPoolFactory.getJedisWritePoolFactory();
        this.jedisReadPoolFactory = jedisPoolFactory.getJedisReadPoolFactory();
        this.methodJedisPoolFactoryMap = createMethodJedisPoolFactoryMap(jedisWritePoolFactory, jedisReadPoolFactory);
    }

    public Vedis newVedis() {
        return (Vedis) Proxy.newProxyInstance(Vedis.class.getClassLoader(), new Class[]{Vedis.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Pair<Method, JedisPoolStorage> poolMethod = methodJedisPoolFactoryMap.get(method.toGenericString());
        if (poolMethod == null) {
            throw new NoSuchMethodException("no jedis pool factory for vedis method [" + method.getName() + "]");
        }
        JedisPool jedisPool = poolMethod.getRight().getJedisPool();
        Jedis jedis = jedisPool.getResource();

        try {
            Method jedisMethod = poolMethod.getLeft();
            result = jedisMethod.invoke(jedis, args);
        } catch (Exception e) {
            VirgoLogger.error("Jedis exception", e);
            jedisPool.returnBrokenResource(jedis);
        } finally {
            try {
                jedisPool.returnResource(jedis);
            } catch (Exception e) {
                VirgoLogger.error("Jedis returnResource exception", e);
            }
        }

        return result;
    }

    private static Map<String, Pair<Method, JedisPoolStorage>> createMethodJedisPoolFactoryMap(JedisWritePoolStorage jedisWritePoolFactory, JedisReadPoolStorage jedisReadPoolFactory) {
        Map<String, Pair<Method, JedisPoolStorage>> map = Maps.newHashMap();
        Map<String, Method> jedisMethodMap = getJedisMethodMap();

        putCommands(map, jedisMethodMap, JedisReadCommands.class.getMethods(), jedisReadPoolFactory);
        putCommands(map, jedisMethodMap, JedisWriteCommands.class.getMethods(), jedisWritePoolFactory);
        putCommands(map, jedisMethodMap, MultiKeyReadCommands.class.getMethods(), jedisReadPoolFactory);
        putCommands(map, jedisMethodMap, MultiKeyWriteCommands.class.getMethods(), jedisWritePoolFactory);

        return map;
    }

    private static Map<String, Method> getJedisMethodMap() {
        Map<String, Method> jedisMethodMap = Maps.newHashMap();
        for (Method method : Jedis.class.getMethods()) {
            jedisMethodMap.put(getIdentifier(method), method);
        }

        return jedisMethodMap;
    }

    private static String getIdentifier(Method method) {
        String name = method.getName();
        String paramsString = StringUtils.join(method.getParameterTypes(), ',');
        return name + "(" + paramsString + ")";
    }

    private static void putCommands(Map<String, Pair<Method, JedisPoolStorage>> map, Map<String, Method> jedisMethodMap, Method[] methods, JedisPoolStorage jedisPoolStorage) {
        for (Method method : methods) {
            String methodIdentifier = getIdentifier(method);
            Method jedisMethod = jedisMethodMap.get(methodIdentifier);
            if (jedisMethod == null) {
                throw new RuntimeException("Jedis has no method: " + method.getName());
            }

            map.put(method.toGenericString(), Pair.of(jedisMethod, jedisPoolStorage));
        }
    }
}
