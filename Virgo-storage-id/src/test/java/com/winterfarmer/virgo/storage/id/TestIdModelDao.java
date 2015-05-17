package com.winterfarmer.virgo.storage.id;

import com.winterfarmer.virgo.redis.Vedis;
import com.winterfarmer.virgo.storage.id.bizconfig.IdModelRedisBiz;
import com.winterfarmer.virgo.storage.id.dao.IdModelDao;
import com.winterfarmer.virgo.storage.id.dao.IdModelRedisDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/14.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-base-context.xml")
public class TestIdModelDao {
    @Resource(name = "hybridToyIdModelDao")
    IdModelDao<IdModelRedisBiz.ToyIdModel> dao;

    @Resource(name = "toyIdModelMysqlDao")
    ToyIdModelMysqlDao mysql;

    @Resource(name = "toyIdModelRedisDao")
    IdModelRedisDao<IdModelRedisBiz.ToyIdModel> redis;

    private long id0 = 1000;
    private long id1 = 2000;
    private long id2 = 3000;
    private long id3 = 4000;
    private long id4 = 5000;

    @Before
    public void setUp() {
        mysql.initTable(true);
        Vedis vedis = redis.getVedis();
        vedis.del(redis.getKey(id0),
                redis.getKey(id1),
                redis.getKey(id2),
                redis.getKey(id3),
                redis.getKey(id4)
        );
    }

    @Test
    public void testNormal() {
        for (IdModelRedisBiz.ToyIdModel model : dao.list(id0, id1, id2, id3, id4)) {
            Assert.assertNull(model);
        }

        IdModelRedisBiz.ToyIdModel model0 = new IdModelRedisBiz.ToyIdModel(id0, "id0");
        IdModelRedisBiz.ToyIdModel model1 = new IdModelRedisBiz.ToyIdModel(id1, "id1");
        IdModelRedisBiz.ToyIdModel model4 = new IdModelRedisBiz.ToyIdModel(id4, "id4");
        Assert.assertNotNull(dao.insert(model0));
        Assert.assertNotNull(dao.insert(model1));
        Assert.assertNotNull(dao.insert(model4));

        Assert.assertEquals(model0, dao.get(id0));
        Assert.assertEquals(model1, dao.get(id1));
        Assert.assertEquals(model4, dao.get(id4));
        Assert.assertNull(dao.get(id2));
        Assert.assertNull(dao.get(id3));

        List<IdModelRedisBiz.ToyIdModel> modelList = dao.list(id0, id1, id2, id3, id4);
        Assert.assertEquals(model0, modelList.get(0));
        Assert.assertEquals(model1, modelList.get(1));
        Assert.assertEquals(model4, modelList.get(4));
        Assert.assertNull(modelList.get(2));
        Assert.assertNull(modelList.get(3));

        model0 = new IdModelRedisBiz.ToyIdModel(id0, "id0000");
        Assert.assertNotNull(dao.update(model0));
        Assert.assertEquals(model4, dao.get(id4));

        modelList = dao.list(id0, id1, id2, id3, id4);
        Assert.assertEquals(model0, modelList.get(0));
        Assert.assertEquals(model1, modelList.get(1));
        Assert.assertEquals(model4, modelList.get(4));
        Assert.assertNull(modelList.get(2));
        Assert.assertNull(modelList.get(3));
    }

    @Test
    public void testSupply() {
        for (IdModelRedisBiz.ToyIdModel model : dao.list(id0, id1, id2, id3, id4)) {
            Assert.assertNull(model);
        }

        IdModelRedisBiz.ToyIdModel model0 = new IdModelRedisBiz.ToyIdModel(id0, "id0");
        IdModelRedisBiz.ToyIdModel model4 = new IdModelRedisBiz.ToyIdModel(id4, "id4");
        Assert.assertNotNull(mysql.insert(model0));
        Assert.assertNotNull(mysql.insert(model4));

        Assert.assertEquals(model0, dao.get(id0));
        Assert.assertEquals(model4, dao.get(id4));
        Assert.assertNull(dao.get(id1));
        Assert.assertNull(dao.get(id2));
        Assert.assertNull(dao.get(id3));

        List<IdModelRedisBiz.ToyIdModel> modelList = dao.list(id0, id1, id2, id3, id4);
        Assert.assertEquals(model0, modelList.get(0));
        Assert.assertEquals(model4, modelList.get(4));
        Assert.assertNull(modelList.get(1));
        Assert.assertNull(modelList.get(2));
        Assert.assertNull(modelList.get(3));
    }
}
