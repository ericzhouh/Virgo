package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-4-16.
 */
public enum RoleType {
    PUBLIC("公开", 0),
    SUPERVISOR("超级管理员", 1),
    CONTENT("内容组", 2),;

    private final String name;
    private final int index;

    private RoleType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static RoleType valueByIndex(int index) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getIndex() == index) {
                return roleType;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return EnumUtil.toString(RoleType.class, this.name, this.index);
    }
}
