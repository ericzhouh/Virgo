package com.winterfarmer.virgo.account.model;

/**
 * Created by yangtianhang on 15-4-16.
 */
public class Role {
    private final GroupType groupType;
    private final RolePrivilege rolePrivilege;

    public Role(GroupType groupType, RolePrivilege rolePrivilege) {
        this.groupType = groupType;
        this.rolePrivilege = rolePrivilege;
    }

    public RolePrivilege getRolePrivilege() {
        return rolePrivilege;
    }

    public GroupType getGroupType() {
        return groupType;
    }
}
