package com.winterfarmer.virgo.account.service;

import com.winterfarmer.virgo.account.dao.PrivilegeDao;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;
import com.winterfarmer.virgo.data.redis.RedisBiz;
import com.winterfarmer.virgo.data.redis.Vedis;
import com.winterfarmer.virgo.data.redis.VedisFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-17.
 */
@Service("staffService")
public class StaffServiceImpl implements StaffService {
    @Resource(name = "privilegeMysqlDao")
    PrivilegeDao privilegeDao;

    private Vedis vedis;

    @PostConstruct
    public void init() {
        vedis = VedisFactory.getVedis(RedisBiz.Privilege);
    }


    // 权限redis：
    // 用hash table存权限信息
    // hash table的key是group type
    // hash table的field 是user id
    // hash table的val 是user对应field权限的bit位值
    @Override
    public boolean hasPrivilege(long userId, List<Role> roleList) {
        return false;
    }

    @Override
    public boolean createPrivilege(long userId, GroupType groupType, int privileges) {
        return false;
    }

    @Override
    public boolean updatePrivilege(long userId, GroupType groupType, int privileges) {
        return false;
    }

    @Override
    public List<Privilege> getPrivileges(long userId) {
        return null;
    }

    @Override
    public Privilege getPrivilege(long userId, GroupType groupType) {
        return null;
    }

    @Override
    public List<Privilege> listStaffPrivilege(GroupType groupType, int page, int count) {
        return privilegeDao.retrievePrivileges(groupType, page * count, count);
    }
}
