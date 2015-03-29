package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;

/**
 * Created by yangtianhang on 15-3-26.
 */
public interface AccessTokenDao {
    AccessToken retrieveAccessToken(long userId, int appKey);

    public boolean createAccessToken(long userId, int appKey, String accessToken, long expireAt);

    public boolean updateAccessToken(long userId, int appKey, String accessToken, long expireAt);
}
