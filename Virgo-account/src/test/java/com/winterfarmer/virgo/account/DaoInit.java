package com.winterfarmer.virgo.account;


import com.winterfarmer.virgo.account.dao.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15-3-8.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-account-context.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class DaoInit {
    @Resource(name = "userMysqlDao")
    UserDao userDao;

    @Resource(name = "accessTokenMysqlDao")
    AccessTokenDao accessTokenDao;

    @Resource(name = "openPlatformAccountMysqlDao")
    OpenPlatformAccountDao openPlatformAccountDao;

    @Resource(name = "oauth2MysqlDao")
    OAuth2Dao oauth2Dao;

    @Resource(name = "privilegeMysqlDao")
    PrivilegeDao privilegeDao;

    @Test
    public void init() {
        ((UserMysqlDaoImpl) userDao).initTable(true);
        ((AccessTokenMysqlDaoImpl) accessTokenDao).initTable(true);
        ((OpenPlatformAccountMysqlDaoImpl) openPlatformAccountDao).initTable(true);
        ((OAuth2MysqlDaoImpl) oauth2Dao).initTable(true);
        ((PrivilegeMysqlDaoImpl) privilegeDao).initTable(true);
    }
}
