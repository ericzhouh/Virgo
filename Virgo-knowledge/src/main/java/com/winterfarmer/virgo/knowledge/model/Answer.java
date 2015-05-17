package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

/**
 * Created by yangtianhang on 15/5/11.
 */
public class Answer extends BaseIdModel {
    private static final long serialVersionUID = -9177417427886797005L;

    private long answererId;
    private long questionId;

    private String imageIds;
    private String content;

    private CommonState commonState;
    private long createAtMs;
    private long updateAtMs;

    public long getAnswererId() {
        return answererId;
    }

    public void setAnswererId(long answererId) {
        this.answererId = answererId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getImageIds() {
        return imageIds;
    }

    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommonState getCommonState() {
        return commonState;
    }

    public void setCommonState(CommonState commonState) {
        this.commonState = commonState;
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
