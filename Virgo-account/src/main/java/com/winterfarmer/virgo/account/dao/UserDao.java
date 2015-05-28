package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccountVersion;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.account.model.UserType;

import java.util.Map;

/**
 * Created by yangtianhang on 15-3-4.
 */
public interface UserDao {
    User retrieveUser(long userId, boolean fromWrite);

    boolean createUser(long userId, String nickName, UserType userType, String hashedPassword, String salt, AccountVersion version, Map<String, Object> extInfo);

    boolean updateUser(User user);

    boolean updatePassword(long userId, String password);

    User retrieveUserByNickName(String nickName);
}
