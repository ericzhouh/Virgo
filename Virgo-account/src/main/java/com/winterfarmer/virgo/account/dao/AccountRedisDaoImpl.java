package com.winterfarmer.virgo.account.dao;

import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.base.dao.BaseRedisDao;
import com.winterfarmer.virgo.data.redis.RedisBiz;
import com.winterfarmer.virgo.data.redis.Vedis;
import com.winterfarmer.virgo.data.redis.VedisFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * Created by yangtianhang on 15-4-11.
 */
@Repository(value = "accountRedisDao")
public class AccountRedisDaoImpl extends BaseRedisDao implements AccountRedisDao {
    private Vedis vedis;

    @PostConstruct
    public void init() {
        vedis = VedisFactory.getVedis(RedisBiz.Account);
    }

    @Override
    public AccessToken getAccessToken(long userId, int appKey) {
        String key = getAccessTokenKey(userId, appKey);
        String val = vedis.get(key);
        if (val != null) {
            return JSON.parseObject(val, AccessToken.class);
        } else {
            return null;
        }
    }

    @Override
    public void insertAccessToken(AccessToken accessToken) {
        String val = JSON.toJSONString(accessToken);
        String key = getAccessTokenKey(accessToken.getUserId(), accessToken.getAppKey());
        vedis.set(key, val);
        vedis.expireAt(key, accessToken.getExpireAt());
    }

    @Override
    public void cacheSignUpMobileRequest(String mobileNumber, int expireS) {
        String key = getKey(SIGN_UP_VERIFICATION_CODE_REQUEST, mobileNumber);
        vedis.set(key, "Y", SET_IF_NOT_EXIST, TU_SECONDS, expireS);
    }

    @Override
    public Object getSignUpMobileRequest(String mobileNumber) {
        return vedis.get(getKey(SIGN_UP_VERIFICATION_CODE_REQUEST, mobileNumber));
    }

    @Override
    public void deleteAccessToken(long userId, int appKey) {
        vedis.del(getAccessTokenKey(userId, appKey));
    }

    @Override
    public Long getUserIdByMobile(String mobileNumber) {
        String userIdStr = vedis.get(getKey(MOBILE_TO_USER_ID, mobileNumber));
        return userIdStr == null ? null : Long.parseLong(userIdStr);
    }

    @Override
    public void setMobileUserId(String mobileNumber, long userId) {
        vedis.set(getKey(MOBILE_TO_USER_ID, mobileNumber), Long.toString(userId));
    }

    private String getAccessTokenKey(long userId, int appKey) {
        return getKey(ACCOUNT_USER_ACCESS_TOKEN, getAccountSubKey(userId, appKey));
    }

    private String getAccountSubKey(long userId, int appKey) {
        return appKey + ":" + userId;
    }
}
