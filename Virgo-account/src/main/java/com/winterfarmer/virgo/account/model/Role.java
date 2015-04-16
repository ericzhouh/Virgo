package com.winterfarmer.virgo.account.model;

/**
 * Created by yangtianhang on 15-4-16.
 */
public class Role {
    private final RoleType roleType;
    private final RolePrivilege rolePrivilege;

    public Role(RoleType roleType, RolePrivilege rolePrivilege) {
        this.roleType = roleType;
        this.rolePrivilege = rolePrivilege;
    }

    public RolePrivilege getRolePrivilege() {
        return rolePrivilege;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
