package com.winterfarmer.virgo.restapi.v1.account;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.account.model.*;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.aggregator.model.ApiQuestionTag;
import com.winterfarmer.virgo.aggregator.model.ApiUser;
import com.winterfarmer.virgo.aggregator.model.ApiUserCounter;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;
import com.winterfarmer.virgo.base.Exception.UnexpectedVirgoException;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.base.service.SmsService;
import com.winterfarmer.virgo.base.util.StaticFileUtil;
import com.winterfarmer.virgo.common.util.AccountUtil;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.common.util.StringUtil;
import com.winterfarmer.virgo.knowledge.model.KnowledgeCounterType;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import com.winterfarmer.virgo.knowledge.service.KnowledgeService;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.util.List;
import java.util.Map;
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

    @Resource(name = "knowledgeService")
    KnowledgeService knowledgeService;

    public static final int MAX_TAG_NUMBER = 5;

    private static final String NICK_NAME_SPEC = "UnicodeString:2~15";
    private static final String NICK_NAME_DESC = "昵称:只能是英文,中文,数字以及-和_,并且不能全是数字2~15个字符";

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
            @ParamSpec(isRequired = false, spec = NICK_NAME_SPEC, desc = NICK_NAME_DESC)
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

        if (StringUtils.isEmpty(nickName)) {
            nickName = accountService.getRandomNickName();
        } else {
            nickName = checkAndPurifyNickName(nickName);
        }

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
        Account account = checkAndGetAccount(mobileNumber, PlatformType.MOBILE);

        String hashedPassword = accountService.getHashedPassword(password, account.getSalt());
        if (hashedPassword.equals(account.getHashedPassword())) {
            AccessToken accessToken = accountService.getAccessToken(account.getUserId(), appKey);
            if (accessToken == null || accessToken.isExpire()) {
                accountService.deleteAccessToken(account.getUserId(), appKey);
                accessToken = accountService.createAccessToken(account.getUserId(), appKey);
            }

            return accessToken;
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
        Account account = checkAndGetAccount(mobileNumber, PlatformType.MOBILE);
        try {
            accountService.resetPassword(account, password);
        } catch (UnexpectedVirgoException e) {
            throw new VirgoRestException(RestExceptionFactor.RESET_PASSWORD_FAILED);
        }
        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("update_user_info.json")
    @POST
    @RestApiInfo(
            desc = "修改用户信息",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiUser.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.INVALID_NICK_NAME,
                    RestExceptionFactor.NICK_NAME_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiUser updateUserInfo(
            @FormParam("nick_name")
            @ParamSpec(isRequired = true, spec = NICK_NAME_SPEC, desc = NICK_NAME_DESC)
            String nickName,
            @FormParam("portrait")
            @ParamSpec(isRequired = false, spec = IMAGE_SPEC, desc = IMAGE_DESC)
            @DefaultValue("0")
            String portrait,
            @FormParam("gender")
            @ParamSpec(isRequired = false, spec = "int:[0,1]", desc = "性别{0,1}->{M,F}")
            @DefaultValue("0")
            String gender,
            @FormParam("birthday")
            @ParamSpec(isRequired = false, spec = "string:10~10", desc = "生日:1970-01-01的形式")
            String birthday,
            @FormParam("email")
            @ParamSpec(isRequired = false, spec = "string:0~200", desc = "email")
            String email,
            @FormParam("introduction")
            @ParamSpec(isRequired = false, spec = "string:0~500", desc = "自我介绍")
            String introduction,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        nickName = checkAndPurifyNickName(nickName, userInfo.getNickName());

        userInfo.setNickName(nickName);
        userInfo.setGender(Integer.parseInt(gender));
        userInfo.setPortrait(portrait);
        if (birthday != null) {
            try {
                Date birthdayDate = Date.valueOf(birthday);
                userInfo.setBirthday(birthdayDate);
            } catch (IllegalArgumentException e) {
                throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, "invalid birthday");
            }
        }
        if (email != null) {
            userInfo.setEmail(email);
        }
        if (introduction != null) {
            userInfo.setIntroduction(introduction);
        }
        userInfo = accountService.updateUserInfo(userInfo);
        return new ApiUser(userInfo);
    }

    @Path("apply_expert.json")
    @POST
    @RestApiInfo(
            desc = "申请专家",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiUser.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.INVALID_TAG_ID}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiUser applyExpert(
            @FormParam("nick_name")
            @ParamSpec(isRequired = true, spec = NICK_NAME_SPEC, desc = NICK_NAME_DESC)
            String nickName,
            @FormParam("tags")
            @ParamSpec(isRequired = true, spec = "string:1~100", desc = "逗号分开的tag,最多5个")
            String tagsString,
            @FormParam("reason")
            @ParamSpec(isRequired = true, spec = "UnicodeString:10~500", desc = "申请理由, 最少10个字")
            String reason,
            @FormParam("portrait")
            @ParamSpec(isRequired = false, spec = IMAGE_SPEC, desc = IMAGE_DESC)
            @DefaultValue("0")
            String portrait,
            @FormParam("gender")
            @ParamSpec(isRequired = false, spec = "int:[0,1]", desc = "性别{0,1}->{M,F}")
            @DefaultValue("0")
            String gender,
            @FormParam("birthday")
            @ParamSpec(isRequired = false, spec = "string:10~10", desc = "生日:1970-01-01的形式")
            String birthday,
            @FormParam("email")
            @ParamSpec(isRequired = false, spec = "string:0~200", desc = "email")
            String email,
            @FormParam("introduction")
            @ParamSpec(isRequired = false, spec = "string:0~500", desc = "自我介绍")
            String introduction,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        if (userInfo.getUserType() == UserType.EXPERT) {
            throw new VirgoRestException(RestExceptionFactor.ALREADY_EXPERT);
        }

        nickName = checkAndPurifyNickName(nickName, userInfo.getNickName());

        long[] tagIds = checkAndGetTagIds(tagsString);
        userInfo.setNickName(nickName);
        userInfo.setGender(Integer.parseInt(gender));
        userInfo.setPortrait(portrait);
        if (birthday != null) {
            try {
                Date birthdayDate = Date.valueOf(birthday);
                userInfo.setBirthday(birthdayDate);
            } catch (IllegalArgumentException e) {
                throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, "invalid birthday");
            }
        }
        if (email != null) {
            userInfo.setEmail(email);
        }
        if (introduction != null) {
            userInfo.setIntroduction(introduction);
        }

        try {
            Pair<UserInfo, ExpertApplying> result = accountService.updateUserInfoAndApplyExpert(userInfo, reason, tagIds);
            return new ApiUser(result.getLeft(), result.getRight(), knowledgeService.listQuestionTag(tagIds));
        } catch (UnexpectedVirgoException e) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, "applying failed");
        }
    }

    @Path("modify_apply_expert_tags.json")
    @POST
    @RestApiInfo(
            desc = "修改申请专家的擅长标签",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.ALREADY_EXPERT}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult modifyApplyingExpertReason(
            @FormParam("reason")
            @ParamSpec(isRequired = true, spec = "UnicodeString:10~500", desc = "申请理由, 最少10个字")
            String reason,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        if (userInfo.getUserType() == UserType.EXPERT) {
            throw new VirgoRestException(RestExceptionFactor.ALREADY_EXPERT);
        }

        try {
            boolean successful = accountService.updateApplyingExpertReason(userInfo, reason);
            return CommonResult.isSuccessfulCommonResult(successful);
        } catch (UnexpectedVirgoException e) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, "updateApplyingExpert applying failed");
        }
    }

    @Path("modify_apply_expert_reason.json")
    @POST
    @RestApiInfo(
            desc = "修改申请专家的申请原因",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.ALREADY_EXPERT}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult applyExpert(
            @FormParam("tags")
            @ParamSpec(isRequired = true, spec = "string:1~100", desc = "逗号分开的tag,最多5个")
            String tagsString,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        if (userInfo == null) {
            throw new VirgoRestException(RestExceptionFactor.USER_ID_NOT_EXISTED);
        }
        if (userInfo.getUserType() == UserType.EXPERT) {
            throw new VirgoRestException(RestExceptionFactor.ALREADY_EXPERT);
        }

        long[] tagIds = checkAndGetTagIds(tagsString);
        try {
            accountService.updateApplyingExpertTags(userInfo, tagIds);
            return CommonResult.isSuccessfulCommonResult(true);
        } catch (UnexpectedVirgoException e) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, "updateApplyingExpert applying failed");
        }
    }

    private static final String COMMON_RESULT_APPLYING_EXPERT_STATE = "applying_expert_state";

    private static final int APPLYING_EXPERT_STATE_NO_APPLY = 0;
    private static final int APPLYING_EXPERT_STATE_APPLYING = 1;
    private static final int APPLYING_EXPERT_STATE_ALREADY_EXPERT = 2;

    @Path("check_applying_expert_state.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "检查申请专家状态:{0,1,2}->{没有申请,申请中,申请通过已经是专家}," +
                    "返回{'applying_expert_state':0}这样的形式",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    public CommonResult checkUserType(
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        if (userInfo.getUserType() == UserType.EXPERT) {
            return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_ALREADY_EXPERT);
        } else {
            ExpertApplying expertApplying = accountService.getExpertApplying(userId);
            if (expertApplying == null) {
                return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_NO_APPLY);
            } else {
                switch (expertApplying.getState()) {
                    case ExpertApplying.APPLYING:
                        return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_APPLYING);
                    case ExpertApplying.REJECT:
                        return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_APPLYING);
                    case ExpertApplying.PASS:
                        return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_ALREADY_EXPERT);
                    default:
                        return CommonResult.oneResultCommonResult(COMMON_RESULT_APPLYING_EXPERT_STATE, APPLYING_EXPERT_STATE_NO_APPLY);
                }
            }
        }
    }

    @Path("user_info.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "获取用户资料信息,如果不是本人访问,则不带上:" +
                    "real_name,email",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiUser.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    public ApiUser getUserInfo(
            @QueryParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId,
            @HeaderParam(HEADER_USER_ID)
            Long fromUserId
    ) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        ApiUser apiUser = new ApiUser(userInfo);
        if (fromUserId == null || userId != fromUserId) {
            apiUser.setRealName(null);
        }

        if (StringUtils.isNotBlank(apiUser.getPortrait())) {
            apiUser.setPortrait(StaticFileUtil.getPortraitUrl(userInfo.getPortrait()));
        }

        return apiUser;
    }

    @Path("selected.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "首页列表内容",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiUser.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    public List<CommonResult> getSelected(
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count,
            @HeaderParam(HEADER_USER_ID)
            Long fromUserId
    ) {
        return null;
    }

    @Path("user_base_info.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "只有用户名、头像、性别信息",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiUser.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    public ApiUser getUserBaseInfo(
            @QueryParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId
    ) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        ApiUser apiUser = new ApiUser(userInfo.getUserId(), userInfo.getUserType().getIndex());

        apiUser.setNickName(userInfo.getNickName());

        if (StringUtils.isNotBlank(apiUser.getPortrait())) {
            apiUser.setPortrait(StaticFileUtil.getPortraitUrl(userInfo.getPortrait()));
        }

        apiUser.setGender(userInfo.getGender());

        return apiUser;
    }

    @Path("user_counter.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(
            desc = "获取用户相关的计数",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiUserCounter.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    public ApiUserCounter getUserCounter(
            @QueryParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId,
            @QueryParam("counter_types")
            @ParamSpec(isRequired = true, spec = "string:1~100", desc = "types,用逗号分开{1,2,3,4}->{提问数,回答数,获得赞的个数,收藏答案数}")
            String counterTypes
    ) {
        checkAndGetUserInfo(userId);
        int[] types = StringUtil.splitIntCommaString(counterTypes);
        List<KnowledgeCounterType> counterTypeList = Lists.newArrayList();
        for (int type : types) {
            KnowledgeCounterType counterType = KnowledgeCounterType.valueByIndex(type);
            if (counterType != null) {
                counterTypeList.add(counterType);
            }
        }

        Map<KnowledgeCounterType, Integer> map = knowledgeService.getUserCounter(counterTypeList, userId);
        ApiUserCounter apiUserCounter = new ApiUserCounter(map);
        return apiUserCounter;
    }

    @Path("user_follow_tag.json")
    @POST
    @RestApiInfo(
            desc = "用户关注或者取消关注标签",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.ALREADY_EXPERT}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult userFollowTag(
            @FormParam("tag_id")
            @ParamSpec(isRequired = true, spec = "long:[1000,2000]", desc = "标签id")
            long tagId,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否关注: {0,1}->{not follow, follow}")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        if (!knowledgeService.isValidTags(tagId)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_TAG_ID);
        }

        return CommonResult.isSuccessfulCommonResult(accountService.followTag(userId, tagId, state));
    }

    @Path("user_followed_tag.json")
    @POST
    @RestApiInfo(
            desc = "用户关注的标签列表",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestionTag.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiQuestionTag> getUserFollowTags(
            @QueryParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId) {
        UserInfo userInfo = checkAndGetUserInfo(userId);
        List<Long> tagIds = accountService.listFollowTags(userInfo.getUserId());
        List<QuestionTag> questionTagList = knowledgeService.listQuestionTag(tagIds);
        return ApiQuestionTag.from(questionTagList);
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

    private Account checkAndGetAccount(String openId, PlatformType platformType) {
        OpenPlatformAccount openPlatformAccount = accountService.getOpenPlatformAccount(openId, platformType);
        if (openPlatformAccount == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ID_OR_PASSWORD);
        }

        Account account = accountService.getAccount(openPlatformAccount.getUserId());
        if (account == null) {
            VirgoLogger.error("no user id for open platform account: {}", openId);
            throw new VirgoRestException(RestExceptionFactor.INVALID_ID_OR_PASSWORD);
        }

        return account;
    }

    private UserInfo checkAndGetUserInfo(long userId) {
        UserInfo userInfo = accountService.getUserInfo(userId);
        if (userInfo == null) {
            throw new VirgoRestException(RestExceptionFactor.USER_ID_NOT_EXISTED);
        }

        return userInfo;
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

    private String checkAndPurifyNickName(String newNickName, String originalNickName) {
        newNickName = StringUtils.trim(newNickName);
        originalNickName = StringUtils.trim(newNickName);
        if (StringUtils.length(newNickName) < 2) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_NICK_NAME);
        }

        Matcher matcher = pattern.matcher(newNickName);
        Matcher allNumberMatcher = allNumberPattern.matcher(newNickName);

        if (matcher.matches() && !allNumberMatcher.matches()) {
            if (!StringUtils.equals(newNickName, originalNickName) && accountService.isNickNameExisted(newNickName)) {
                throw new VirgoRestException(RestExceptionFactor.NICK_NAME_EXISTED);
            } else {
                return newNickName;
            }
        } else {
            throw new VirgoRestException(RestExceptionFactor.INVALID_NICK_NAME);
        }

    }

    private ApiQuestionTag[] getApiQuestionTags(long[] tagIds) {
        QuestionTag[] tags = knowledgeService.listQuestionTag(tagIds);
        ApiQuestionTag[] apiQuestionTags = new ApiQuestionTag[tags.length];

        int i = 0;
        for (QuestionTag tag : tags) {
            apiQuestionTags[i++] = new ApiQuestionTag(tag);
        }

        return apiQuestionTags;
    }

    private long[] checkAndGetTagIds(String tagsString) {
        long[] tagIds = StringUtil.splitLongCommaString(tagsString);
        tagIds = ArrayUtil.deduplicate(tagIds);
        if (ArrayUtils.isEmpty(tagIds) || tagIds.length > MAX_TAG_NUMBER || !knowledgeService.isValidTags(tagIds)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_TAG_ID);
        }

        return tagIds;
    }
}
