package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.OpenPlatformAccount;
import com.winterfarmer.virgo.account.model.PlatformType;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;
import com.winterfarmer.virgo.base.Exception.UnexpectedVirgoException;

import java.util.List;

/**
 * Created by yangtianhang on 15-2-14.
 */
public interface AccountService {
    User getUser(long userId);

    List<User> getUsers(long... userIds);

    boolean isNickNameExisted(String nickName);

    boolean isMobileExisted(String mobile) throws MobileNumberException;

    String getHashedPassword(String password, String salt);

    AccessToken signUpByMobile(String mobileNumber, String password, String nickName,
                               String openToken, int appKey) throws MobileNumberException;

    AccessToken getAccessToken(String tokenString);

    AccessToken getAccessToken(long userId, int appKey);

    void cacheSentSignUpMobileVerificationCode(String mobileNumber);

    boolean isRequestSignUpMobileVerificationCodeTooFrequently(String mobileNumber);

    OpenPlatformAccount getOpenPlatformAccount(String openId, PlatformType platformType);

    void resetPassword(User user, String password) throws UnexpectedVirgoException;

    boolean deleteAccessToken(long userId, int appKey);

    AccessToken createAccessToken(long userId, int appKey);

    Long getUserId(String openId, PlatformType platformType);

    String getRandomNickName();
}
