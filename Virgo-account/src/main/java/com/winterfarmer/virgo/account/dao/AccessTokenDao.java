package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;

/**
 * Created by yangtianhang on 15-3-26.
 */
public interface AccessTokenDao {
    AccessToken retrieveAccessToken(long userId, int appKey);

    boolean createAccessToken(long userId, int appKey, String accessToken, long expireAt);

    boolean updateAccessToken(long userId, int appKey, String accessToken, long expireAt);

    boolean deleteAccessToken(long userId);

    boolean deleteAccessToken(long userId, int appKey);
}
