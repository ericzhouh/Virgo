package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.AccessToken;

/**
 * Created by yangtianhang on 15-2-14.
 */
public interface AccountService {
    AccessToken signUpByMobile(String mobileNumber, String password, String nickName,
                               String openToken, int appKey);

    AccessToken getAccessTokenByTokenString(String tokenString);
}
