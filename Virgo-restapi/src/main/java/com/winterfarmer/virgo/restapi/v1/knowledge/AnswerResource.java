package com.winterfarmer.virgo.restapi.v1.knowledge;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.UserInfo;
import com.winterfarmer.virgo.account.model.UserType;
import com.winterfarmer.virgo.aggregator.model.ApiAnswer;
import com.winterfarmer.virgo.aggregator.model.ApiQuestion;
import com.winterfarmer.virgo.aggregator.model.ApiQuestionTag;
import com.winterfarmer.virgo.aggregator.model.ApiUser;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15/5/17.
 */
@Path("answer")
@ResourceOverview(desc = "答案接口")
@Component("answerResource")
public class AnswerResource extends KnowledgeResource {
    protected static final String ANSWER_CONTENT_SPEC = "UnicodeString:1~65536";

    private static final Function<Answer, ApiAnswer> apiAnswerListConverter =
            new Function<Answer, ApiAnswer>() {
                @Override
                public ApiAnswer apply(Answer answer) {
                    return ApiAnswer.forSimpleDisplay(answer);
                }
            };

    @Path("new_answer.json")
    @POST
    @RestApiInfo(
            desc = "新的回答",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiAnswer.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED,
                    RestExceptionFactor.CANNOT_DO_THIS_TO_QUESTION}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiAnswer createAnswer(
            @FormParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = ANSWER_CONTENT_SPEC, desc = "回答的内容")
            String content,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        Question question = checkAndGetQuestion(questionId);
        if (question.getUserId() == userId) {
            throw new VirgoRestException(RestExceptionFactor.CANNOT_DO_THIS_TO_QUESTION);
        }

        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");
        Answer answer = knowledgeService.newAnswer(userId, questionId, content, imageIds);
        return addCount(new ApiAnswer(answer));
    }

    @Path("update_answer.json")
    @POST
    @RestApiInfo(
            desc = "新的回答",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiAnswer.class,
            errors = {RestExceptionFactor.NO_RIGHTS,
                    RestExceptionFactor.ANSWER_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiAnswer updateAnswer(
            @FormParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = ANSWER_CONTENT_SPEC, desc = "回答的内容")
            String content,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        Answer answer = checkAndGetAnswer(answerId);
        if (answer.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }

        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");
        answer.setContent(content);
        answer.setImageIds(imageIds);
        answer.setUpdateAtMs(System.currentTimeMillis());
        answer = knowledgeService.updateAnswer(answer);
        return addCount(new ApiAnswer(answer));
    }

    @Path("delete_answer.json")
    @POST
    @RestApiInfo(
            desc = "删除回答",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.ANSWER_NOT_EXISTED,
                    RestExceptionFactor.NO_RIGHTS}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult deleteAnswer(
            @FormParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Answer answer = checkAndGetAnswer(answerId);
        if (answer.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }

        knowledgeService.updateAnswerState(answer, CommonState.DELETE);
        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("agree_answer.json")
    @POST
    @RestApiInfo(
            desc = "认为回答有价值",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult agreeAnswer(
            @FormParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否agree: 0-not agree, 1-agree")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Answer answer = checkAndGetAnswer(answerId);
        if (answer.getUserId() == userId) {
            throw new VirgoRestException(RestExceptionFactor.CANNOT_DO_THIS_TO_ANSWER);
        }
        boolean result =
                state == CommonState.NORMAL ?
                        knowledgeService.agreeAnswer(userId, answer.getId()) :
                        knowledgeService.disagreeAnswer(userId, answer.getId());
        return CommonResult.isSuccessfulCommonResult(result);
    }

    @Path("collect_answer.json")
    @POST
    @RestApiInfo(
            desc = "收藏答案",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult followAnswer(
            @FormParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否收藏: 0-not collect, 1-collect")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Answer answer = checkAndGetAnswer(answerId);
        boolean result =
                state == CommonState.NORMAL ?
                        knowledgeService.collectAnswer(userId, answer.getId()) :
                        knowledgeService.discollectAnswer(userId, answer.getId());
        return CommonResult.isSuccessfulCommonResult(result);
    }

    @Path("user_answers.json")
    @GET
    @RestApiInfo(
            desc = "用户回答或者收藏的问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiAnswer> listUserAnswers(
            @QueryParam("type")
            @ParamSpec(isRequired = true, spec = "int:[0,2]", desc = "筛选类型: 0-我回答的, 1-我收藏的, 2-最新的回答我的")
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
            long userId) {
        List<Answer> answerList = queryAnswers(type, userId, page, count);
        List<ApiAnswer> apiAnswerList = Lists.transform(answerList, apiAnswerListConverter);
        return addCountInfo(addUserInfo(addQuestionSubject(apiAnswerList)));
    }

    @Path("question_answers.json")
    @GET
    @RestApiInfo(
            desc = "list问题的答案",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiAnswer> listQuestionAnswers(
            @QueryParam(QUESTION_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = QUESTION_ID_DESC)
            long questionId,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        List<Answer> answerList = knowledgeService.listQuestionAnswers(questionId, page, count);
        List<ApiAnswer> apiAnswerList = Lists.transform(answerList, apiAnswerListConverter);
        return addCountInfo(addUserInfo(addQuestionSubject(apiAnswerList)));
    }

    @Path("answers.json")
    @GET
    @RestApiInfo(
            desc = "list所有答案",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiAnswer> listAnswers(
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        List<Answer> answerList = knowledgeService.listAnswers(page, count);
        List<ApiAnswer> apiAnswerList = Lists.transform(answerList, apiAnswerListConverter);
        return addCountInfo(addUserInfo(addQuestionSubject(apiAnswerList)));
    }

    @Path("answer_detail.json")
    @GET
    @RestApiInfo(
            desc = "回答的详情",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiQuestion.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiAnswer getAnswer(
            @QueryParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId) {
        Answer answer = checkAndGetAnswer(answerId);
        Question question = knowledgeService.getQuestion(answerId);

        ApiAnswer apiAnswer = new ApiAnswer(answer);
        apiAnswer.setQuestionSubject(question.getSubject());

        return addCount(new ApiAnswer(answer));
    }

    /**
     * @param type   0:我回答的;1:我收藏的
     * @param userId
     * @param page
     * @param count
     * @return
     */
    private List<Answer> queryAnswers(int type, long userId, int page, int count) {
        switch (type) {
            case 0:
                return knowledgeService.listUserAnswers(userId, page, count);
            case 1:
                return knowledgeService.listUserCollectedAnswers(userId, page, count);
            case 2:
                return knowledgeService.listUserQuestionAnswers(userId, page, count);
            default:
                throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM);
        }
    }

    private Pair<String, List<String>> checkAndGetContentAndImageIds(String content) {
        Pair<String, List<String>> refinedContentAndImageIds = knowledgeService.refineAnswerContent(content);
        if (refinedContentAndImageIds == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ANSWER_CONTENT);
        }

        return refinedContentAndImageIds;
    }

    private List<ApiAnswer> addUserInfo(List<ApiAnswer> apiAnswerList) {
        Map<Long, ApiUser> userMap = Maps.newHashMap();
        List<ApiAnswer> answerList = Lists.newArrayList();

        for (ApiAnswer apiAnswer : apiAnswerList) {
            long userId = apiAnswer.getUserId();
            if (!userMap.containsKey(userId)) {
                UserInfo userInfo = accountService.getUserInfo(userId);
                ApiUser apiUser = ApiUser.simpleUser(userInfo);
                if (userInfo.getUserType() == UserType.EXPERT) {
                    List<Long> tagIdList = accountService.getExpertTags(userInfo.getUserId());
                    List<QuestionTag> questionTagList = knowledgeService.listQuestionTag(tagIdList);
                    apiUser.setExpertTags(ApiQuestionTag.from(questionTagList));
                }
                userMap.put(userId, apiUser);
            }

            apiAnswer.setUser(userMap.get(userId));
            answerList.add(apiAnswer);
        }

        return answerList;
    }

    private List<ApiAnswer> addQuestionSubject(List<ApiAnswer> apiAnswerList) {
        Map<Long, Question> questionMap = Maps.newHashMap();
        List<ApiAnswer> newApiAnswerList = Lists.newArrayList();
        for (ApiAnswer apiAnswer : apiAnswerList) {
            if (!questionMap.containsKey(apiAnswer.getQuestionId())) {
                Question question = knowledgeService.getQuestion(apiAnswer.getQuestionId());
                if (question == null) {
                    VirgoLogger.error("question : " + apiAnswer.getQuestionId() + " not existed!!");
                }
                questionMap.put(apiAnswer.getQuestionId(), question);
            }
            Question question = questionMap.get(apiAnswer.getQuestionId());
            if (question != null) {
                apiAnswer.setQuestionSubject(question.getSubject());
                newApiAnswerList.add(apiAnswer);
            }
        }

        return newApiAnswerList;
    }

    private List<ApiAnswer> addCountInfo(List<ApiAnswer> apiAnswerList) {
        Map<Long, Integer> collectCountMap = Maps.newHashMap();
        Map<Long, Integer> commentCountMap = Maps.newHashMap();
        Map<Long, Integer> agreeCountMap = Maps.newHashMap();

        List<ApiAnswer> newApiAnswerList = Lists.newArrayList();
        for (ApiAnswer apiAnswer : apiAnswerList) {
            long answerId = apiAnswer.getAnswerId();
            if (!collectCountMap.containsKey(answerId)) {
                collectCountMap.put(answerId, knowledgeService.getAnswerCollectCount(answerId));
            }
            if (!commentCountMap.containsKey(answerId)) {
                commentCountMap.put(answerId, knowledgeService.getAnswerCommentCount(answerId));
            }
            if (!agreeCountMap.containsKey(answerId)) {
                agreeCountMap.put(answerId, knowledgeService.getAnswerAgreeCount(answerId));
            }

            apiAnswer.setCollectCount(collectCountMap.get(answerId));
            apiAnswer.setCommentCount(commentCountMap.get(answerId));
            apiAnswer.setAgreeCount(agreeCountMap.get(answerId));

            newApiAnswerList.add(apiAnswer);
        }

        return newApiAnswerList;
    }

    private ApiAnswer addCount(ApiAnswer apiAnswer) {
        long answerId = apiAnswer.getAnswerId();

        apiAnswer.setCollectCount(knowledgeService.getAnswerCollectCount(answerId));
        apiAnswer.setCommentCount(knowledgeService.getAnswerCommentCount(answerId));
        apiAnswer.setAgreeCount(knowledgeService.getAnswerAgreeCount(answerId));

        return apiAnswer;
    }
}
