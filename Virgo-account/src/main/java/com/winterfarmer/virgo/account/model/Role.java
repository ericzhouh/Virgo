package com.winterfarmer.virgo.account.model;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

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

    public static Map<GroupType, Integer> getPrivilegeMap(List<Role> roleList) {
        Map<GroupType, Integer> privilegeMap = Maps.newHashMap();
        for (Role role : roleList) {
            if (!privilegeMap.containsKey(role.getGroupType())) {
                privilegeMap.put(role.getGroupType(), 0);
            }

            int value = privilegeMap.get(role.getGroupType());
            value += role.getRolePrivilege().getBit();
            privilegeMap.put(role.getGroupType(), value);
        }

        return privilegeMap;
    }

    public RolePrivilege getRolePrivilege() {
        return rolePrivilege;
    }

    public GroupType getGroupType() {
        return groupType;
    }
}
