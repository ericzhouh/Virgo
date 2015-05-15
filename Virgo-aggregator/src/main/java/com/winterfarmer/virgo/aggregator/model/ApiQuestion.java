package com.winterfarmer.virgo.aggregator.model;

import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.Question;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/15.
 */
@ApiMode(desc = "问题")
public class ApiQuestion {
    public ApiQuestion(Question question, ApiQuestionTag[] tags) {
    }

    public ApiQuestion(Question question, List<ApiQuestionTag> tagList) {
    }

}
