package com.winterfarmer.virgo.restapi.v1.account;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.OpenPlatformAccount;
import com.winterfarmer.virgo.account.model.PlatformType;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;
import com.winterfarmer.virgo.base.Exception.UnexpectedVirgoException;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.service.SmsService;
import com.winterfarmer.virgo.common.util.AccountUtil;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangtianhang on 15-3-30.
 */
@Path("account")
@ResourceOverview(desc = "账号接口")
@Component("accountResource")
public class AccountResource extends BaseResource {
    @Resource(name = "accountService")
    AccountService accountService;

    @Resource(name = "smsService")
    SmsService smsService;

    private static final String NICK_NAME_SPEC = "UnicodeString:2~10";
    private static final String NICK_NAME_DESC = "昵称:只能是英文,中文,数字以及-和_,并且不能全是数字";


    @Path("test.json")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        VirgoLogger.debug("this is debug");
        VirgoLogger.info("this is info");
        VirgoLogger.warn("this is warn");
        VirgoLogger.error("this is error");
        return "yes";
    }

    @Path("nick_name_existed.json")
    @GET
    @RestApiInfo(
            desc = "昵称是否存在",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.INVALID_NICK_NAME,
                    RestExceptionFactor.NICK_NAME_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult isNickNameExisted(
            @QueryParam("nick_name")
            @ParamSpec(isRequired = true, spec = NICK_NAME_SPEC, desc = NICK_NAME_DESC)
            String nickName) {
        checkAndPurifyNickName(nickName);
        return CommonResult.oneResultCommonResult("existed", false);
    }

    @Path("mobile_existed.json")
    @GET
    @RestApiInfo(
            desc = "昵称是否存在",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.INVALID_MOBILE_NUMBER}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult isMobileExisted(
            @QueryParam("mobile_number")
            @ParamSpec(isRequired = true, spec = MOBILE_SPEC, desc = "")
            String mobileNumber) {
        try {
            boolean isExisted = accountService.isMobileExisted(mobileNumber);
            return CommonResult.oneResultCommonResult("existed", isExisted);
        } catch (MobileNumberException e) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_MOBILE_NUMBER);
        }
    }

    @Path("mobile_verification_code.json")
    @GET
    @RestApiInfo(
            desc = "根据手机号发送验证码",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.INVALID_MOBILE_NUMBER,
                    RestExceptionFactor.REQUEST_SIGN_UP_MOBILE_VERIFICATION_CODE_TOO_FREQUENTLY}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult getMobileVerificationCode(
            @QueryParam("mobile_number")
            @ParamSpec(isRequired = true, spec = MOBILE_SPEC, desc = "")
            String mobileNumber) {
        mobileNumber = checkAndStandardizeMobileNumber(mobileNumber);

        if (accountService.isRequestSignUpMobileVerificationCodeTooFrequently(mobileNumber)) {
            throw new VirgoRestException(RestExceptionFactor.REQUEST_SIGN_UP_MOBILE_VERIFICATION_CODE_TOO_FREQUENTLY);
        }

        if (!smsService.sendSignUpMobileVerificationCode(mobileNumber, AccountUtil.generateMobileCode(mobileNumber))) {
            return CommonResult.isSuccessfulCommonResult(false);
        }

        accountService.cacheSentSignUpMobileVerificationCode(mobileNumber);

        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("mobile_access_token.json")
    @POST
    @RestApiInfo(
            desc = "注册并获取access token",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = AccessToken.class,
            errors = {RestExceptionFactor.INVALID_MOBILE_NUMBER,
                    RestExceptionFactor.INVALID_MOBILE_VERIFICATION_CODE,
                    RestExceptionFactor.MOBILE_NUMBER_HAS_BEEN_REGISTERED,
                    RestExceptionFactor.INVALID_NICK_NAME,
                    RestExceptionFactor.NICK_NAME_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public AccessToken signUpByMobile(
            @FormParam("mobile_number")
            @ParamSpec(isRequired = true, spec = MOBILE_SPEC, desc = "电话号码")
            String mobileNumber,
            @FormParam("password")
            @ParamSpec(isRequired = true, spec = PASSWORD_SPEC, desc = "密码6~24位")
            String password,
            @FormParam("nick_name")
            @ParamSpec(isRequired = true, spec = NICK_NAME_SPEC, desc = NICK_NAME_DESC)
            String nickName,
            @FormParam("open_token")
            @ParamSpec(isRequired = true, spec = "UnicodeString:2~10", desc = "第三方token")
            String openToken,
            @FormParam("app_key")
            @ParamSpec(isRequired = true, spec = APP_KEY_SPEC, desc = "web:1005")
            int appKey,
            @FormParam("verification_code")
            @ParamSpec(isRequired = true, spec = VERIFICATION_CODE_SPEC, desc = "短信验证码")
            int verificationCode) {
        mobileNumber = checkAndStandardizeMobileNumber(mobileNumber);
        checkMobileVerificationCode(mobileNumber, verificationCode);
        Long userId = accountService.getUserId(mobileNumber, PlatformType.MOBILE);
        if (userId != null) {
            throw new VirgoRestException(RestExceptionFactor.MOBILE_NUMBER_HAS_BEEN_REGISTERED);
        }

        nickName = checkAndPurifyNickName(nickName);
        try {
            return accountService.signUpByMobile(mobileNumber, password, nickName,
                    openToken, appKey);
        } catch (MobileNumberException e) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_MOBILE_NUMBER);
        }
    }

    @Path("access_token.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "获取access token",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = AccessToken.class,
            errors = {RestExceptionFactor.INVALID_MOBILE_NUMBER,
                    RestExceptionFactor.INVALID_ID_OR_PASSWORD
            }
    )
    public AccessToken getAccessToken(
            @QueryParam("mobile_number")
            @ParamSpec(isRequired = true, spec = MOBILE_SPEC, desc = "")
            String mobileNumber,
            @QueryParam("password")
            @ParamSpec(isRequired = true, spec = PASSWORD_SPEC, desc = "")
            String password,
            @QueryParam("app_key")
            @ParamSpec(isRequired = true, spec = APP_KEY_SPEC, desc = "")
            int appKey
    ) {
        mobileNumber = checkAndStandardizeMobileNumber(mobileNumber);
        User user = checkAndGetUser(mobileNumber, PlatformType.MOBILE);

        String hashedPassword = accountService.getHashedPassword(password, user.getSalt());
        if (hashedPassword.equals(user.getHashedPassword())) {
            AccessToken accessToken = accountService.getAccessToken(user.getUserId(), appKey);
            if (accessToken == null || accessToken.isExpire()) {
                accountService.deleteAccessToken(user.getUserId(), appKey);
                return accountService.createAccessToken(user.getUserId(), appKey);
            } else {
                return accessToken;
            }
        } else {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ID_OR_PASSWORD);
        }
    }

    @Path("password.json")
    @POST
    @RestApiInfo(
            desc = "重置密码",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.INVALID_MOBILE_NUMBER,
                    RestExceptionFactor.INVALID_MOBILE_VERIFICATION_CODE,
                    RestExceptionFactor.RESET_PASSWORD_FAILED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult resetPassword(
            @FormParam("mobile_number")
            @ParamSpec(isRequired = true, spec = MOBILE_SPEC, desc = "")
            String mobileNumber,
            @FormParam("password")
            @ParamSpec(isRequired = true, spec = PASSWORD_SPEC, desc = "")
            String password,
            @FormParam("verification_code")
            @ParamSpec(isRequired = true, spec = VERIFICATION_CODE_SPEC, desc = "")
            int verificationCode) {
        mobileNumber = checkAndStandardizeMobileNumber(mobileNumber);
        checkMobileVerificationCode(mobileNumber, verificationCode);
        User user = checkAndGetUser(mobileNumber, PlatformType.MOBILE);
        try {
            accountService.resetPassword(user, password);
        } catch (UnexpectedVirgoException e) {
            throw new VirgoRestException(RestExceptionFactor.RESET_PASSWORD_FAILED);
        }
        return CommonResult.isSuccessfulCommonResult(true);
    }


    @Path("testing.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(authPolicy = RestApiInfo.AuthPolicy.OAUTH)
    public AccessToken testing(
    ) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAppKey(100);
        accessToken.setToken("00000");
        return accessToken;
    }

    private String checkAndStandardizeMobileNumber(String mobileNumber) {
        mobileNumber = AccountUtil.formatMobile(mobileNumber);
        if (mobileNumber == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_MOBILE_NUMBER);
        } else {
            return mobileNumber;
        }
    }

    private void checkMobileVerificationCode(String mobilePhone, int code) {
        if (!AccountUtil.checkMobileCode(mobilePhone, code)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_MOBILE_VERIFICATION_CODE);
        }
    }

    private User checkAndGetUser(String openId, PlatformType platformType) {
        OpenPlatformAccount openPlatformAccount = accountService.getOpenPlatformAccount(openId, platformType);
        if (openPlatformAccount == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ID_OR_PASSWORD);
        }

        User user = accountService.getUser(openPlatformAccount.getUserId());
        if (user == null) {
            VirgoLogger.error("no user id for open platform account: {}", openId);
            throw new VirgoRestException(RestExceptionFactor.INVALID_ID_OR_PASSWORD);
        }

        return user;
    }


    private static String regex = "^[a-zA-Z0-9\u4E00-\u9FA5-_]+$";
    private Pattern pattern = Pattern.compile(regex);

    private static String allNumberRegex = "^\\d+$";
    private Pattern allNumberPattern = Pattern.compile(allNumberRegex);

    private String checkAndPurifyNickName(String nickName) {
        nickName = StringUtils.trim(nickName);
        if (StringUtils.length(nickName) < 2) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_NICK_NAME);
        }
        Matcher matcher = pattern.matcher(nickName);
        Matcher allNumberMatcher = allNumberPattern.matcher(nickName);

        if (matcher.matches() && !allNumberMatcher.matches()) {
            if (accountService.isNickNameExisted(nickName)) {
                throw new VirgoRestException(RestExceptionFactor.NICK_NAME_EXISTED);
            } else {
                return nickName;
            }
        } else {
            throw new VirgoRestException(RestExceptionFactor.INVALID_NICK_NAME);
        }
    }
}
