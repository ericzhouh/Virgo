package com.winterfarmer.virgo.restapi.v1.knowledge;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.aggregator.model.ApiAnswer;
import com.winterfarmer.virgo.aggregator.model.ApiAnswerComment;
import com.winterfarmer.virgo.aggregator.model.ApiQuestion;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/29.
 */
@Path("answer_comment")
@ResourceOverview(desc = "答案的评论接口")
@Component("answerCommentResource")
public class AnswerCommentResource extends KnowledgeResource {
    protected static final String ANSWER_COMMENT_CONTENT_SPEC = "UnicodeString:1~1024";

    @Path("new_comment.json")
    @POST
    @RestApiInfo(
            desc = "新的评论",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiAnswer.class,
            errors = {RestExceptionFactor.ANSWER_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiAnswerComment createAnswerComment(
            @FormParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @FormParam("to_user_id")
            @ParamSpec(isRequired = false, spec = USER_ID_SPEC, desc = "被答复的用户id")
            @DefaultValue("0")
            long toUserId,
            @FormParam("content")
            @ParamSpec(isRequired = true, spec = ANSWER_COMMENT_CONTENT_SPEC, desc = "评论的内容")
            String content,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        checkAndGetAnswer(answerId);
        AnswerComment answerComment = knowledgeService.newAnswerComment(userId, toUserId, answerId, content);
        return new ApiAnswerComment(answerComment);
    }

    @Path("delete_comment.json")
    @POST
    @RestApiInfo(
            desc = "删除评论",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiAnswer.class,
            errors = {RestExceptionFactor.ANSWER_COMMENT_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult deleteAnswerComment(
            @FormParam("answer_comment_id")
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerCommentId,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        AnswerComment answerComment = knowledgeService.getAnswerComment(answerCommentId);
        if (answerComment.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.ANSWER_COMMENT_NOT_EXISTED);
        }

        knowledgeService.updateAnswerCommentState(answerComment, CommonState.DELETE);
        return CommonResult.isSuccessfulCommonResult(true);
    }

    @Path("list_comments.json")
    @GET
    @RestApiInfo(
            desc = "列出回答下的评论",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = ApiQuestion.class,
            errors = {RestExceptionFactor.ANSWER_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiAnswerComment> listAnswerComments(
            @QueryParam(ANSWER_ID_PARAM_NAME)
            @ParamSpec(isRequired = true, spec = POSITIVE_LONG_ID_SPEC, desc = ANSWER_ID_DESC)
            long answerId,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        checkAndGetAnswer(answerId);
        List<AnswerComment> answerCommentList = knowledgeService.listAnswerComments(answerId, page, count);
        return Lists.transform(answerCommentList, new Function<AnswerComment, ApiAnswerComment>() {
            @Override
            public ApiAnswerComment apply(AnswerComment answerComment) {
                return new ApiAnswerComment(answerComment);
            }
        });
    }
}
