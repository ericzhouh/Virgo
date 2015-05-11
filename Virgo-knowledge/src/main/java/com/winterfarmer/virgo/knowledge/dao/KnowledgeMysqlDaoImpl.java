package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/11.
 */
@Repository(value = "knowledge")
public class KnowledgeMysqlDaoImpl extends BaseDao implements KnowledgeDao {
    @Override
    public Question createQuestion(Question question, Tag... tags) {
        return null;
    }

    @Override
    public Question updateQuestion(Question question) {
        return null;
    }

    @Override
    public Question retrieveQuestion(long questionId) {
        return null;
    }

    @Override
    public List<Question> retrieveQuestionsByTag(long tagId, int offset, int limit) {
        return null;
    }

    @Override
    public List<Question> retrieveQuestionsBySubject(String subject, int offset, int limit) {
        return null;
    }

    @Override
    public List<Question> retrieveQuestionsByUser(long userId, int offset, int limit) {
        return null;
    }

    @Override
    public Answer createAnswer(Answer answer) {
        return null;
    }

    @Override
    public Answer updateQuestion(Answer answer) {
        return null;
    }

    @Override
    public List<Answer> retrieveAnswersByQuestion(long questionId, int offset, int limit) {
        return null;
    }

    @Override
    public List<Answer> retrieveAnswersByUser(long userId, int offset, int limit) {
        return null;
    }

    @Override
    public boolean agreeAnswer(long answerId, long userId) {
        return false;
    }

    @Override
    public boolean disagreeAnswer(long answerId, long userId) {
        return false;
    }

    @Override
    public boolean agreeQuestion(long questionId, long userId) {
        return false;
    }

    @Override
    public boolean disagreeQuestion(long questionId, long userId) {
        return false;
    }

    @Override
    public boolean favAnswer(long answerId, long userId) {
        return false;
    }

    @Override
    public boolean disfavAnswer(long answerId, long userId) {
        return false;
    }

    @Override
    public boolean favQuestion(long questionId, long userId) {
        return false;
    }

    @Override
    public boolean disfavQuestion(long questionId, long userId) {
        return false;
    }

    @Override
    public Tag createTag(String name, int weight) {
        return null;
    }

    @Override
    public boolean updateTag(Tag tag) {
        return false;
    }

    @Override
    public List<Tag> retrieveTagsByUser(long userId, int offset, int limit) {
        return null;
    }

    @Override
    public boolean createUserCareTag(long userId, long tagId, boolean care) {
        return false;
    }

    @Override
    public boolean updateUserCareTag(long userId, long tagId, boolean care) {
        return false;
    }
}
