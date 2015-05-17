package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

/**
 * Created by yangtianhang on 15/5/13.
 */
public class AnswerComment extends BaseIdModel {
    private static final long serialVersionUID = -8491932245013372757L;

    private long answerId;
    private long userId;
    private CommonState state;
    private String content;
    private long createAtMs;
    private long updateAtMs;

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

    public CommonState getState() {
        return state;
    }

    public void setState(CommonState state) {
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
    }

    public long getUpdateAtMs() {
        return updateAtMs;
    }

    public void setUpdateAtMs(long updateAtMs) {
        this.updateAtMs = updateAtMs;
    }
}
