package com.winterfarmer.virgo.account.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.winterfarmer.virgo.storage.base.BaseModel;

/**
 * Created by yangtianhang on 15-3-4.
 */
@JSONType(ignores = {"appKey", "createAt"})
public class AccessToken extends BaseModel {
    private static final long serialVersionUID = -7409395183250576521L;
    @JSONField(name = "user_id")
    private long userId;

    @JSONField(name = "expire_at")
    private long expireAt;

    @JSONField(name = "token")
    private String token;

    @JSONField(name = "user_id_str")
    private String userIdStr;

    private int appKey;
    private long createAt;

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        this.userIdStr = Long.toString(userId);
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getAppKey() {
        return appKey;
    }

    public void setAppKey(int appKey) {
        this.appKey = appKey;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public boolean isExpire() {
        return System.currentTimeMillis() > expireAt;
    }
}
