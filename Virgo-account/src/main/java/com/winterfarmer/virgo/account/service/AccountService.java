package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;

/**
 * Created by yangtianhang on 15-2-14.
 */
public interface AccountService {
    AccessToken signUpByMobile(String mobileNumber, String password, String nickName,
                               String openToken, int appKey) throws MobileNumberException;

    AccessToken getAccessToken(String tokenString);

    AccessToken getAccessToken(long userId, int appKey);

    void cacheSentSignUpMobileVerificationCode(String mobileNumber);

    boolean isRequestSignUpMobileVerificationCodeTooFrequently(String mobileNumber);
}
