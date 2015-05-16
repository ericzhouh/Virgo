package com.winterfarmer.virgo.knowledge.service;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public interface KnowledgeService {
    Question newQuestion(long userId, String subject, String content, String imageIds, long[] tagIds);

    Question updateQuestion(Question question, long[] tagIds);

    Answer newAnswer(long userId, long questionId, String content, String[] imageIds);

    AnswerComment newAnswerComment(long userId, long answerId, String content);

    Question updateQuestionState(long questionId, CommonState commonState);

    Answer updateAnswerState(long answerId, CommonState commonState);

    AnswerComment updateAnswerCommentState(long answerCommentId, CommonState commonState);

    boolean agreeQuestion(long userId, long questionId);

    boolean disagreeQuestion(long userId, long questionId);

    boolean followQuestion(long userId, long questionId);

    boolean disfollowQuestion(long userId, long questionId);

    boolean agreeAnswer(long userId, long answerId);

    boolean disagreeAnswer(long userId, long answerId);

    boolean collectAnswer(long userId, long answerId);

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

    /**
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Question> listUserAnsweredQuestions(long userId, int page, int count);

    /**
     * List (count number) answers of questionId from page
     *
     * @param questionId
     * @param page
     * @param count
     * @return
     */
    List<Answer> ListAnswers(long questionId, int page, int count);

    /**
     * List (count number) answers of user collected from page
     *
     * @param userId
     * @param page
     * @param count
     * @return
     */
    List<Answer> ListUserCollectedAnswers(long userId, int page, int count);

    List<QuestionTag> listQuestionTag();

    QuestionTag getQuestionTag(long questionTagId);

    List<QuestionTag> listQuestionTag(long... questionTagId);

    List<Long> listQuestionTagIdsByQuestionId(long questionId);

    boolean isValidTags(long... questionTagId);

    /**
     * @param questionContent
     * @return result[0] - refined content,
     * result[1] - image ids
     */
    Pair<String, List<String>> refineQuestionContent(String questionContent);
}
