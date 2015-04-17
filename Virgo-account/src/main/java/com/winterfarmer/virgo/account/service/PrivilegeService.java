package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-17.
 */
public interface PrivilegeService {
    boolean hasPrivilege(long userId, List<Role> roleList);

    boolean createPrivilege(long userId, GroupType groupType, int privileges);

    boolean updatePrivilege(long userId, GroupType groupType, int privileges);

    List<Privilege> retrievePrivileges(long userId);

    Privilege retrievePrivilege(long userId, GroupType groupType);
}
