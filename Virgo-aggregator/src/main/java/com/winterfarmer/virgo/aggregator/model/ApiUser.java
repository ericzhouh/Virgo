package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;

/**
 * Created by yangtianhang on 15-4-18.
 */
@ApiMode(desc = "用户信息")
public class ApiUser {
    @JSONField(name = "user_id")
    @ApiField(desc = "用户id")
    private long userId;

    @JSONField(name = "昵称")
    @ApiField(desc = "nick_name")
    private String nickName;

    public ApiUser(User user) {
        this.userId = user.getUserId();
        this.nickName = user.getNickName();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
