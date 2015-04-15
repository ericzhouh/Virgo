package com.winterfarmer.virgo.account.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.winterfarmer.virgo.base.model.BaseModel;

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

    private int appKey;
    private long createAt;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
        return expireAt > System.currentTimeMillis();
    }
}
