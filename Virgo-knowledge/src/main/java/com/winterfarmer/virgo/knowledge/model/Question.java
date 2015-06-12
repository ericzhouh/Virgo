package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

/**
 * Created by yangtianhang on 15/5/11.
 */
public class Question extends BaseIdModel {
    private static final long serialVersionUID = 4035352746273381032L;

    private long userId;
    private String subject;
    private String imageIds; // comma split
    private String digest;
    private String content;

    private CommonState commonState;
    private long createAtMs;
    private long updateAtMs;

    public Question() {
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

    public String getImageIds() {
        return imageIds;
    }

    public void setImageIds(String imageIds) {
        this.imageIds = imageIds;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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
