package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.Answer;

/**
 * Created by yangtianhang on 15/5/17.
 */
@ApiMode(desc = "回答")
public class ApiAnswer {
    @JSONField(name = "answer_id")
    @ApiField(desc = "答案id")
    private long answerId;

    @JSONField(name = "question_id")
    @ApiField(desc = "问题id")
    private long questionId;

    @JSONField(name = "user_id")
    @ApiField(desc = "用户id")
    private long userId;

    @JSONField(name = "digest")
    @ApiField(desc = "答案内容的摘要, 在list类接口中使用")
    private String digest;

    @JSONField(name = "content")
    @ApiField(desc = "答案内容, 在详情类接口中使用")
    private String content;

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

    @JSONField(name = "user")
    @ApiField(desc = "回答者")
    private ApiUser user;

    @JSONField(name = "question_subject")
    @ApiField(desc = "问题题目")
    private String questionSubject;

    @JSONField(name = "collect_count")
    @ApiField(desc = "收藏数量")
    private int collectCount;

    @JSONField(name = "agree_count")
    @ApiField(desc = "点赞数量")
    private int agreeCount;

    @JSONField(name = "comment_count")
    @ApiField(desc = "评论数量")
    private int commentCount;

    public static ApiAnswer forSimpleDisplay(Answer answer) {
        ApiAnswer apiAnswer = new ApiAnswer(answer);
        apiAnswer.setContent(null);
        return apiAnswer;
    }

    public ApiAnswer(Answer answer) {
        this.answerId = answer.getId();
        this.questionId = answer.getQuestionId();
        this.userId = answer.getUserId();
        this.content = answer.getContent();
        this.digest = answer.getDigest();
        this.state = answer.getCommonState().getIndex();
        this.createAtMs = answer.getCreateAtMs();
        this.updateAtMs = answer.getUpdateAtMs();
        setCreateAtMsStr();
        setUpdateAtMsStr();
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

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getQuestionSubject() {
        return questionSubject;
    }

    public void setQuestionSubject(String questionSubject) {
        this.questionSubject = questionSubject;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
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

    public ApiUser getUser() {
        return user;
    }

    public void setUser(ApiUser user) {
        this.user = user;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
