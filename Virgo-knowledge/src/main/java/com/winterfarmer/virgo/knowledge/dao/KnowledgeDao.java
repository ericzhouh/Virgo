package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.Tag;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/11.
 */
public interface KnowledgeDao {
    // questions
    Question createQuestion(Question question, Tag... tags);

    Question updateQuestion(Question question);

    Question retrieveQuestion(long questionId);

    List<Question> retrieveQuestionsByTag(long tagId, int offset, int limit);

    List<Question> retrieveQuestionsBySubject(String subject, int offset, int limit);

    List<Question> retrieveQuestionsByUser(long userId, int offset, int limit);

    // answers
    Answer createAnswer(Answer answer);

    Answer updateQuestion(Answer answer);

    List<Answer> retrieveAnswersByQuestion(long questionId, int offset, int limit);

    List<Answer> retrieveAnswersByUser(long userId, int offset, int limit);

    // agree / disagree
    boolean agreeAnswer(long answerId, long userId);

    boolean disagreeAnswer(long answerId, long userId);

    boolean agreeQuestion(long questionId, long userId);

    boolean disagreeQuestion(long questionId, long userId);

    // fav / disfav
    boolean favAnswer(long answerId, long userId);

    boolean disfavAnswer(long answerId, long userId);

    boolean favQuestion(long questionId, long userId);

    boolean disfavQuestion(long questionId, long userId);

    // tags
    Tag createTag(String name, int weight);

    boolean updateTag(Tag tag);

    List<Tag> retrieveTagsByUser(long userId, int offset, int limit);

    boolean createUserCareTag(long userId, long tagId, boolean care);

    boolean updateUserCareTag(long userId, long tagId, boolean care);
}
