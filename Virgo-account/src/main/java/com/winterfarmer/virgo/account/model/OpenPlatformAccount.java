package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.base.model.BaseModel;

/**
 * Created by yangtianhang on 15-2-26.
 */
public class OpenPlatformAccount extends BaseModel {
    private static final long serialVersionUID = -3698991647916107528L;

    private long userId;
    private String openId;
    private PlatformType platformType;
    private String openToken;
    private CommonState commonState;
    private long createAtMs;

    public OpenPlatformAccount() {
    }

    public OpenPlatformAccount(long userId, String openId, PlatformType platformType, String openToken, CommonState commonState) {
        this.userId = userId;
        this.openId = openId;
        this.platformType = platformType;
        this.openToken = openToken;
        this.commonState = commonState;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public String getOpenToken() {
        return openToken;
    }

    public void setOpenToken(String openToken) {
        this.openToken = openToken;
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
}
