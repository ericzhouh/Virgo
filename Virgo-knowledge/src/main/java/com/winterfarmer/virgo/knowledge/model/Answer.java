package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.BaseModel;
import com.winterfarmer.virgo.base.model.CommonState;

/**
 * Created by yangtianhang on 15/5/11.
 */
public class Answer extends BaseModel {
    private static final long serialVersionUID = -1499986423315815946L;

    private long answerId;
    private long answererId;
    private long questionId;

    private String imageIds;
    private String content;

    private CommonState commonState;
    private long createAtMs;
    private long updateAtMs;

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

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
