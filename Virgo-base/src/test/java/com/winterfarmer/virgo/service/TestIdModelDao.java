package com.winterfarmer.virgo.service;

import com.winterfarmer.virgo.base.dao.IdModelDao;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/5/14.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/virgo-test-base-context.xml")
public class TestIdModelDao {
    @Resource(name = "toyIdModelDao")
    IdModelDao<ToyIdModel> dao;

    @Test
    public void test() {
//        ToyIdModel d = dao.get("d");
    }
}
