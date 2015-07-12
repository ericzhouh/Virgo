package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15/5/29.
 */
@ApiMode(desc = "回答的评论")
public class ApiAnswerComment {
    public static final int MAX_DIGEST_LENGTH = 140;

    @JSONField(name = "answer_id")
    @ApiField(desc = "答案id")
    private long answerId;

    @JSONField(name = "user_id")
    @ApiField(desc = "用户id")
    private long userId;

    @JSONField(name = "to_user_id")
    @ApiField(desc = "被评论的用户, 0表示直接评论给问题")
    private long toUserId;

    @JSONField(name = "state")
    @ApiField(desc = "state")
    private int state;

    @JSONField(name = "content")
    @ApiField(desc = "评论内容的摘要")
    private String digest;

    @JSONField(name = "digest")
    @ApiField(desc = "评论内容")
    private String content;

    @JSONField(name = "create_at_ms")
    @ApiField(desc = "创建时间")
    private long createAtMs;

    @JSONField(name = "update_at_ms")
    @ApiField(desc = "更新时间")
    private long updateAtMs;

    @JSONField(name = "create_at_ms_str")
    @ApiField(desc = "创建时间 string")
    private String createAtMsStr;

    @JSONField(name = "update_at_ms_str")
    @ApiField(desc = "更新时间 string")
    private String updateAtMsStr;

    @JSONField(name = "user")
    @ApiField(desc = "评论者")
    private ApiUser user;

    @JSONField(name = "to_user")
    @ApiField(desc = "被评论者")
    private ApiUser toUser;

    public static ApiAnswerComment forSimpleDisplay(AnswerComment answerComment) {
        ApiAnswerComment apiAnswerComment = new ApiAnswerComment(answerComment);
        apiAnswerComment.setContent(null);
        return apiAnswerComment;
    }

    public ApiAnswerComment(AnswerComment answerComment) {
        this.answerId = answerComment.getAnswerId();
        this.userId = answerComment.getUserId();
        this.toUserId = answerComment.getToUserId();
        this.state = answerComment.getState().getIndex();
        this.content = answerComment.getContent();
        this.digest = StringUtils.substring(this.content, 0, MAX_DIGEST_LENGTH);
        this.createAtMs = answerComment.getCreateAtMs();
        this.updateAtMs = answerComment.getUpdateAtMs();
        setCreateAtMsStr();
        setUpdateAtMsStr();
    }

    public String getCreateAtMsStr() {
        return createAtMsStr;
    }

    public void setCreateAtMsStr() {
        this.createAtMsStr = AggregatorUtils.timeConverter(this.createAtMs);
    }

    public String getUpdateAtMsStr() {
        return updateAtMsStr = AggregatorUtils.timeConverter(this.updateAtMs);
    }

    public void setUpdateAtMsStr() {
        this.updateAtMsStr = updateAtMsStr;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public ApiUser getToUser() {
        return toUser;
    }

    public void setToUser(ApiUser toUser) {
        this.toUser = toUser;
    }
}
