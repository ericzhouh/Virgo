package com.winterfarmer.virgo.knowledge;

import com.winterfarmer.virgo.knowledge.dao.AnswerCommentMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.dao.AnswerMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.dao.QuestionMysqlDaoImpl;
import com.winterfarmer.virgo.knowledge.model.Question;
import com.winterfarmer.virgo.storage.counter.dao.CounterMysqlDaoImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
@ContextConfiguration(locations = {"classpath:spring/virgo-knowledge-context.xml"})
public class TestKnowledgeDao {
    @Resource(name = "questionMysqlDao")
    QuestionMysqlDaoImpl questionMysqlDao;

    @Resource(name = "answerMysqlDao")
    AnswerMysqlDaoImpl answerMysqlDao;

    @Resource(name = "answerCommentMysqlDao")
    AnswerCommentMysqlDaoImpl answerCommentMysqlDao;

    @Resource(name ="knowledgeCounterMysqlDao")
    CounterMysqlDaoImpl knowledgeCounterMysqlDao;

    @Test
    public void initDao() {
//        knowledgeCounterMysqlDao.initTable(true);
//        questionMysqlDao.initTable(true);
        answerMysqlDao.initTable(true);
//        answerCom、mentMysqlDao.initTable(false);
    }
//
//    @Test
//    public void testSearch() {
//        List<Question> list = questionMysqlDao.searchBySubject("test", 10, 0);
//        System.out.println(list.size());
//        for (Question q : list) {
//            System.out.println(q);
//        }
//    }

    @Test
    public void testJsoup() {
//        String html = "An <a href='http://example.com/'>example</b></a>         \n\n\n\nlink.</p>" +
//                "<p></p><p></p>  <p>   </p>    <p>    <p>asfasf</p>";
//
//        String plain = "dsfsaf        sadfasf\n\n\n\n\n\nasfasf";
//
//        Document doc = Jsoup.parse(html);
//        String text = doc.body().text();
//        System.out.println(text);
    }
}
