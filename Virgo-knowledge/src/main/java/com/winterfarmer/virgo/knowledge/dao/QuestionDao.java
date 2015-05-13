package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.knowledge.model.Question;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 * <p/>
 * TODO: 可以把所有key是id的那种提出来, 基本上redis相关的可以写很少的代码了
 */
public interface QuestionDao {
    Question createQuestion(Question question);

    Question updateQuestion(Question question);

    Question retrieveQuestion(long id);

    List<Question> listQuestions(long... ids);
}
