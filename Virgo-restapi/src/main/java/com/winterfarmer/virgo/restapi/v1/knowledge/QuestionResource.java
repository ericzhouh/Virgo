package com.winterfarmer.virgo.restapi.v1.knowledge;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.UserInfo;
import com.winterfarmer.virgo.aggregator.model.ApiQuestion;
import com.winterfarmer.virgo.aggregator.model.ApiQuestionTag;
import com.winterfarmer.virgo.aggregator.model.ApiUser;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.common.util.StringUtil;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15/5/15.
 */
@Path("question")
@ResourceOverview(desc = "问题接口")
@Component("questionResource")
public class QuestionResource extends KnowledgeResource {
    protected static final String SUBJECT_SPEC = "UnicodeString:4~64";
    protected static final String QUESTION_CONTENT_SPEC = "UnicodeString:1~65536";

    public static final int MAX_TAG_NUMBER = 3;
    private final Function<Question, ApiQuestion> apiQuestionListConverter =
            new Function<Question, ApiQuestion>() {
                @Override
                public ApiQuestion apply(Question question) {
                    ApiQuestion apiQuestion = ApiQuestion.forSimpleDisplay(question);
                    List<Long> tagIds = knowledgeService.listQuestionTagIdsByQuestionId(question.getId());
                    List<QuestionTag> questionTagList = knowledgeService.listQuestionTag(tagIds);
                    apiQuestion.setTags(ApiQuestionTag.from(questionTagList));
                    return apiQuestion;
                }
            };


    @Path("new_question.json")
    @POST
    @RestApiInfo(
            desc = "新的问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.INVALID_QUESTION_CONTENT
            }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion createQuestion(
            @FormParam("subject")
            @ParamSpec(isRequired = true, spec = SUBJECT_SPEC, desc = "题目")
            String subject,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = QUESTION_CONTENT_SPEC, desc = "内容")
            @DefaultValue("")
            String content,
            @FormParam("question_tags")
            @ParamSpec(isRequired = true, spec = "string:1~1000", desc = "tag id, 用逗号分开")
            String tagsString,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        long[] tagIds = checkAndGetTagIds(tagsString);
        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");
        Question question = knowledgeService.newQuestion(userId, subject, refinedContentAndImageIds.getLeft(), imageIds, tagIds);
        ApiQuestionTag[] apiQuestionTags = getApiQuestionTags(tagIds);
        ApiQuestion apiQuestion = addCountInfo(new ApiQuestion(question, apiQuestionTags));
        apiQuestion.setIsAgreed(false);
        apiQuestion.setIsFollowed(false);
        return apiQuestion;
    }

    @Path("update_question.json")
    @POST
    @RestApiInfo(
            desc = "更新问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.INVALID_QUESTION_CONTENT,
                    RestExceptionFactor.QUESTION_NOT_EXISTED
            }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion updateQuestion(
            @FormParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @FormParam("subject")
            @ParamSpec(isRequired = true, spec = SUBJECT_SPEC, desc = "题目")
            String subject,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = QUESTION_CONTENT_SPEC, desc = "内容")
            String content,
            @FormParam("question_tags")
            @ParamSpec(isRequired = true, spec = "string:1~1000", desc = "tag id, 用逗号分开")
            String tagsString,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        long[] tagIds = checkAndGetTagIds(tagsString);
        Question question = checkAndGetQuestion(questionId);
        if (question.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }
        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");

        question.setSubject(subject);
        question.setImageIds(imageIds);
        question.setContent(refinedContentAndImageIds.getLeft());
        question.setUpdateAtMs(System.currentTimeMillis());

        question = knowledgeService.updateQuestion(question, tagIds);
        ApiQuestionTag[] apiQuestionTags = getApiQuestionTags(tagIds);
        return addUserOperations(userId, addCountInfo(new ApiQuestion(question, apiQuestionTags)));
    }

    @Path("delete_question.json")
    @POST
    @RestApiInfo(
            desc = "删除问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED,
                    RestExceptionFactor.NO_RIGHTS}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult deleteQuestion(
            @FormParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        if (question.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }

        knowledgeService.updateQuestionState(question, CommonState.DELETE);
        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("agree_question.json")
    @POST
    @RestApiInfo(
            desc = "认为问题有价值",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED,
                    RestExceptionFactor.CANNOT_DO_THIS_TO_QUESTION}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult agreeQuestion(
            @FormParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否agree: 0-not agree, 1-agree")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        if (state == CommonState.NORMAL) {
            knowledgeService.agreeQuestion(userId, question.getId());
        } else {
            knowledgeService.disagreeQuestion(userId, question.getId());
        }

        int count = knowledgeService.getQuestionAgreeCount(questionId);
        boolean isAgreed = knowledgeService.isUserAgreeQuestion(userId, questionId);

        return CommonResult.newCommonResult(
                "agree_count", count,
                "is_agreed", isAgreed);
    }

    @Path("follow_question.json")
    @POST
    @RestApiInfo(
            desc = "关注问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult followQuestion(
            @FormParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否follow: 0-not follow, 1-follow")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        if (state == CommonState.NORMAL) {
            knowledgeService.followQuestion(userId, question.getId());
        } else {
            knowledgeService.disfollowQuestion(userId, question.getId());
        }
        int count = knowledgeService.getQuestionFollowCount(questionId);
        boolean isFollowed = knowledgeService.isUserFollowQuestion(userId, questionId);

        return CommonResult.newCommonResult(
                "follow_count", count,
                "is_followed", isFollowed
        );
    }

    @Path("user_questions.json")
    @GET
    @RestApiInfo(
            desc = "用户提问, 回答或者关注的问题",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiQuestion> listUserQuestions(
            @QueryParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId,
            @QueryParam("type")
            @ParamSpec(isRequired = true, spec = "int:[0,2]", desc = "筛选类型: 0-我提问的, 1-我回答的, 2-我关注的")
            int type,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count,
            @HeaderParam(HEADER_USER_ID)
            Long fromUserId) {
        List<Question> questionList = queryQuestions(type, userId, page, count);
        List<ApiQuestion> apiQuestionList = Lists.transform(questionList, apiQuestionListConverter);
        return addUserOperations(fromUserId, addCountInfo(apiQuestionList));
    }

    @Path("list_questions.json")
    @GET
    @RestApiInfo(
            desc = "列出问题",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.INVALID_TAG_ID}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiQuestion> listQuestions(
            @QueryParam("tag_id")
            @ParamSpec(isRequired = false, spec = NATURAL_LONG_ID_SPEC, desc = "标签id: 0-所有(无视此参数), 其他-tag id")
            @DefaultValue("0")
            long tagId,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count,
            @HeaderParam(HEADER_USER_ID)
            Long userId) {
        List<Question> questionList = tagId == 0 ?
                knowledgeService.listQuestions(page, count) :
                knowledgeService.listQuestions(tagId, page, count);
        List<ApiQuestion> apiQuestionList = Lists.transform(questionList, apiQuestionListConverter);
        if (userId != null) {
            apiQuestionList = addUserOperations(userId, apiQuestionList);
        }
        return addCountInfo(addUserInfo(apiQuestionList));
    }

    @Path("question_detail.json")
    @GET
    @RestApiInfo(
            desc = "问题详情",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion getQuestion(
            @QueryParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @HeaderParam(HEADER_USER_ID)
            Long userId) {
        Question question = checkAndGetQuestion(questionId);
        List<Long> tagIdList = knowledgeService.listQuestionTagIdsByQuestionId(questionId);
        List<ApiQuestionTag> tagList = getApiQuestionTags(tagIdList);

        ApiQuestion apiQuestion = new ApiQuestion(question, tagList);
        UserInfo userInfo = accountService.getUserInfo(question.getUserId());

        VirgoLogger.debug("test " + userInfo.getGender());
        ApiUser apiUser = ApiUser.simpleUser(userInfo);
        apiQuestion.setUser(apiUser);
        if (userId != null) {
            apiQuestion = addUserOperations(userId, apiQuestion);
        }

        return addCountInfo(apiQuestion);
    }

    private List<ApiQuestionTag> apiQuestionTagList;

    @Path("list_tags.json")
    @GET
    @RestApiInfo(
            desc = "列出标签",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestionTag.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiQuestionTag> listQuestionTags() {
        // TODO: lazy load, 可能加载多次，不过没什么问题
        if (apiQuestionTagList == null) {
            apiQuestionTagList = Lists.transform(knowledgeService.listQuestionTag(), new Function<QuestionTag, ApiQuestionTag>() {
                @Override
                public ApiQuestionTag apply(QuestionTag questionTag) {
                    return new ApiQuestionTag(questionTag);
                }
            });
        }

        return apiQuestionTagList;
    }

    @Path("search.json")
    @GET
    @RestApiInfo(
            desc = "搜索",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            cautions = "目前只能从数据库里简单搜索题目,需要有专门的服务做搜索的事情",
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiQuestion> search(
            @QueryParam("key_words")
            @ParamSpec(isRequired = true, spec = "UnicodeString:2~64", desc = "搜查关键字")
            String keywords,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        List<ApiQuestion> apiQuestionList = Lists.transform(knowledgeService.searchQuestion(keywords, page, count), apiQuestionListConverter);
        return addCountInfo(addUserInfo(apiQuestionList));
    }

    /**
     * @param type   0:我提问的;1:我回答的;2:我关注的
     * @param userId
     * @param page
     * @param count
     * @return
     */
    private List<Question> queryQuestions(int type, long userId, int page, int count) {
        switch (type) {
            case 0:
                return knowledgeService.listUserProposedQuestions(userId, page, count);
            case 1:
                return knowledgeService.listUserAnsweredQuestions(userId, page, count);
            case 2:
                return knowledgeService.listUserFollowedQuestions(userId, page, count);
            default:
                throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM);
        }
    }

    private long[] checkAndGetTagIds(String tagsString) {
        long[] tagIds = StringUtil.splitLongCommaString(tagsString);
        tagIds = ArrayUtil.deduplicate(tagIds);
        if (ArrayUtils.isEmpty(tagIds) || tagIds.length > MAX_TAG_NUMBER || !knowledgeService.isValidTags(tagIds)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_TAG_ID);
        }

        return tagIds;
    }

    private Pair<String, List<String>> checkAndGetContentAndImageIds(String content) {
        Pair<String, List<String>> refinedContentAndImageIds = knowledgeService.refineQuestionContent(content);
        if (refinedContentAndImageIds == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_QUESTION_CONTENT);
        }

        return refinedContentAndImageIds;
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

    private List<ApiQuestionTag> getApiQuestionTags(List<Long> tagIdList) {
        ApiQuestionTag[] apiQuestionTags = getApiQuestionTags(ArrayUtil.toLongArray(tagIdList));
        return Lists.newArrayList(apiQuestionTags);
    }

    private List<ApiQuestion> addUserInfo(List<ApiQuestion> apiQuestionList) {
        Map<Long, ApiUser> userMap = Maps.newHashMap();
        List<ApiQuestion> questionList = Lists.newArrayList();
        for (ApiQuestion apiQuestion : apiQuestionList) {
            long userId = apiQuestion.getUserId();
            if (!userMap.containsKey(userId)) {
                UserInfo userInfo = accountService.getUserInfo(userId);
                ApiUser apiUser = ApiUser.simpleUser(userInfo);
                userMap.put(userId, apiUser);
            }

            apiQuestion.setUser(userMap.get(userId));
            questionList.add(apiQuestion);
        }

        return questionList;
    }

    private List<ApiQuestion> addCountInfo(List<ApiQuestion> apiQuestionList) {
        Map<Long, Integer> followCountMap = Maps.newHashMap();
        Map<Long, Integer> answerCountMap = Maps.newHashMap();
        Map<Long, Integer> agreeCountMap = Maps.newHashMap();

        List<ApiQuestion> newApiQuestionList = Lists.newArrayList();

        for (ApiQuestion apiQuestion : apiQuestionList) {
            long questionId = apiQuestion.getQuestionId();
            if (!followCountMap.containsKey(questionId)) {
                followCountMap.put(questionId, knowledgeService.getQuestionFollowCount(questionId));
            }
            if (!answerCountMap.containsKey(questionId)) {
                answerCountMap.put(questionId, knowledgeService.getQuestionAnswerCount(questionId));
            }
            if (!agreeCountMap.containsKey(questionId)) {
                agreeCountMap.put(questionId, knowledgeService.getQuestionAgreeCount(questionId));
            }

            apiQuestion.setAgreeCount(agreeCountMap.get(questionId));
            apiQuestion.setAnswerCount(answerCountMap.get(questionId));
            apiQuestion.setFollowCount(followCountMap.get(questionId));
            newApiQuestionList.add(apiQuestion);
        }

        return newApiQuestionList;
    }

    private ApiQuestion addCountInfo(ApiQuestion apiQuestion) {
        long questionId = apiQuestion.getQuestionId();

        apiQuestion.setAgreeCount(knowledgeService.getQuestionAgreeCount(questionId));
        apiQuestion.setAnswerCount(knowledgeService.getQuestionAnswerCount(questionId));
        apiQuestion.setFollowCount(knowledgeService.getQuestionFollowCount(questionId));

        return apiQuestion;
    }

    private List<ApiQuestion> addUserOperations(Long userId, List<ApiQuestion> apiQuestions) {
        if (userId == null || userId < 1) {
            return apiQuestions;
        }

        List<ApiQuestion> newApiQuestionList = Lists.newArrayList();

        for (ApiQuestion apiQuestion : apiQuestions) {
            newApiQuestionList.add(addUserOperations(userId, apiQuestion));
        }

        return newApiQuestionList;
    }

    private ApiQuestion addUserOperations(Long userId, ApiQuestion apiQuestion) {
        if (userId == null || userId < 1) {
            return apiQuestion;
        }

        long questionId = apiQuestion.getQuestionId();
        apiQuestion.setIsAgreed(knowledgeService.isUserAgreeQuestion(userId, questionId));
        apiQuestion.setIsFollowed(knowledgeService.isUserFollowQuestion(userId, questionId));
        return apiQuestion;
    }
}
