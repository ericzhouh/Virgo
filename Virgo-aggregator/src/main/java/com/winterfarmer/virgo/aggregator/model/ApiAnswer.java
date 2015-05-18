package com.winterfarmer.virgo.aggregator.model;

import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.Answer;

/**
 * Created by yangtianhang on 15/5/17.
 */
@ApiMode(desc = "回答")
public class ApiAnswer {
    public static ApiAnswer forSimpleDisplay(Answer answer) {
        return null;
    }

    public ApiAnswer(Answer answer) {

    }
}
