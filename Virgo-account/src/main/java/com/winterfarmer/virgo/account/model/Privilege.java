package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.base.model.BaseModel;

/**
 * Created by yangtianhang on 15-4-17.
 */
public class Privilege extends BaseModel {
    private static final long serialVersionUID = 1792019526712183225L;

    private long userId;
    private GroupType groupType;
    private int privileges;

    public Privilege() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public int getPrivileges() {
        return privileges;
    }

    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }
}
