package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-17.
 */
public interface StaffService {
    boolean hasPrivilege(long userId, List<Role> roleList);

    boolean createPrivilege(long userId, GroupType groupType, int privileges);

    boolean updatePrivilege(long userId, GroupType groupType, int privileges);

    List<Privilege> getPrivileges(long userId);

    Privilege getPrivilege(long userId, GroupType groupType);

    // groupType == null -> 返回所有的
    List<Privilege> listStaffPrivilege(GroupType groupType, int page, int count);
}
