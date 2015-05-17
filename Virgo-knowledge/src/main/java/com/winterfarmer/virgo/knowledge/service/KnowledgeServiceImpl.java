package com.winterfarmer.virgo.knowledge.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.knowledge.dao.QuestionMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;
import com.winterfarmer.virgo.storage.graph.Edge;
import com.winterfarmer.virgo.storage.graph.dao.GraphDao;
import com.winterfarmer.virgo.storage.id.dao.IdModelDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by yangtianhang on 15/5/13.
 */
@Service("knowledgeService")
public class KnowledgeServiceImpl implements KnowledgeService {
    @Resource(name = "hybridQuestionDao")
    IdModelDao<Question> questionDao;

    @Resource(name = "questionMysqlDao")
    QuestionMysqlDaoImpl questionMysqlDao;

    @Resource(name = "questionTagGraphMysqlDao")
    GraphDao questionTagGraphDao;

    @Resource(name = "userAgreeQuestionGraphMysqlDao")
    GraphDao userAgreeQuestionGraphDao;

    @Resource(name = "userFollowQuestionGraphMysqlDao")
    GraphDao userFollowQuestionGraphDao;

    // ----------------------------------------------------------------------
    @Resource(name = "userAgreeAnswerGraphMysqlDao")
    GraphDao userAgreeAnswerGraphDao;

    @Resource(name = "userCollectAnswerGraphMysqlDao")
    GraphDao userCollectAnswerGraphDao;


    //===========================================================================================

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Question newQuestion(long userId, String subject, String content, String imageIds, long[] tagIds) {
        Question question = new Question();
        question.setUserId(userId);
        question.setSubject(subject);
        question.setContent(content);
        question.setImageIds(imageIds);
        question.setCommonState(CommonState.NORMAL);
        long time = System.currentTimeMillis();
        question.setCreateAtMs(time);
        question.setUpdateAtMs(time);

        question = questionDao.insert(question);

        questionTagGraphDao.insertOrUpdateEdges(Edge.createEdges(question.getId(), tagIds));

        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Question updateQuestion(Question question, long[] tagIds) {
        question.setUpdateAtMs(System.currentTimeMillis());
        question = questionDao.update(question);
        // 会对tag数进行限制，这里只当最多有100个标签
        List<Edge> tagged = questionTagGraphDao.queryEdgesByHead(question.getId(), 100, 0);

        Set<Long> previous = Sets.newHashSet();
        for (Edge edge : tagged) {
            previous.add(edge.getTail());
        }

        Set<Long> current = Sets.newHashSet();
        for (long tagId : tagIds) {
            current.add(tagId);
        }

        // tag有变化
        if (!SetUtils.isEqualSet(previous, current)) {
            Set<Long> toAddTagIds = Sets.difference(current, previous);
            if (CollectionUtils.isNotEmpty(toAddTagIds)) {
                questionTagGraphDao.insertOrUpdateEdges(Edge.createEdges(question.getId(), toAddTagIds, 0));
            }

            Set<Long> toDelTagIds = Sets.difference(previous, current);
            if (CollectionUtils.isNotEmpty(toDelTagIds)) {
                questionTagGraphDao.insertOrUpdateEdges(Edge.createEdges(question.getId(), toDelTagIds, 0));
            }
        }

        return question;
    }

    /**
     * 从业务的角度看，如果问题删除，那么和tag的关联关系也没有了。但是收藏关系应该还在。
     *
     * @param question
     * @param commonState
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Question updateQuestionState(Question question, CommonState commonState) {
        question.setCommonState(commonState);
        question.setUpdateAtMs(System.currentTimeMillis());
        List<Edge> tagged = questionTagGraphDao.queryEdgesByHead(question.getId(), 100, 0);
        long time = System.currentTimeMillis();
        for (Edge edge : tagged) {
            edge.setUpdateAtMs(time);
            edge.setState(0);
        }
        questionTagGraphDao.insertOrUpdateEdges(tagged);
        return questionDao.update(question);
    }

    @Override
    public boolean agreeQuestion(long userId, long questionId) {
        return userAgreeQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId)) > 0;
    }

    @Override
    public boolean disagreeQuestion(long userId, long questionId) {
        return userAgreeQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId, 0)) > 0;
    }

    @Override
    public boolean followQuestion(long userId, long questionId) {
        return userFollowQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId)) > 0;
    }

    @Override
    public Question getQuestion(long questionId) {
        return questionDao.get(questionId);
    }

    @Override
    public List<Question> listQuestions(int page, int count) {
        return questionMysqlDao.list(count, page * count);
    }

    @Override
    public List<Question> listQuestions(long tagId, int page, int count) {
        List<Edge> edgeList = questionTagGraphDao.queryEdgesByTail(tagId, count, page * count);
        return listQuestionsAsHead(edgeList);
    }

    @Override
    public List<Question> listUserFollowedQuestions(long userId, int page, int count) {
        List<Edge> edgeList = userFollowQuestionGraphDao.queryEdgesByHead(userId, count, page * count);
        return listQuestionsAsTail(edgeList);
    }

    @Override
    public List<Question> listUserProposedQuestions(long userId, int page, int count) {
        return questionMysqlDao.listByUser(userId, count, page * count);
    }

    @Override
    public List<Long> listQuestionTagIdsByQuestionId(long questionId) {
        List<Edge> edgeList = questionTagGraphDao.queryEdgesByHead(questionId, 100, 0);
        return Lists.transform(edgeList, new Function<Edge, Long>() {
            @Override
            public Long apply(Edge edge) {
                return edge.getTail();
            }
        });
    }

    @Override
    public Pair<String, List<String>> refineQuestionContent(String questionContent) {
        List<String> images = Lists.newArrayList();
        return Pair.of(questionContent, images);
    }

    @Override
    public Pair<String, List<String>> refineAnswerContent(String answerContent) {
        List<String> images = Lists.newArrayList();
        return Pair.of(answerContent, images);
    }

    @Override
    public boolean disfollowQuestion(long userId, long questionId) {
        return userFollowQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId, 0)) > 0;
    }

    // ========================================================================

    @Override
    public List<Question> listUserAnsweredQuestions(long userId, int page, int count) {
        return null;
    }

    @Override
    public Answer newAnswer(long userId, long questionId, String content, String imageIds) {
        return null;
    }

    @Override
    public AnswerComment newAnswerComment(long userId, long answerId, String content) {
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
    public List<Answer> ListAnswers(long questionId, int page, int count) {
        return null;
    }

    @Override
    public List<Answer> ListUserCollectedAnswers(long userId, int page, int count) {
        return null;
    }

    // ======================================================================

    @Override
    public boolean isValidTags(long... questionTagId) {
        return false;
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
    public List<QuestionTag> listQuestionTag() {
        return null;
    }

    private List<Question> listQuestionsAsHead(List<Edge> edgeList) {
        List<Long> idList = Lists.transform(edgeList, new Function<Edge, Long>() {
            @Override
            public Long apply(Edge edge) {
                return edge.getHead();
            }
        });

        long[] ids = ArrayUtil.toLongArray(idList);
        return questionDao.listByIds(ids);
    }

    private List<Question> listQuestionsAsTail(List<Edge> edgeList) {
        List<Long> idList = Lists.transform(edgeList, new Function<Edge, Long>() {
            @Override
            public Long apply(Edge edge) {
                return edge.getTail();
            }
        });

        long[] ids = ArrayUtil.toLongArray(idList);
        return questionDao.listByIds(ids);
    }
}
