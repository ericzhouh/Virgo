package com.winterfarmer.virgo.knowledge;

import com.winterfarmer.virgo.knowledge.dao.QuestionMysqlDaoImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by yangtianhang on 15/5/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/virgo-test-knowledge-context.xml"})
public class TestKnowledgeDao {
    @Resource(name = "questionMysqlDao")
    QuestionMysqlDaoImpl questionMysqlDao;

    @Test
    public void initDao() {
        questionMysqlDao.initTable(true);
    }
}
