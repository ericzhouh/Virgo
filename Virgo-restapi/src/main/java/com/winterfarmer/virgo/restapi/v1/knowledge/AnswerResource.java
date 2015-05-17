package com.winterfarmer.virgo.restapi.v1.knowledge;

import com.winterfarmer.virgo.aggregator.model.ApiAnswer;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.Question;
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

/**
 * Created by yangtianhang on 15/5/17.
 */
@Path("answer")
@ResourceOverview(desc = "答案接口")
@Component("answerResource")
public class AnswerResource extends KnowledgeResource {
    protected static final String ANSWER_CONTENT_SPEC = "UnicodeString:1~65536";

    @Path("new_answer.json")
    @POST
    @RestApiInfo(
            desc = "新的回答",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiAnswer.class,
            errors = {RestExceptionFactor.QUESTION_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiAnswer createAnswer(
            @FormParam("question_id")
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = "问题id")
            long questionId,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = ANSWER_CONTENT_SPEC, desc = "回答的内容")
            String content,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        checkAndGetQuestion(questionId);
        Pair<String, List<String>> refinedContentAndImageIds = checkAndGetContentAndImageIds(content);
        String imageIds = StringUtils.join(refinedContentAndImageIds.getRight(), ",");
        Answer answer = knowledgeService.newAnswer(userId, questionId, content, imageIds);
        return new ApiAnswer(answer);
    }

    private Pair<String, List<String>> checkAndGetContentAndImageIds(String content) {
        Pair<String, List<String>> refinedContentAndImageIds = knowledgeService.refineAnswerContent(content);
        if (refinedContentAndImageIds == null) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ANSWER_CONTENT);
        }

        return refinedContentAndImageIds;
    }
}
