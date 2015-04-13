package com.winterfarmer.virgo.base.dao;

/**
 * Created by yangtianhang on 15-4-11.
 * redis 服务的基类
 * 定义了key的namespace
 */
public abstract class BaseRedisDao {
    protected static final String SET_IF_NOT_EXIST = "NX";
    protected static final String SET_IF_ALREAD_EXIST = "XX";
    protected static final String TU_SECONDS = "EX";
    protected static final String TU_MILLISECONDS = "PX";

    protected static final int ACCOUNT_USER_ACCESS_TOKEN = 101;
    protected static final int SIGN_UP_VERIFICATION_CODE_REQUEST = 102;
    protected static final int MOBILE_TO_USER_ID = 103;

    protected String getKey(int namespace, String key) {
        return namespace + "_" + key;
    }
}
