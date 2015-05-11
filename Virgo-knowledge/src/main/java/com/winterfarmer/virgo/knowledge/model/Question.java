package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.BaseModel;
import com.winterfarmer.virgo.base.model.CommonState;

/**
 * Created by yangtianhang on 15/5/11.
 */
public class Question extends BaseModel {
    private static final long serialVersionUID = 838272856622506487L;

    private long questionId;
    private long questionerId;

    private String subject;
    private String imageIds; // comma split
    private String content;

    private CommonState commonState;
    private long createAtMs;
    private long updateAtMs;

    public Question() {
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getQuestionerId() {
        return questionerId;
    }

    public void setQuestionerId(long questionerId) {
        this.questionerId = questionerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
