package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.*;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;
import com.winterfarmer.virgo.base.Exception.UnexpectedVirgoException;
import com.winterfarmer.virgo.base.model.CommonState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by yangtianhang on 15-2-14.
 */
public interface AccountService {
    Account getAccount(long userId);

    List<Account> getAccounts(long... userIds);

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

    void resetPassword(Account account, String password) throws UnexpectedVirgoException;

    boolean deleteAccessToken(long userId, int appKey);

    AccessToken createAccessToken(long userId, int appKey);

    Long getUserId(String openId, PlatformType platformType);

    String getRandomNickName();

    UserInfo getUserInfo(long userId);

    UserInfo updateUserInfo(UserInfo userInfo);

    Pair<UserInfo, ExpertApplying> updateUserInfoAndApplyExpert(UserInfo userInfo, String reason, long[] tagIds) throws UnexpectedVirgoException;

    void updateApplyingExpertTags(UserInfo userInfo, long[] tagIds) throws UnexpectedVirgoException;

    boolean updateApplyingExpertReason(UserInfo userInfo, String reason) throws UnexpectedVirgoException;

    ExpertApplying getExpertApplying(long userId);

    boolean followTag(long userId, long tagId, CommonState state);

    List<Long> listFollowTags(long userId);
}
