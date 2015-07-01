package com.winterfarmer.virgo.account;


import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.account.dao.*;
import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.UserInfo;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
    @Resource(name = "accountMysqlDao")
    AccountDao accountDao;

    @Resource(name = "accessTokenMysqlDao")
    AccessTokenDao accessTokenDao;

    @Resource(name = "openPlatformAccountMysqlDao")
    OpenPlatformAccountDao openPlatformAccountDao;

    @Resource(name = "privilegeMysqlDao")
    PrivilegeDao privilegeDao;

    @Resource(name = "userInfoMysqlDao")
    UserInfoDao userInfoDao;

    @Resource(name = "userInfoHybridDao")
    UserInfoDao userInfoHybridDao;

    @Resource(name = "userInfoRedisDao")
    UserInfoDao userInfoRedisDao;

    @Test
    public void init() {
        ((AccountMysqlDaoImpl) accountDao).initTable(true);
//        ((AccessTokenMysqlDaoImpl) accessTokenDao).initTable(true);
//        ((OpenPlatformAccountMysqlDaoImpl) openPlatformAccountDao).initTable(true);
//        ((PrivilegeMysqlDaoImpl) privilegeDao).initTable(true);
//        ((UserInfoMysqlDaoImpl) userInfoDao).initTable(true);
    }

    @Test
    @Ignore
    public void testAccessToken() {
        String tokenString = "{\"expire_at\":1461056605436,\"token\":\"0.UyDx1cGLA1wgjq26fAxDhWSrJMioJW\",\"user_id\":1201500287756537856}";

        AccessToken accessToken = JSON.parseObject(tokenString, AccessToken.class);
        System.out.println(accessToken);
        Object object = JSON.parse(tokenString);
        System.out.println(object);

        object = JSON.parse("{\"properties\":{}}");
        System.out.println(object);

        System.out.println("==============");

        AccessToken accessToken1 = new AccessToken();
        accessToken1.setAppKey(1000);
        accessToken1.setCreateAt(2000);
        accessToken1.setExpireAt(33300);
        accessToken1.setToken("this");
        accessToken1.setUserId(5000);

        System.out.println("$$$$$$$");

        System.out.println("accessToken1:" + accessToken1);

        String val = JSON.toJSONString(accessToken1);
        System.out.println(val);
        System.out.println("---------------------");
        System.out.println(JSON.parseObject(val));
        AccessToken accessToken2 = JSON.parseObject(val, AccessToken.class);
        System.out.println(accessToken2);
    }

    @Test
    public void testUserInfoDao() {
        long userId = 99999;
        UserInfo userInfo = new UserInfo(userId);
        String nickName = "ttnick";
        userInfo.setNickName(nickName);

        Assert.assertTrue(userInfoHybridDao.create(userInfo));

        Assert.assertNotNull(userInfoRedisDao.retrieveUser(userId, true));
        Assert.assertNotNull(userInfoHybridDao.retrieveUser(userId, true));
        Assert.assertNotNull(userInfoHybridDao.retrieveUser(userId, false));
        userInfo = userInfoHybridDao.retrieveUser(userId, false);
        Assert.assertEquals(nickName, userInfo.getNickName());

        String intro = "122345";
        nickName = "kknickname";
        userInfo.setIntroduction(intro);
        userInfo.setNickName(nickName);
        Assert.assertTrue(userInfoHybridDao.update(userInfo));
        userInfo = userInfoHybridDao.retrieveUser(userId, false);
        Assert.assertNotNull(userInfo);
        Assert.assertEquals(intro, userInfo.getIntroduction());
        Assert.assertEquals(nickName, userInfo.getNickName());
    }
}
