package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.UserInfo;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/6/3.
 */
@Repository(value = "userInfoHybridDao")
public class UserInfoHybridDaoImpl implements UserInfoDao {
    @Resource(name = "userInfoRedisDao")
    UserInfoDao redisDao;

    @Resource(name = "userInfoMysqlDao")
    UserInfoDao mysqlDao;

    @Override
    public boolean create(UserInfo userInfo) {
        if (!mysqlDao.create(userInfo)) {
            return false;
        }
        return redisDao.create(userInfo);
    }

    @Override
    public boolean update(UserInfo userInfo) {
        if (!mysqlDao.update(userInfo)) {
            return false;
        }

        return redisDao.update(userInfo);
    }

    @Override
    public UserInfo retrieveUser(long userId, boolean fromWrite) {
        if (fromWrite) {
            return mysqlDao.retrieveUser(userId, true);
        }

        UserInfo userInfo = redisDao.retrieveUser(userId, false);
        if (userInfo == null) {
            userInfo = mysqlDao.retrieveUser(userId, false);
            if (userInfo != null) {
                redisDao.create(userInfo);
            }
        }

        return userInfo;
    }
}
