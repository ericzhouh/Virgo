package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;

/**
 * Created by yangtianhang on 15-3-12.
 */
public interface OAuth2Dao {
    public boolean createAccessToken(long userId, int appKey, String accessToken, long expireAt);

    public AccessToken retrieveAccessToken(long userId, int appKey);




}
