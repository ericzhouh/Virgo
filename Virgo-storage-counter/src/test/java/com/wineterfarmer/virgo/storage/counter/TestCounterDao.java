package com.wineterfarmer.virgo.storage.counter;

import com.winterfarmer.virgo.storage.counter.dao.CounterHybridDaoImpl;
import com.winterfarmer.virgo.storage.counter.dao.CounterMysqlDaoImpl;
import com.winterfarmer.virgo.storage.counter.dao.CounterRedisDaoImpl;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/6/5.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-storage-counter.xml")
public class TestCounterDao {
    @Resource(name = "testCounterMysqlDao")
    CounterMysqlDaoImpl mysqlDao;

    @Resource(name = "testCounterRedisDao")
    CounterRedisDaoImpl redisDao;

    @Resource(name = "testCounterHybridDao")
    CounterHybridDaoImpl hybridDao;

//    @Test
//    public void initTable() {
//        mysqlDao.initTable(true);
//    }

    @Test
    public void testCounter() {
        Assert.assertTrue(hybridDao.setCount(100, 1, 200));
        Assert.assertEquals(200, (int) redisDao.getCount(100, 1));
        Assert.assertEquals(200, (int) mysqlDao.getCount(100, 1));
        Assert.assertEquals(200, (int) hybridDao.getCount(100, 1));

        Assert.assertTrue(hybridDao.setCount(100, 1, 1000));
        Assert.assertEquals(1000, (int) redisDao.getCount(100, 1));
        Assert.assertEquals(1000, (int) mysqlDao.getCount(100, 1));
        Assert.assertEquals(1000, (int) hybridDao.getCount(100, 1));

        Assert.assertTrue(mysqlDao.setCount(100, 2, 200));
        Assert.assertEquals(200, (int) mysqlDao.getCount(100, 2));
        Assert.assertNull(redisDao.getCount(100, 2));
        Assert.assertEquals(200, (int) hybridDao.getCount(100, 2));
        Assert.assertEquals(200, (int) redisDao.getCount(100, 2));
    }
}
