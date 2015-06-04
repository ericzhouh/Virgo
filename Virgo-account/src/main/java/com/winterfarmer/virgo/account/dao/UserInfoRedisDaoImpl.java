package com.winterfarmer.virgo.account.dao;

import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.account.model.UserInfo;
import com.winterfarmer.virgo.base.dao.BaseRedisDao;
import com.winterfarmer.virgo.redis.RedisBiz;
import com.winterfarmer.virgo.redis.Vedis;
import com.winterfarmer.virgo.redis.VedisFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * Created by yangtianhang on 15/6/3.
 */
@Repository(value = "userInfoRedisDao")
public class UserInfoRedisDaoImpl extends BaseRedisDao implements UserInfoDao {
    private Vedis vedis;

    @PostConstruct
    public void init() {
        vedis = VedisFactory.getVedis(RedisBiz.UserInfo);
    }

    @Override
    public boolean create(UserInfo userInfo) {
        if (userInfo.getUserId() <= 0) {
            return false;
        }

        String key = getKey(USER_INFO, userInfo.getUserId());
        String val = JSON.toJSONString(userInfo);
        return successful(vedis.set(key, val));
    }

    @Override
    public boolean update(UserInfo userInfo) {
        return create(userInfo);
    }

    @Override
    public UserInfo retrieveUser(long userId, boolean fromWrite) {
        String key = getKey(USER_INFO, userId);
        String val = vedis.get(key);
        if (val != null) {
            return JSON.parseObject(val, UserInfo.class);
        } else {
            return null;
        }
    }
}
