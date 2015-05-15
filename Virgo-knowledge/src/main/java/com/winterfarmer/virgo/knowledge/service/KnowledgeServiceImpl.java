package com.winterfarmer.virgo.knowledge.service;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import com.winterfarmer.virgo.storage.graph.dao.GraphDao;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
@Service("knowledgeService")
public class KnowledgeServiceImpl implements KnowledgeService {
    @Resource(name = "userAgreeQuestionGraphMysqlDao")
    GraphDao userAgreeQuestionGraphMysqlDao;

    @Resource(name = "userDisagreeQuestionGraphMysqlDao")
    GraphDao userDisagreeQuestionGraphMysqlDao;

    @Resource(name = "userFollowQuestionGraphMysqlDao")
    GraphDao userFollowQuestionGraphMysqlDao;

    @Resource(name = "userAgreeAnswerGraphMysqlDao")
    GraphDao userAgreeAnswerGraphMysqlDao;

    @Resource(name = "userDisagreeAnswerGraphMysqlDao")
    GraphDao userDisagreeAnswerGraphMysqlDao;

    @Resource(name = "userCollectAnswerGraphMysqlDao")
    GraphDao userCollectAnswerGraphMysqlDao;

    @Resource(name = "questionTagGraphMysqlDao")
    GraphDao questionTagGraphMysqlDao;

    @Override
    public Question newQuestion(long userId, String subject, String content, String imageIds, long[] tagIds) {
        return null;
    }

    @Override
    public Answer newAnswer(long userId, long questionId, String content, String[] imageIds) {
        return null;
    }

    @Override
    public AnswerComment newAnswerComment(long userId, long answerId, String content) {
        return null;
    }

    @Override
    public Question updateQuestionState(long questionId, CommonState commonState) {
        return null;
    }

    @Override
    public Answer updateAnswerState(long answerId, CommonState commonState) {
        return null;
    }

    @Override
    public AnswerComment updateAnswerCommentState(long answerCommentId, CommonState commonState) {
        return null;
    }

    @Override
    public boolean agreeQuestion(long userId, long questionId) {
        return false;
    }

    @Override
    public boolean disagreeQuestion(long userId, long questionId) {
        return false;
    }

    @Override
    public boolean followQuestion(long userId, long questionId) {
        return false;
    }

    @Override
    public boolean agreeAnswer(long userId, long answerId) {
        return false;
    }

    @Override
    public boolean disagreeAnswer(long userId, long answerId) {
        return false;
    }

    @Override
    public boolean collectAnswer(long userId, long answerId) {
        return false;
    }

    @Override
    public List<Question> listQuestions(int count) {
        return null;
    }

    @Override
    public List<Question> listQuestions(long questionId, int count) {
        return null;
    }

    @Override
    public List<Question> listQuestions(long questionId, int tagId, int count) {
        return null;
    }

    @Override
    public List<Question> listUserFollowedQuestions(long userId, int offset, int count) {
        return null;
    }

    @Override
    public List<Question> listUserProposedQuestions(long userId, int offset, int count) {
        return null;
    }

    @Override
    public List<Question> listUserAnsweredQuestions(long userId, int offset, int count) {
        return null;
    }

    @Override
    public List<Answer> ListAnswers(long questionId, int offset, int count) {
        return null;
    }

    @Override
    public List<Answer> ListUserCollectedAnswers(long userId, int offset, int count) {
        return null;
    }

    @Override
    public List<QuestionTag> listQuestionTag() {
        return null;
    }

    @Override
    public QuestionTag getQuestionTag(long questionTagId) {
        return null;
    }

    @Override
    public List<QuestionTag> listQuestionTag(long... questionTagId) {
        return null;
    }

    @Override
    public boolean isValidTags(long... questionTagId) {
        return false;
    }

    @Override
    public Pair<String, List<String>> refineQuestionContent(String questionContent) {
        return null;
    }

    @Override
    public Question getQuestion(long questionId) {
        return null;
    }

    @Override
    public List<Long> listQuestionTagIdsByQuestionId(long questionId) {
        return null;
    }
}
