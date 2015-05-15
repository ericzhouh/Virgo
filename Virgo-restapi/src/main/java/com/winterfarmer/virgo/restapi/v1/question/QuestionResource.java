package com.winterfarmer.virgo.restapi.v1.question;

import com.winterfarmer.virgo.aggregator.model.ApiQuestion;
import com.winterfarmer.virgo.aggregator.model.ApiQuestionTag;
import com.winterfarmer.virgo.aggregator.model.ApiVehicle;
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
            errors = {}
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

    @Path("questions.json")
    @GET
    @RestApiInfo(
            desc = "获取用户车辆信息",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiVehicle.class,
            errors = {RestExceptionFactor.VEHICLE_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiQuestion getUserVehicle(
            @QueryParam("vehicle_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "用户汽车id")
            long vehicleId,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        throw new VirgoRestException(RestExceptionFactor.VEHICLE_NOT_EXISTED);
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
            throw new VirgoRestException(RestExceptionFactor.INVALID_TAG_ID);
        }

        return refinedContentAndImageIds;
    }

    private static ApiQuestionTag[] getApiQuestionTags(long[] tagIds) {
        return null;
    }
}
