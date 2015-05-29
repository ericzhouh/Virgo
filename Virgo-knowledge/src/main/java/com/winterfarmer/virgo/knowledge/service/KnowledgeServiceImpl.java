package com.winterfarmer.virgo.knowledge.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.knowledge.dao.AnswerCommentMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.dao.AnswerMysqlDaoImpl;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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

    @Resource(name = "hybridAnswerDao")
    IdModelDao<Answer> answerDao;

    @Resource(name = "answerMysqlDao")
    AnswerMysqlDaoImpl answerMysqlDao;

    @Resource(name = "hybridAnswerCommentDao")
    IdModelDao<AnswerComment> answerCommentDao;

    @Resource(name = "answerCommentMysqlDao")
    AnswerCommentMysqlDaoImpl answerCommentMysqlDao;

    @Resource(name = "questionTagGraphMysqlDao")
    GraphDao questionTagGraphDao;

    @Resource(name = "userAgreeQuestionGraphMysqlDao")
    GraphDao userAgreeQuestionGraphDao;

    @Resource(name = "userFollowQuestionGraphMysqlDao")
    GraphDao userFollowQuestionGraphDao;

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
        return userAgreeQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId, Edge.DELETED_EDGE)) > 0;
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
        return userFollowQuestionGraphDao.insertOrUpdateEdges(new Edge(userId, questionId, Edge.DELETED_EDGE)) > 0;
    }

    @Override
    public List<Question> searchQuestion(String keywords, int page, int count) {
        keywords = StringUtils.trim(keywords);
        if (StringUtils.length(keywords) < 2) {
            return Lists.newArrayList();
        }

        return questionMysqlDao.searchBySubject(keywords, count, page * count);
    }

    // ========================================================================

    @Override
    public List<Question> listUserAnsweredQuestions(long userId, int page, int count) {
        return null;
    }

    @Override
    public Answer newAnswer(long userId, long questionId, String content, String imageIds) {
        Answer answer = new Answer();
        answer.setUserId(userId);
        answer.setQuestionId(questionId);
        answer.setContent(content);
        answer.setImageIds(imageIds);
        answer.setCommonState(CommonState.NORMAL);
        long current = System.currentTimeMillis();
        answer.setCreateAtMs(current);
        answer.setUpdateAtMs(current);

        return answerDao.insert(answer);
    }

    @Override
    public Answer getAnswer(long answerId) {
        return answerDao.get(answerId);
    }

    @Override
    public Answer updateAnswer(Answer answer) {
        answer.setUpdateAtMs(System.currentTimeMillis());
        return answerDao.update(answer);
    }

    @Override
    public Answer updateAnswerState(Answer answer, CommonState commonState) {
        answer.setUpdateAtMs(System.currentTimeMillis());
        answer.setCommonState(commonState);
        return answerDao.update(answer);
    }

    @Override
    public boolean agreeAnswer(long userId, long answerId) {
        return userAgreeAnswerGraphDao.insertOrUpdateEdges(new Edge(userId, answerId, Edge.NORMAL_EDGE)) > 0;
    }

    @Override
    public boolean disagreeAnswer(long userId, long answerId) {
        return userAgreeAnswerGraphDao.insertOrUpdateEdges(new Edge(userId, answerId, Edge.DELETED_EDGE)) > 0;
    }

    @Override
    public boolean collectAnswer(long userId, long answerId) {
        return userCollectAnswerGraphDao.insertOrUpdateEdges(new Edge(userId, answerId, Edge.NORMAL_EDGE)) > 0;
    }

    @Override
    public boolean discollectAnswer(long userId, long answerId) {
        return userCollectAnswerGraphDao.insertOrUpdateEdges(new Edge(userId, answerId, Edge.DELETED_EDGE)) > 0;
    }

    @Override
    public List<Answer> listAnswers(long questionId, int page, int count) {
        return answerMysqlDao.listByQuestionId(questionId, count, page * count);
    }

    @Override
    public List<Answer> listUserAnswers(long userId, int page, int count) {
        return answerMysqlDao.listByUserId(userId, count, page * count);
    }

    @Override
    public List<Answer> listUserCollectedAnswers(long userId, int page, int count) {
        List<Edge> edgeList = userCollectAnswerGraphDao.queryEdgesByHead(userId, count, page * count);
        return listAnswersAsTail(edgeList);
    }

    // ======================================================================
    // 目前所有的tag都是写死在程序里
    private static final Map<Long, QuestionTag> tagMap = Maps.newHashMap();
    private static final List<QuestionTag> tagList = Lists.newArrayList();


    static {
        QuestionTag[] questionTags = new QuestionTag[]{
                new QuestionTag(1000, "标签0"),
                new QuestionTag(1001, "标签1"),
                new QuestionTag(1002, "标签2"),
                new QuestionTag(1003, "标签3"),
                new QuestionTag(1004, "标签4")};

        for (QuestionTag questionTag : questionTags) {
            tagMap.put(questionTag.getId(), questionTag);
            tagList.add(questionTag);
        }
    }

    @Override
    public boolean isValidTags(long... questionTagIds) {
        for (long questionTagId : questionTagIds) {
            if (!tagMap.containsKey(questionTagId)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public QuestionTag getQuestionTag(long questionTagId) {
        return tagMap.get(questionTagId);
    }

    @Override
    public QuestionTag[] listQuestionTag(long... questionTagIds) {
        QuestionTag[] questionTags = new QuestionTag[questionTagIds.length];
        int i = 0;
        for (long questionTagId : questionTagIds) {
            questionTags[i++] = getQuestionTag(questionTagId);
        }

        return questionTags;
    }

    @Override
    public List<QuestionTag> listQuestionTag() {
        return tagList;
    }

    // ======================================================================

    @Override
    public AnswerComment newAnswerComment(long userId, long toUserId, long answerId, String content) {
        AnswerComment answerComment = new AnswerComment();
        answerComment.setAnswerId(answerId);
        answerComment.setUserId(userId);
        answerComment.setToUserId(toUserId);
        answerComment.setContent(content);
        long time = System.currentTimeMillis();
        answerComment.setCreateAtMs(time);
        answerComment.setUpdateAtMs(time);
        answerComment.setState(CommonState.NORMAL);

        return answerCommentDao.insert(answerComment);
    }

    @Override
    public AnswerComment getAnswerComment(long answerCommentId) {
        return answerCommentDao.get(answerCommentId);
    }

    @Override
    public AnswerComment updateAnswerCommentState(AnswerComment answerComment, CommonState commonState) {
        answerComment.setState(commonState);
        answerComment.setUpdateAtMs(System.currentTimeMillis());
        return answerCommentDao.update(answerComment);
    }

    @Override
    public List<AnswerComment> listAnswerComments(long answerId, int page, int count) {
        return answerCommentMysqlDao.listAnswerComment(answerId, count, page * count);
    }

    // ======================================================================

    private List<Question> listQuestionsAsHead(List<Edge> edgeList) {
        List<Long> idList = Edge.listHeads(edgeList);
        long[] ids = ArrayUtil.toLongArray(idList);
        return questionDao.listByIds(ids);
    }

    private List<Question> listQuestionsAsTail(List<Edge> edgeList) {
        List<Long> idList = Edge.listTails(edgeList);
        long[] ids = ArrayUtil.toLongArray(idList);
        return questionDao.listByIds(ids);
    }

    private List<Answer> listAnswersAsTail(List<Edge> edgeList) {
        List<Long> idList = Edge.listTails(edgeList);
        long[] ids = ArrayUtil.toLongArray(idList);
        return answerDao.listByIds(ids);
    }
}
