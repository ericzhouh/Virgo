package com.winterfarmer.virgo.account.service;

import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.Role;
import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.common.util.CollectionsUtil;
import com.winterfarmer.virgo.data.redis.RedisBiz;
import com.winterfarmer.virgo.data.redis.Vedis;
import com.winterfarmer.virgo.data.redis.VedisFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-17.
 */
public class PrivilegeServiceImpl implements PrivilegeService {
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
    public List<Privilege> retrievePrivileges(long userId) {
        return null;
    }

    @Override
    public Privilege retrievePrivilege(long userId, GroupType groupType) {
        return null;
    }
}
