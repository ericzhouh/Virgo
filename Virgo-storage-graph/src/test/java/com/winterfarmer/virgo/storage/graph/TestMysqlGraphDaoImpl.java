package com.winterfarmer.virgo.storage.graph;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.storage.graph.dao.GraphDao;
import com.winterfarmer.virgo.storage.graph.dao.MysqlGraphDaoImpl;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/virgo-graph-storage-context.xml"})
//@ContextConfiguration(locations = {"classpath:spring/virgo-test-storage-graph-context.xml"})
public class TestMysqlGraphDaoImpl {
    private long p0 = 1000;
    private long p1 = 2000;
    private long p2 = 3000;
    private long p3 = 4000;
    private long p4 = 5000;

//    @Resource(name = "testGraphDBMysqlDao")
//    private GraphDao dao;

    //    @Resource(name = "questionTagGraphMysqlDao")
//    MysqlGraphDaoImpl questionTagGraphDao;
//
//    @Resource(name = "userAgreeQuestionGraphMysqlDao")
//    MysqlGraphDaoImpl userAgreeQuestionGraphDao;
//
//    @Resource(name = "userFollowQuestionGraphMysqlDao")
//    MysqlGraphDaoImpl userFollowQuestionGraphDao;
//
//    // ----------------------------------------------------------------------
//    @Resource(name = "userAgreeAnswerGraphMysqlDao")
//    MysqlGraphDaoImpl userAgreeAnswerGraphDao;
//
    @Resource(name = "userApplyExpertTagGraphMysqlDao")
    MysqlGraphDaoImpl userApplyExpertTagGraphMysqlDao;
    @Resource(name = "userFollowTagGraphMysqlDao")
    MysqlGraphDaoImpl userFollowTagGraphMysqlDao;


    @Test
    public void initAllDao() {
        userApplyExpertTagGraphMysqlDao.initGraphTables(true);
        userFollowTagGraphMysqlDao.initGraphTables(true);
    }

//
//
//    @Before
//    public void rebuildTables() {
//        MysqlGraphDaoImpl mysqlGraphDao = (MysqlGraphDaoImpl) dao;
//        mysqlGraphDao.initiateEdgeTable(true);
//        mysqlGraphDao.initiateHeadVertexTable(true);
//        mysqlGraphDao.initiateTailVertexTable(true);
//    }
//
//    @Test
//    @Ignore
//    public void test_00_00_InitTables() {
//        MysqlGraphDaoImpl mysqlGraphDao = (MysqlGraphDaoImpl) dao;
//        mysqlGraphDao.initiateEdgeTable(true);
//        mysqlGraphDao.initiateHeadVertexTable(true);
//        mysqlGraphDao.initiateTailVertexTable(true);
//    }
//
//    @Test
//    public void test_01_00_insertNewEdge() {
//        // p0->p1, p1->p2, p2->0
//        Edge edge0 = new Edge(p0, p1);
//        Edge edge1 = new Edge(p1, p2);
//        Edge edge2 = new Edge(p2, p0);
//
//        Assert.assertEquals(3, dao.insertOrUpdateEdges(edge0, edge1, edge2));
//
//        Assert.assertEquals(edge0, dao.queryEdge(p0, p1));
//        Assert.assertEquals(edge1, dao.queryEdge(p1, p2));
//        Assert.assertEquals(edge2, dao.queryEdge(p2, p0));
//
//        printAndAssertVertex(dao.queryHeadVertex(p0), 1);
//        printAndAssertVertex(dao.queryHeadVertex(p1), 1);
//        printAndAssertVertex(dao.queryHeadVertex(p2), 1);
//
//        printAndAssertVertex(dao.queryTailVertex(p0), 1);
//        printAndAssertVertex(dao.queryTailVertex(p1), 1);
//        printAndAssertVertex(dao.queryTailVertex(p2), 1);
//
//        // p0\->p1 (取消p0p1, p1p2, p2p0边)
//        edge0 = new Edge(p0, p1, Edge.DELETED_EDGE);
//        edge1 = new Edge(p1, p2, Edge.DELETED_EDGE);
//        edge2 = new Edge(p2, p0, Edge.DELETED_EDGE);
//        Assert.assertEquals(3, dao.insertOrUpdateEdges(edge0, edge1, edge2));
//
//        Assert.assertNull(dao.queryEdge(p0, p1));
//        Assert.assertNull(dao.queryEdge(p1, p2));
//        Assert.assertNull(dao.queryEdge(p2, p0));
//
//        printAndAssertVertex(dao.queryHeadVertex(p0), 0);
//        printAndAssertVertex(dao.queryHeadVertex(p1), 0);
//        printAndAssertVertex(dao.queryHeadVertex(p2), 0);
//
//        printAndAssertVertex(dao.queryTailVertex(p0), 0);
//        printAndAssertVertex(dao.queryTailVertex(p1), 0);
//        printAndAssertVertex(dao.queryTailVertex(p2), 0);
//    }
//
//    @Test
//    public void test_01_01_multiDegree() {
//        // p0->p1, p1->p1, p3->p1
//        Edge edge0 = new Edge(p0, p1);
//        Edge edge1 = new Edge(p1, p1);
//        Edge edge2 = new Edge(p2, p1);
//
//        Assert.assertEquals(3, dao.insertOrUpdateEdges(edge0, edge1, edge2));
//        Assert.assertEquals(edge0, dao.queryEdge(p0, p1));
//        Assert.assertEquals(edge1, dao.queryEdge(p1, p1));
//        Assert.assertEquals(edge2, dao.queryEdge(p2, p1));
//
//        printAndAssertVertex(dao.queryHeadVertex(p0), 1);
//        printAndAssertVertex(dao.queryHeadVertex(p1), 1);
//        printAndAssertVertex(dao.queryHeadVertex(p2), 1);
//
//        printAndAssertVertex(dao.queryTailVertex(p0), 0);
//        printAndAssertVertex(dao.queryTailVertex(p1), 3);
//        printAndAssertVertex(dao.queryTailVertex(p2), 0);
//
//        // p0->p0, p0->p1, p0->p2
//        Edge edge3 = new Edge(p0, p0);
//        Edge edge4 = new Edge(p0, p1);
//        Edge edge5 = new Edge(p0, p2);
//
//        Assert.assertEquals(3, dao.insertOrUpdateEdges(edge3, edge4, edge5));
//        Assert.assertEquals(edge3, dao.queryEdge(p0, p0));
//        Assert.assertEquals(edge4, dao.queryEdge(p0, p1));
//        Assert.assertEquals(edge5, dao.queryEdge(p0, p2));
//
//        printAndAssertVertex(dao.queryHeadVertex(p0), 3);
//        printAndAssertVertex(dao.queryHeadVertex(p1), 1);
//        printAndAssertVertex(dao.queryHeadVertex(p2), 1);
//
//        printAndAssertVertex(dao.queryTailVertex(p0), 1);
//        printAndAssertVertex(dao.queryTailVertex(p1), 3);
//        printAndAssertVertex(dao.queryTailVertex(p2), 1);
//    }
//
//    @Test
//    public void test_01_01_query_edges_by_head() throws InterruptedException {
////        List<Edge> queryEdgesByHead(long head, int limit, int offset);
////        List<Edge> queryEdgesByHead(long head, int limit, int offset, boolean updateAtDesc);
//        List<Edge> edgeList = Lists.newArrayList();
//        addThenSleep(edgeList, p0, p0, 500);
//        addThenSleep(edgeList, p0, p1, 500);
//        addThenSleep(edgeList, p0, p2, 500);
//        addThenSleep(edgeList, p0, p3, 500);
//        addThenSleep(edgeList, p0, p4, 500);
//
//        for (Edge edge : edgeList) {
//            Assert.assertEquals(1, dao.insertOrUpdateEdges(edge));
//        }
//
//        List<Edge> orderByTimeAsc = dao.queryEdgesByHead(p0, 100, 0, false);
//        assertOrderSame(edgeList, orderByTimeAsc);
//
//        List<Edge> orderByTimeDesc = dao.queryEdgesByHead(p0, 100, 0, true);
//        assertReverseSame(edgeList, orderByTimeDesc);
//
//        orderByTimeDesc = dao.queryEdgesByHead(p0, 100, 0);
//        assertReverseSame(edgeList, orderByTimeDesc);
//    }
//
//    @Test
//    public void test_01_01_query_edges_by_tail() throws InterruptedException {
////        List<Edge> queryEdgesByTail(long tail, int limit, int offset);
////        List<Edge> queryEdgesByTail(long tail, int limit, int offset, boolean updateAtDesc);
//
//        List<Edge> edgeList = Lists.newArrayList();
//        addThenSleep(edgeList, p0, p0, 500);
//        addThenSleep(edgeList, p1, p0, 500);
//        addThenSleep(edgeList, p2, p0, 500);
//        addThenSleep(edgeList, p3, p0, 500);
//        addThenSleep(edgeList, p4, p0, 500);
//
//        for (Edge edge : edgeList) {
//            Assert.assertEquals(1, dao.insertOrUpdateEdges(edge));
//        }
//
//        List<Edge> orderByTimeAsc = dao.queryEdgesByTail(p0, 100, 0, false);
//        assertOrderSame(edgeList, orderByTimeAsc);
//
//        List<Edge> orderByTimeDesc = dao.queryEdgesByTail(p0, 100, 0, true);
//        assertReverseSame(edgeList, orderByTimeDesc);
//
//        orderByTimeDesc = dao.queryEdgesByTail(p0, 100, 0);
//        assertReverseSame(edgeList, orderByTimeDesc);
//    }
//
//    private void printAndAssertVertex(Vertex vertex, int degree) {
//        System.out.println(vertex);
//        Assert.assertEquals(degree, vertex.getDegree());
//    }
//
//    private void assertOrderSame(List<Edge> expected, List<Edge> actual) {
//        assertSizeEqual(expected, actual);
//        int i = 0;
//        for (Edge e : expected) {
//            Assert.assertEquals(e, actual.get(i++));
//        }
//    }
//
//    private void assertReverseSame(List<Edge> expected, List<Edge> actual) {
//        assertSizeEqual(expected, actual);
//        int i = expected.size();
//        for (Edge e : expected) {
//            Assert.assertEquals(e, actual.get(--i));
//        }
//    }
//
//    private void assertSizeEqual(List<Edge> expected, List<Edge> actual) {
//        if (expected.size() != actual.size()) {
//            Assert.fail("assertOrderSame size not equal, expected,size: " + expected.size() + " actual.size: " + actual.size());
//        }
//    }
//
//    private void addThenSleep(List<Edge> edgeList, long head, long tail, long sleep) throws InterruptedException {
//        edgeList.add(new Edge(head, tail));
//        Thread.sleep(sleep);
//    }
}
