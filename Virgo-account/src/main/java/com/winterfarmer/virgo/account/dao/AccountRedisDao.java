package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;

import java.util.Map;

/**
 * Created by yangtianhang on 15-4-11.
 */
public interface AccountRedisDao {
    AccessToken getAccessToken(long userId, int appKey);

    void insertAccessToken(AccessToken accessToken);

    void cacheSignUpMobileRequest(String mobileNumber, int expireS);

    Object getSignUpMobileRequest(String mobileNumber);

    void deleteAccessToken(long userId, int appKey);

    Long getUserIdByMobile(String mobileNumber);

    void setMobileUserId(String mobileNumber, long userId);

    boolean hasPrivilege(long userId, Map<GroupType, Integer> privilegeMap);

    void setPrivilege(Privilege privilege);
}
