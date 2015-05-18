package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.base.model.CommonState;
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
    @ApiField(desc = "问题内容")
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

    @JSONField(name = "tags")
    @ApiField(desc = "标签列表")
    private List<ApiQuestionTag> tags;

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
    }

    public long getUpdateAtMs() {
        return updateAtMs;
    }

    public void setUpdateAtMs(long updateAtMs) {
        this.updateAtMs = updateAtMs;
    }

    public List<ApiQuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<ApiQuestionTag> tags) {
        this.tags = tags;
    }

    /**
     * 对于list出的问题给简单的展现形式
     *
     * @param question
     * @return
     */
    public static ApiQuestion forSimpleDisplay(Question question) {
        return new ApiQuestion(question);
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
        this.content = question.getContent();
        this.state = question.getCommonState().getIndex();
        this.createAtMs = question.getCreateAtMs();
        this.updateAtMs = question.getUpdateAtMs();
    }
}
