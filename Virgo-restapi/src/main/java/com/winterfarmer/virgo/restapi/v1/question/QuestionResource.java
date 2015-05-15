package com.winterfarmer.virgo.restapi.v1.question;

import com.winterfarmer.virgo.aggregator.model.ApiQuestion;
import com.winterfarmer.virgo.aggregator.model.ApiQuestionTag;
import com.winterfarmer.virgo.aggregator.model.ApiVehicle;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.common.util.StringUtil;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.service.KnowledgeService;
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
import java.util.List;

/**
 * Created by yangtianhang on 15/5/15.
 */
@Path("question")
@ResourceOverview(desc = "问题接口")
@Component("questionResource")
public class QuestionResource extends BaseResource {
    protected static final String SUBJECT_SPEC = "UnicodeString:4~64";
    protected static final String QUESTION_CONTENT_SPEC = "UnicodeString:1~10240";

    public static final int MAX_TAG_NUMBER = 3;

    @Resource(name = "knowledgeService")
    KnowledgeService knowledgeService;

    @Path("new_question.json")
    @POST
    @RestApiInfo(
            desc = "新的问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.INVALID_QUESTION_CONTENT_ID
            }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion createQuestion(
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
            long userId) {
        long[] tagIds = checkAndGetTagIds(tagsString);
        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");
        Question question = knowledgeService.newQuestion(userId, subject, refinedContentAndImageIds.getLeft(), imageIds, tagIds);
        ApiQuestionTag[] apiQuestionTags = getApiQuestionTags(tagIds);
        return new ApiQuestion(question, apiQuestionTags);
    }

    @Path("update_question.json")
    @POST
    @RestApiInfo(
            desc = "更新问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.INVALID_TAG_ID,
                    RestExceptionFactor.INVALID_QUESTION_CONTENT_ID,
                    RestExceptionFactor.QUESTION_NOT_EXISTED
            }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion updateQuestion(
            @FormParam("question_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "问题id")
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
        return new ApiQuestion(question, apiQuestionTags);
    }

    @Path("delete_question.json")
    @POST
    @RestApiInfo(
            desc = "更新问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult deleteQuestion(
            @FormParam("question_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "问题id")
            long questionId,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        if (question.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }

        knowledgeService.updateQuestionState(question.getId(), CommonState.DELETE);
        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("agree_question.json")
    @POST
    @RestApiInfo(
            desc = "认为问题有价值",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult agreeQuestion(
            @FormParam("question_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "问题id")
            long questionId,
            @FormParam("is_agree")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否agree: 0-not agree, 1-agree")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        boolean result =
                state == CommonState.NORMAL ?
                        knowledgeService.agreeQuestion(userId, question.getId()) :
                        knowledgeService.disagreeQuestion(userId, question.getId());
        return CommonResult.isSuccessfulCommonResult(result);
    }

    @Path("follow_question.json")
    @POST
    @RestApiInfo(
            desc = "关注问题",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult followQuestion(
            @FormParam("question_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "问题id")
            long questionId,
            @FormParam("is_follow")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "是否follow: 0-not follow, 1-follow")
            CommonState state,
            @HeaderParam(HEADER_USER_ID)
            long userId
    ) {
        Question question = checkAndGetQuestion(questionId);
        boolean result =
                state == CommonState.NORMAL ?
                        knowledgeService.followQuestion(userId, question.getId()) :
                        knowledgeService.disagreeQuestion(userId, question.getId());
        return CommonResult.isSuccessfulCommonResult(result);
    }

    @Path("question.json")
    @GET
    @RestApiInfo(
            desc = "查看问题详情",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion getUserVehicle(
            @QueryParam("question_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "问题id")
            long questionId) {
        Question question = checkAndGetQuestion(questionId);
        List<Long> tagIdList = knowledgeService.listQuestionTagIdsByQuestionId(question.getId());
        List<ApiQuestionTag> apiQuestionTagList = getApiQuestionTags(tagIdList);
        return new ApiQuestion(question, apiQuestionTagList);
    }

    private long[] checkAndGetTagIds(String tagsString) {
        long[] tagIds = StringUtil.splitLongCommaString(tagsString);
        tagIds = ArrayUtil.deduplicate(tagIds);
        if (ArrayUtils.isEmpty(tagIds) || tagIds.length > MAX_TAG_NUMBER || knowledgeService.isValidTags(tagIds)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_TAG_ID);
        }

        return tagIds;
    }

    private Pair<String, List<String>> checkAndGetContentAndImageIds(String content) {
        Pair<String, List<String>> refinedContentAndImageIds = knowledgeService.refineQuestionContent(content);
        if (refinedContentAndImageIds == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_QUESTION_CONTENT_ID);
        }

        return refinedContentAndImageIds;
    }

    private static ApiQuestionTag[] getApiQuestionTags(long[] tagIds) {
        return null;
    }

    private static List<ApiQuestionTag> getApiQuestionTags(List<Long> tagIdList) {
        return null;
    }

    private Question checkAndGetQuestion(long questionId) {
        Question question = knowledgeService.getQuestion(questionId);
        if (question == null || question.getCommonState() == CommonState.DELETE) {
            throw new VirgoRestException(RestExceptionFactor.QUESTION_NOT_EXISTED);
        }

        return question;
    }
}
