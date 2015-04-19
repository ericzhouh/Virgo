package com.winterfarmer.virgo.account.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;
import com.winterfarmer.virgo.base.dao.BaseRedisDao;
import com.winterfarmer.virgo.redis.RedisBiz;
import com.winterfarmer.virgo.redis.Vedis;
import com.winterfarmer.virgo.redis.VedisFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-11.
 */
@Repository(value = "accountRedisDao")
public class AccountRedisDaoImpl extends BaseRedisDao implements AccountRedisDao {
//    @Resource(name = "accountVedis")
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

    @Override
    public boolean hasPrivilege(long userId, Map<GroupType, Integer> privilegeMap) {
        for (Map.Entry<GroupType, Integer> entry : privilegeMap.entrySet()) {
            String key = getKey(PRIVILEGE, entry.getKey().getIndex());
            String privilege = vedis.hget(key, Long.toString(userId));
            if (privilege == null) {
                return false;
            }

            Integer privilegeBits = Integer.parseInt(privilege);
            if (!Privilege.hasPrivileges(entry.getValue(), privilegeBits)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void setPrivilege(Privilege privilege) {
        String key = getKey(PRIVILEGE, privilege.getGroupType().getIndex());

        vedis.hset(key, Long.toString(privilege.getUserId()), Integer.toString(privilege.getPrivileges()));
    }

    private String getAccessTokenKey(long userId, int appKey) {
        return getKey(ACCOUNT_USER_ACCESS_TOKEN, getAccountSubKey(userId, appKey));
    }

    private String getAccountSubKey(long userId, int appKey) {
        return appKey + ":" + userId;
    }

    private Map<GroupType, Integer> getPrivilegeMap(List<Role> roleList) {
        Map<GroupType, Integer> privilegeMap = Maps.newHashMap();
        for (Role role : roleList) {
            if (!privilegeMap.containsKey(role.getGroupType())) {
                privilegeMap.put(role.getGroupType(), 0);
            }

            int value = privilegeMap.get(role.getGroupType());
            value += role.getRolePrivilege().getBit();
            privilegeMap.put(role.getGroupType(), value);
        }

        return privilegeMap;
    }
}
