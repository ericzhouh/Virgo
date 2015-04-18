package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.dao.AccountRedisDao;
import com.winterfarmer.virgo.account.dao.PrivilegeDao;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-17.
 */
@Service("staffService")
public class StaffServiceImpl implements StaffService {
    @Resource(name = "privilegeMysqlDao")
    PrivilegeDao privilegeDao;

    @Resource(name = "accountRedisDao")
    AccountRedisDao accountRedisDao;

    // 权限redis：
    // 用hash table存权限信息
    // hash table的key是group type
    // hash table的field 是user id
    // hash table的val 是user对应field权限的bit位值
    @Override
    public boolean hasPrivilege(long userId, List<Role> roleList) {
        Map<GroupType, Integer> privilegeMap = Role.getPrivilegeMap(roleList);

        if (accountRedisDao.hasPrivilege(userId, privilegeMap)) {
            return true;
        }

        boolean hasPrivilege = true;
        for (Map.Entry<GroupType, Integer> entry : privilegeMap.entrySet()) {
            Privilege privilege = privilegeDao.retrievePrivilege(userId, entry.getKey());
            if (Privilege.hasPrivileges(entry.getValue(), privilege.getPrivileges())) {
                hasPrivilege = false;
            }

            accountRedisDao.setPrivilege(privilege);
        }

        return hasPrivilege;
    }

    @Override
    public boolean insertOrUpdatePrivilege(long userId, GroupType groupType, int privileges) {
        Privilege privilege = privilegeDao.retrievePrivilege(userId, groupType);
        boolean result;
        if (privilege != null) {
            result = privilegeDao.updatePrivilege(userId, groupType, privileges);
        } else {
            result = privilegeDao.createPrivilege(userId, groupType, privileges);
        }

        if (result) {
            Privilege newPrivilege = privilegeDao.retrievePrivilege(userId, groupType);
            accountRedisDao.setPrivilege(newPrivilege);
        }

        return result;
    }

    @Override
    public List<Privilege> getPrivileges(long userId) {
        return privilegeDao.retrievePrivileges(userId);
    }

    @Override
    public Privilege getPrivilege(long userId, GroupType groupType) {
        return privilegeDao.retrievePrivilege(userId, groupType);
    }

    @Override
    public List<Privilege> listStaffPrivilege(GroupType groupType, int page, int count) {
        return privilegeDao.retrievePrivileges(groupType, page * count, count);
    }
}
