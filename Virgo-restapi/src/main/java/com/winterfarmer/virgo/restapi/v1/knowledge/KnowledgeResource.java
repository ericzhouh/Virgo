package com.winterfarmer.virgo.restapi.v1.knowledge;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.service.KnowledgeService;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/5/17.
 */
public abstract class KnowledgeResource extends BaseResource {
    @Resource(name = "knowledgeService")
    KnowledgeService knowledgeService;

    protected static final String ANSWER_ID_DESC = "答案id";
    protected static final String QUESTION_ID_DESC = "问题id";

    protected static final String ANSWER_ID_PARAM_NAME = "answer_id";
    protected static final String QUESTION_ID_PARAM_NAME = "question_id";

    protected Question checkAndGetQuestion(long questionId) {
        Question question = knowledgeService.getQuestion(questionId);
        if (question == null || question.getCommonState() == CommonState.DELETE) {
            throw new VirgoRestException(RestExceptionFactor.QUESTION_NOT_EXISTED);
        }

        return question;
    }
}
