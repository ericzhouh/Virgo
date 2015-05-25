package com.winterfarmer.virgo.knowledge;

import com.winterfarmer.virgo.knowledge.dao.AnswerMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.dao.QuestionMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.model.Question;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/virgo-test-knowledge-context.xml"})
public class TestKnowledgeDao {
    @Resource(name = "questionMysqlDao")
    QuestionMysqlDaoImpl questionMysqlDao;

    @Resource(name = "answerMysqlDao")
    AnswerMysqlDaoImpl answerMysqlDao;


    //    @Test
//    public void initDao() {
//        questionMysqlDao.initTable(false);
//        answerMysqlDao.initTable(false);
//    }
    @Test
    public void testSearch() {
        List<Question> list = questionMysqlDao.searchBySubject("test", 10, 0);
        System.out.println(list.size());
        for (Question q : list) {
            System.out.println(q);
        }
    }
}
