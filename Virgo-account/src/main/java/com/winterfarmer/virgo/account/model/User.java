package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.base.model.BaseModel;

/**
 * Created by yangtianhang on 15-2-26.
 */
public class User extends BaseModel {
    private static final long serialVersionUID = -2155394502113462241L;

    private long userId;
    private long createAtMs;
    private String nickName;
    private String hashedPassword;
    private String salt;
    private AccountVersion version;

    public User() {
    }

    public User(long userId, long createAtMs, String nickName, String hashedPassword, String salt, AccountVersion version) {
        this.userId = userId;
        this.createAtMs = createAtMs;
        this.nickName = nickName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.version = version;
    }

    public long getUserId() {
        return userId;
    }

    public AccountVersion getVersion() {
        return version;
    }

    public void setVersion(AccountVersion version) {
        this.version = version;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreateAtMs() {
        return createAtMs;
    }

    public void setCreateAtMs(long createAtMs) {
        this.createAtMs = createAtMs;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
