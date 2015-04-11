package com.winterfarmer.virgo.base.redis.service;

/**
 * Created by yangtianhang on 15-4-11.
 * redis 服务的基类
 * 定义了key的namespace
 */
public abstract class BaseRedisService {
    protected static final int OAUTH2_BASE = 100;
    protected static final int OAUTH2_USER_ACCESS_TOKEN = OAUTH2_BASE + 1;

    protected String getKey(int namespace, String key) {
        return namespace + "_" + key;
    }
}
