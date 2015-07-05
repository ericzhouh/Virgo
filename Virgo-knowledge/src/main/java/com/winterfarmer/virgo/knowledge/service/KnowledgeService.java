package com.winterfarmer.virgo.knowledge.service;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15/5/13.
 */
public interface KnowledgeService {
    Question newQuestion(long userId, String subject, String content, String imageIds, long[] tagIds);

    Question updateQuestion(Question question, long[] tagIds);

    Question updateQuestionState(Question question, CommonState commonState);

    boolean agreeQuestion(long userId, long questionId);

    boolean disagreeQuestion(long userId, long questionId);

    boolean followQuestion(long userId, long questionId);

    Question getQuestion(long questionId);

    /**
     * @param page
     * @param count
     * @return
     */
    List<Question> listQuestions(int page, int count);

    /**
     * List the last (count number) questions of tagId from questionId
     *
     * @param tagId
     * @param page
     * @param count
     * @return
     */
    List<Question> listQuestions(long tagId, int page, int count);

    /**
     * List (count number) questions of user followed from page
     *
     * @param userId
     * @param count
     * @return
     */
    List<Question> listUserFollowedQuestions(long userId, int page, int count);

    /**
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Question> listUserProposedQuestions(long userId, int page, int count);

    List<Long> listQuestionTagIdsByQuestionId(long questionId);

    int getQuestionFollowCount(long questionId);

    int getQuestionAgreeCount(long questionId);

    int getQuestionAnswerCount(long questionId);

    /**
     * @param questionContent
     * @return result[0] - refined content,
     * result[1] - image ids
     */
    Pair<String, List<String>> refineQuestionContent(String questionContent);

    Pair<String, List<String>> refineAnswerContent(String answerContent);

    boolean disfollowQuestion(long userId, long questionId);

    List<Question> searchQuestion(String keywords, int page, int count);

    //------------------------------------------------------------------------------

    /**
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Question> listUserAnsweredQuestions(long userId, int page, int count);

    Answer newAnswer(long userId, long questionId, String content, String imageIds);

    Answer getAnswer(long answerId);

    Answer updateAnswer(Answer answer);

    Answer updateAnswerState(Answer answer, CommonState commonState);

    boolean agreeAnswer(long userId, long answerId);

    boolean disagreeAnswer(long userId, long answerId);

    boolean collectAnswer(long userId, long answerId);

    boolean discollectAnswer(long userId, long answerId);

    /**
     * List (count number) answers from page
     *
     * @param page
     * @param count
     * @return
     */
    List<Answer> listAnswers(int page, int count);

    /**
     * List (count number) answers of questionId from page
     *
     * @param questionId
     * @param page
     * @param count
     * @return
     */
    List<Answer> listQuestionAnswers(long questionId, int page, int count);

    /**
     * List (count number) answers of user answerd from page
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Answer> listUserAnswers(long userId, int page, int count);

    /**
     * List (count number) answers of user collected from page
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Answer> listUserCollectedAnswers(long userId, int page, int count);

    List<Answer> listUserQuestionAnswers(long userId, int page, int count);

    int getAnswerCollectCount(long answerId);

    int getAnswerAgreeCount(long answerId);

    int getAnswerCommentCount(long answerId);

    // ========================================================================
    boolean isValidTags(long... questionTagId);

    QuestionTag getQuestionTag(long questionTagId);

    QuestionTag[] listQuestionTag(long... questionTagId);

    List<QuestionTag> listQuestionTag(List<Long> questionTagIdList);

    List<QuestionTag> listQuestionTag();

    // ========================================================================
    AnswerComment newAnswerComment(long userId, long toUserId, long answerId, String content);

    AnswerComment getAnswerComment(long answerCommentId);

    AnswerComment updateAnswerCommentState(AnswerComment answerComment, CommonState commonState);

    List<AnswerComment> listAnswerComments(long answerId, int page, int count);

    // ========================================================================
    Map<KnowledgeCounterType, Integer> getUserCounter(Collection<KnowledgeCounterType> counterTypes, long userId);
}
