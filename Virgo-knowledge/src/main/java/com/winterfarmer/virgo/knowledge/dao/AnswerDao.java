package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.knowledge.model.Answer;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public interface AnswerDao {
    Answer createAnswer(Answer question);

    Answer updateAnswer(Answer question);

    Answer retrieveAnswer(long id);

    List<Answer> listAnswers(long... ids);
}
