package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-4-16.
 */
public enum RolePrivilege {
    VIEW("查", 0),
    MERGE("增/修改", 1),
    DELETE("删", 2),
    ADMIN("管理", 3),;

    private final String name;
    private final int index;

    private RolePrivilege(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static RolePrivilege valueByIndex(int index) {
        for (RolePrivilege rolePrivilege : RolePrivilege.values()) {
            if (rolePrivilege.getIndex() == index) {
                return rolePrivilege;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return EnumUtil.toString(GroupType.class, this.name, this.index);
    }
}
