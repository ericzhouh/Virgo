package com.winterfarmer.virgo.knowledge.service;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.knowledge.model.Question;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public interface KnowledgeService {
    Question newQuestion(long userId, String subject, String content, String[] imageIds, long[] tagIds);

    Answer newAnswer(long userId, long questionId, String content, String[] imageIds);

    AnswerComment newAnswerComment(long userId, long answerId, String content);

    Question updateQuestionState(long questionId, CommonState commonState);

    Answer updateAnswerState(long answerId, CommonState commonState);

    AnswerComment updateAnswerCommentState(long answerCommentId, CommonState commonState);

    boolean agreeQuestion(long userId, long questionId);

    boolean disagreeQuestion(long userId, long questionId);

    boolean followQuestion(long userId, long questionId);

    boolean agreeAnswer(long userId, long answerId);

    boolean disagreeAnswer(long userId, long answerId);

    boolean collectAnswer(long userId, long answerId);

    /**
     * List the last (count number) questions
     *
     * @param count
     * @return
     */
    List<Question> listQuestions(int count);

    /**
     * List the last (count number) questions from questionId
     *
     * @param questionId
     * @param count
     * @return
     */
    List<Question> listQuestions(long questionId, int count);

    /**
     * List the last (count number) questions of tagId from questionId
     *
     * @param questionId
     * @param tagId
     * @param count
     * @return
     */
    List<Question> listQuestions(long questionId, int tagId, int count);

    /**
     * List (count number) questions of user followed from offset
     *
     * @param userId
     * @param count
     * @return
     */
    List<Question> listUserFollowedQuestions(long userId, int offset, int count);

    /**
     * @param userId
     * @param offset
     * @param count
     * @return
     */
    List<Question> listUserProposedQuestions(long userId, int offset, int count);

    /**
     * @param userId
     * @param offset
     * @param count
     * @return
     */
    List<Question> listUserAnsweredQuestions(long userId, int offset, int count);

    /**
     * List (count number) answers of questionId from offset
     *
     * @param questionId
     * @param offset
     * @param count
     * @return
     */
    List<Answer> ListAnswers(long questionId, int offset, int count);

    /**
     * List (count number) answers of user collected from offset
     *
     * @param userId
     * @param offset
     * @param count
     * @return
     */
    List<Answer> ListUserCollectedAnswers(long userId, int offset, int count);
}
