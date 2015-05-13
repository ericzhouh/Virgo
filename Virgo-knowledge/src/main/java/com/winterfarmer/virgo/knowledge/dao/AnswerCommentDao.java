package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.knowledge.model.AnswerComment;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public interface AnswerCommentDao {
    AnswerComment createAnswerComment(AnswerComment question);

    AnswerComment updateAnswerComment(AnswerComment question);

    AnswerComment retrieveAnswerComment(long id);

    List<AnswerComment> listAnswerComments(long... ids);
}
