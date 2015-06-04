package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.storage.base.BaseModel;

import java.sql.Date;

/**
 * Created by yangtianhang on 15/6/3.
 */
public class UserInfo extends BaseModel {
    private static final long serialVersionUID = -5722138525357993687L;

    private long userId;
    private String nickName;
    private String portrait;
    private int gender;
    private Date birthday; // TODO: 使用java.sql.Date代表一个纯粹的日期
    private String realName;
    private String email;
    private String introduction;
    private UserType userType;

    public UserInfo() {
        this.userType = UserType.NORMAL;
        this.birthday = Date.valueOf("1970-01-01");
    }

    public UserInfo(long userId, String nickName) {
        this();
        this.userId = userId;
        this.nickName = nickName;
    }

    public UserInfo(long userId) {
        this();
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
