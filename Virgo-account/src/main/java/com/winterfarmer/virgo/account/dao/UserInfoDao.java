package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.UserInfo;

/**
 * Created by yangtianhang on 15/6/3.
 */
public interface UserInfoDao {
    boolean create(UserInfo userInfo);

    boolean update(UserInfo userInfo);

    UserInfo retrieveUser(long userId, boolean fromWrite);
}
