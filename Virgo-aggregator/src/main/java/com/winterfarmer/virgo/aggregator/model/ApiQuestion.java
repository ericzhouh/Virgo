package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.Question;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/15.
 */
@ApiMode(desc = "问题")
public class ApiQuestion {
    @JSONField(name = "question_id")
    @ApiField(desc = "问题id")
    private long questionId;

    @JSONField(name = "user_id")
    @ApiField(desc = "用户id")
    private long userId;

    @JSONField(name = "subject")
    @ApiField(desc = "题目")
    private String subject;

    @JSONField(name = "content")
    @ApiField(desc = "问题内容, 只在详情类接口中使用")
    private String content;

    @JSONField(name = "digest")
    @ApiField(desc = "问题内容摘要, 只在list类接口中使用")
    private String digest;

    @JSONField(name = "state")
    @ApiField(desc = "state")
    private int state;

    @JSONField(name = "create_at_ms")
    @ApiField(desc = "创建时间")
    private long createAtMs;

    @JSONField(name = "update_at_ms")
    @ApiField(desc = "更新时间")
    private long updateAtMs;

    @JSONField(name = "create_at_ms_str")
    @ApiField(desc = "创建时间")
    private String createAtMsStr;

    @JSONField(name = "update_at_ms_str")
    @ApiField(desc = "更新时间")
    private String updateAtMsStr;

    @JSONField(name = "tags")
    @ApiField(desc = "标签列表")
    private List<ApiQuestionTag> tags;

    @JSONField(name = "user")
    @ApiField(desc = "提问者")
    private ApiUser user;

    @JSONField(name = "answer_count")
    @ApiField(desc = "回答数量")
    private int answerCount;

    @JSONField(name = "follow_count")
    @ApiField(desc = "关注数量")
    private int followCount;

    @JSONField(name = "agree_count")
    @ApiField(desc = "对问题点赞的数量")
    private int agreeCount;

    @JSONField(name = "is_followed")
    @ApiField(desc = "是否关注")
    private Boolean isFollowed;

    @JSONField(name = "is_agreed")
    @ApiField(desc = "是否点赞")
    private Boolean isAgreed;

    public Boolean isFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public Boolean isAgreed() {
        return isAgreed;
    }

    public void setIsAgreed(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public ApiUser getUser() {
        return user;
    }

    public void setUser(ApiUser user) {
        this.user = user;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getCreateAtMs() {
        return createAtMs;
    }

    public void setCreateAtMs(long createAtMs) {
        this.createAtMs = createAtMs;
        setCreateAtMsStr();
    }

    public long getUpdateAtMs() {
        return updateAtMs;
    }

    public void setUpdateAtMs(long updateAtMs) {
        this.updateAtMs = updateAtMs;
        setUpdateAtMsStr();
    }

    public List<ApiQuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<ApiQuestionTag> tags) {
        this.tags = tags;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getUpdateAtMsStr() {
        return updateAtMsStr;
    }

    public void setUpdateAtMsStr() {
        this.updateAtMsStr = AggregatorUtils.timeConverter(this.updateAtMs);
    }

    public String getCreateAtMsStr() {
        return createAtMsStr;
    }

    public void setCreateAtMsStr() {
        this.createAtMsStr = AggregatorUtils.timeConverter(this.createAtMs);
    }


    /**
     * 对于list出的问题给简单的展现形式
     *
     * @param question
     * @return
     */
    public static ApiQuestion forSimpleDisplay(Question question) {
        ApiQuestion apiQuestion = new ApiQuestion(question);
        apiQuestion.setContent(null);
        return apiQuestion;
    }

    public ApiQuestion(Question question, ApiQuestionTag[] tags) {
        this(question);
        this.tags = Lists.newArrayList(tags);
    }

    public ApiQuestion(Question question, List<ApiQuestionTag> tagList) {
        this(question);
        this.tags = tagList;
    }

    public ApiQuestion(Question question) {
        this.questionId = question.getId();
        this.userId = question.getUserId();
        this.subject = question.getSubject();
        this.digest = question.getDigest();
        this.content = question.getContent();
        this.state = question.getCommonState().getIndex();
        this.createAtMs = question.getCreateAtMs();
        this.updateAtMs = question.getUpdateAtMs();
        setCreateAtMsStr();
        setUpdateAtMsStr();
    }
}
