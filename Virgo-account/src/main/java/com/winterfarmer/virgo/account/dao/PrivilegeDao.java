package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-17.
 */
public interface PrivilegeDao {
    boolean createPrivilege(long userId, GroupType groupType, int privileges);

    boolean updatePrivilege(long userId, GroupType groupType, int privileges);

    List<Privilege> retrievePrivileges(long userId);

    Privilege retrievePrivilege(long userId, GroupType groupType);

    List<Privilege> retrievePrivileges(GroupType groupType, int offset, int limit);
}
