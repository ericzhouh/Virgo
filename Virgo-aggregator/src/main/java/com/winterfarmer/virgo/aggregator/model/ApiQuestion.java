package com.winterfarmer.virgo.aggregator.model;

import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.Question;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/15.
 */
@ApiMode(desc = "问题")
public class ApiQuestion {
    /**
     * 对于list出的问题给简单的展现形式
     *
     * @param question
     * @return
     */
    public static ApiQuestion forSimpleDisplay(Question question) {
        return null;
    }

    public ApiQuestion(Question question, ApiQuestionTag[] tags) {
    }

    public ApiQuestion(Question question, List<ApiQuestionTag> tagList) {
    }


}
