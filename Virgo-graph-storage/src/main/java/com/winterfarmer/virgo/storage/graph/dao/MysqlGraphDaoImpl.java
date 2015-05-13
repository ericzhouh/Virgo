package com.winterfarmer.virgo.storage.graph.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sun.jndi.toolkit.ctx.HeadTail;
import com.winterfarmer.virgo.database.JdbcTemplateFactory;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.storage.graph.Edge;
import com.winterfarmer.virgo.storage.graph.HeadVertex;
import com.winterfarmer.virgo.storage.graph.TailVertex;
import com.winterfarmer.virgo.storage.graph.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangtianhang on 15/5/12.
 */
public class MysqlGraphDaoImpl implements GraphDao {
    public static final String EDGE_DROP = "DROP TABLE IF EXISTS `%s`";

    public static final String EDGE_DDL = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "  `head` bigint(20) NOT NULL COMMENT 'head vertex id',\n" +
            "  `tail` bigint(20) NOT NULL COMMENT 'tail vertex id',\n" +
            "  `state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '(0, 1) is (deleted, normal)',\n" +

            "  `position` int(11) NOT NULL DEFAULT '0' COMMENT 'sort position',\n" +
            "  `category` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'category',\n" +

            "  `update_at_ms` bigint(20) NOT NULL DEFAULT '0',\n" +
            "  `accessory_id` bigint(20) NOT NULL DEFAULT '0',\n" +
            "  `ext_info` varbinary(6000) DEFAULT NULL,\n" +

            "  PRIMARY KEY (`head`,`tail`),\n" +
            "  INDEX `idx_head` using hash (`head`),\n" +
            "  INDEX `idx_tail` using hash (`tail`),\n" +
            "  INDEX `idx_update_at_ms` using btree (`update_at_ms`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

    private static final String VERTEX_DROP = "DROP TABLE IF EXISTS `%s`";

    private static final String VERTEX_DDL = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "  `vertex` bigint(20) NOT NULL,\n" +
            "  `degree` int(11) NOT NULL,\n" +
            "  `create_at_ms` bigint(20) NOT NULL DEFAULT '0',\n" +
            "  `update_at_ms` bigint(20) NOT NULL DEFAULT '0',\n" +

            "  PRIMARY KEY (`vertex`),\n" +
            "  INDEX `idx_degree` using btree (`degree`),\n" +
            "  INDEX `idx_create_at_ms` using btree (`create_at_ms`),\n" +
            "  INDEX `idx_update_at_ms` using btree (`update_at_ms`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

    protected static final ListeningExecutorService writeExecutorService = MoreExecutors
            .listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4));

    protected static final ListeningExecutorService readExecutorService = MoreExecutors
            .listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8));

    public static final String GRAPH_EDGE_TABLE_PRE_FIX = "graph_edge_";
    public static final String GRAPH_VERTEX_HEAD_TABLE_PRE_FIX = "graph_vertex_head_";
    public static final String GRAPH_VERTEX_TAIL_TABLE = "graph_vertex_tail_";
    public static final String INSERT_OR_UPDATE_VERTEX_DEGREE_SQL = "INSERT INTO %s "
            + " (vertex, degree, create_at_ms, update_at_ms) "
            + " VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + " degree=VALUES(degree), "
            + " create_at_ms=VALUES(create_at_ms), "
            + " update_at_ms=VALUES(update_at_ms)";

    private String bizName;

    private String graphEdgeTable;

    private String graphVertexHeadTable;

    private String graphVertexTailTable;

    private String countHeadDegreeSql;

    private String countTailDegreeSql;

    private String insertOrUpdateHeadVertexDegreeSql;

    private String insertOrUpdateTailVertexDegreeSql;

    private JdbcTemplateFactory jdbcTemplateFactory;

    @Override
    public void init() {
    }

    public void initiateEdgeTable(boolean dropBeforeCreate) {
        if (dropBeforeCreate) {
            String drop = String.format(EDGE_DROP, getGraphEdgeTable());
            jdbcTemplateFactory.getWriteJdbcTemplate().execute(drop);
        }

        String ddl = String.format(EDGE_DDL, getGraphEdgeTable());
        jdbcTemplateFactory.getWriteJdbcTemplate().execute(ddl);
    }

    public void initiateHeadVertexTable(boolean dropBeforeCreate) {
        initiateVertexTable(true, dropBeforeCreate);
    }

    public void initiateTailVertexTable(boolean dropBeforeCreate) {
        initiateVertexTable(false, dropBeforeCreate);
    }

    private void initiateVertexTable(boolean isHead, boolean dropBeforeCreate) {
        String table = isHead ? getGraphVertexHeadTable() : getGraphVertexTailTable();
        String drop = String.format(VERTEX_DROP, table);
        String ddl = String.format(VERTEX_DDL, table);

        if (dropBeforeCreate) {
            jdbcTemplateFactory.getWriteJdbcTemplate().execute(drop);
        }

        jdbcTemplateFactory.getWriteJdbcTemplate().execute(ddl);
    }

    public String getGraphEdgeTable() {
        return graphEdgeTable;
    }

    public String getGraphVertexHeadTable() {
        return graphVertexHeadTable;
    }

    public String getGraphVertexTailTable() {
        return graphVertexTailTable;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = StringUtils.trim(bizName);

        this.graphEdgeTable = GRAPH_EDGE_TABLE_PRE_FIX + this.bizName;
        this.graphVertexHeadTable = GRAPH_VERTEX_HEAD_TABLE_PRE_FIX + this.bizName;
        this.graphVertexTailTable = GRAPH_VERTEX_TAIL_TABLE + this.bizName;

        this.countHeadDegreeSql =
                "select count(1) as rst from " + getGraphEdgeTable() + " where head=? and state=1";
        this.countTailDegreeSql =
                "select count(1) as rst from " + getGraphEdgeTable() + " where tail=? and state=1";
        this.insertOrUpdateHeadVertexDegreeSql = String.format(INSERT_OR_UPDATE_VERTEX_DEGREE_SQL, getGraphVertexHeadTable());
        this.insertOrUpdateTailVertexDegreeSql = String.format(INSERT_OR_UPDATE_VERTEX_DEGREE_SQL, getGraphVertexTailTable());
    }

    public JdbcTemplateFactory getJdbcTemplateFactory() {
        return jdbcTemplateFactory;
    }

    public void setJdbcTemplateFactory(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public static final String insert_or_update_edges_sql = "INSERT INTO %s "
            + " (head, tail, state, position, category, update_at_ms, accessory_id, ext_info) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + " state=VALUES(state), "
            + " position=VALUES(position), "
            + " category=VALUES(category), "
            + " update_at_ms=VALUES(update_at_ms), "
            + " accessory_id=VALUES(accessory_id), "
            + " ext_info=VALUES(ext_info)";

//    public static final String insert_or_update_edges_sql = "INSERT INTO %s "
//            + " (head, tail, state, position, category, update_at_ms, accessory_id, ext_info) "
//            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private class InsertBatchEdgePreparedStatementSetter implements BatchPreparedStatementSetter {
        private final Edge[] edges;

        public InsertBatchEdgePreparedStatementSetter(Edge[] edges) {
            this.edges = edges;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Edge edge = edges[i];

            ps.setLong(1, edge.getHead());
            ps.setLong(2, edge.getTail());
            ps.setInt(3, edge.getState());

            ps.setInt(4, edge.getPosition());
            ps.setInt(5, edge.getCategory());

            ps.setLong(6, edge.getUpdateAtMs());
            ps.setLong(7, edge.getAccessoryId());

            if (edge.getExtInfo() == null) {
                ps.setNull(8, Types.VARBINARY);
            } else {
                ps.setBytes(8, edge.getExtInfo());
            }
        }

        @Override
        public int getBatchSize() {
            return edges.length;
        }
    }

    @Override
    // 不保证所有的都edge都能create成功
    public int insertOrUpdateEdges(final Edge... edges) {
        if (ArrayUtils.isEmpty(edges)) {
            return 0;
        }

        ListenableFuture<Integer> future = writeExecutorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int[] counts = jdbcTemplateFactory.getWriteJdbcTemplate().batchUpdate(
                        String.format(insert_or_update_edges_sql, getGraphEdgeTable()), new InsertBatchEdgePreparedStatementSetter(edges));
                updateHeadVertexDegree(edges);
                updateTailVertexDegree(edges);
                return sumInsertOrUpdateEdgesAffectRows(counts);
            }
        });

        try {
            return Futures.get(future, DB_WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS, Exception.class);
        } catch (Exception e) {
            VirgoLogger.error(String.format("Failed to insert or update table [%s]", getGraphEdgeTable()), e);
            return 0;
        }
    }

    // 使用 on duplicate 方式, 重复的数据算2, 所以这里都是对sum进行自增
    private int sumInsertOrUpdateEdgesAffectRows(int[] ints) {
        int sum = 0;
        if (ArrayUtils.isEmpty(ints)) {
            return sum;
        }

        for (int i : ints) {
            if (i > 0) {
                ++sum;
            }
        }

        return sum;
    }

    @Override
    public int insertOrUpdateEdges(Collection<Edge> edges) {
        if (CollectionUtils.isEmpty(edges)) {
            return 0;
        }

        Edge[] edgeArray = edges.toArray(new Edge[edges.size()]);
        return insertOrUpdateEdges(edgeArray);
    }

    private static final RowMapper<Edge> edgeMapper = new RowMapper<Edge>() {
        @Override
        public Edge mapRow(ResultSet resultSet, int i) throws SQLException {
            Edge edge = new Edge();

            edge.setHead(resultSet.getLong("head"));
            edge.setTail(resultSet.getLong("tail"));
            edge.setState(resultSet.getInt("state"));

            edge.setPosition(resultSet.getInt("position"));
            edge.setCategory(resultSet.getInt("category"));

            edge.setUpdateAtMs(resultSet.getLong("update_at_ms"));
            edge.setAccessoryId(resultSet.getLong("accessory_id"));
            edge.setExtInfo(resultSet.getBytes("ext_info"));

            return edge;
        }
    };

    private String getQueryEdgesSql(boolean byHead, boolean updateAtDesc) {
        String sql = "select * from " + getGraphEdgeTable() + " where %s=? and state=1 order by update_at_ms %s limit ? offset ?";
        return String.format(sql, byHead ? "head" : "tail", updateAtDesc ? "desc" : "asc");
    }

    private List<Edge> queryEdges(boolean byHead, boolean updateAtDesc, long vertex, int limit, int offset) {
        String sql = getQueryEdgesSql(byHead, updateAtDesc);
        return queryForList(jdbcTemplateFactory.getReadJdbcTemplate(), sql, edgeMapper, vertex, limit, offset);
    }

    @Override
    public List<Edge> queryEdgesByHead(long head, int limit, int offset) {
        return queryEdges(true, true, head, limit, offset);
    }

    @Override
    public List<Edge> queryEdgesByHead(long head, int limit, int offset, boolean updateAtDesc) {
        return queryEdges(true, updateAtDesc, head, limit, offset);
    }

    @Override
    public List<Edge> queryEdgesByTail(long tail, int limit, int offset) {
        return queryEdges(false, true, tail, limit, offset);
    }

    @Override
    public List<Edge> queryEdgesByTail(long tail, int limit, int offset, boolean updateAtDesc) {
        return queryEdges(false, updateAtDesc, tail, limit, offset);
    }

    @Override
    public Edge queryEdge(long head, long tail) {
        String sql = "select * from " + getGraphEdgeTable() + " where head=? and tail=? and state=1";
        return queryForObject(jdbcTemplateFactory.getReadJdbcTemplate(), sql, edgeMapper, head, tail);
    }

    private static void setVertex(ResultSet resultSet, Vertex vertex) throws SQLException {
        vertex.setVertex(resultSet.getInt("vertex"));
        vertex.setDegree(resultSet.getInt("degree"));
        vertex.setCreateAtMs(resultSet.getLong("create_at_ms"));
        vertex.setUpdateAtMs(resultSet.getLong("update_at_ms"));
    }

    private static final RowMapper<HeadVertex> headVertexMapper = new RowMapper<HeadVertex>() {
        @Override
        public HeadVertex mapRow(ResultSet resultSet, int i) throws SQLException {
            HeadVertex vertex = new HeadVertex();
            setVertex(resultSet, vertex);
            return vertex;
        }
    };

    @Override
    public HeadVertex queryHeadVertex(long head) {
        final String sql = "select * from " + getGraphVertexHeadTable() + " where vertex=?";
        HeadVertex headVertex = queryForObject(jdbcTemplateFactory.getReadJdbcTemplate(), sql, headVertexMapper, head);
        if (headVertex == null) {
            headVertex = HeadVertex.notExistVertex(head);
        }

        return headVertex;
    }

    private static final RowMapper<TailVertex> tailVertexMapper = new RowMapper<TailVertex>() {
        @Override
        public TailVertex mapRow(ResultSet resultSet, int i) throws SQLException {
            TailVertex vertex = new TailVertex();
            setVertex(resultSet, vertex);
            return vertex;
        }
    };

    @Override
    public TailVertex queryTailVertex(long tail) {
        final String sql = "select * from " + getGraphVertexTailTable() + " where vertex=?";
        TailVertex tailVertex = queryForObject(jdbcTemplateFactory.getReadJdbcTemplate(), sql, tailVertexMapper, tail);
        if (tailVertex == null) {
            tailVertex = TailVertex.notExistVertex(tail);
        }

        return tailVertex;
    }

    private void updateHeadVertexDegree(final Edge[] edges) {
        Set<Long> headVertexSet = getHeadVertexSet(edges);
        updateVertexDegree(true, headVertexSet);
    }

    private void updateTailVertexDegree(final Edge[] edges) {
        Set<Long> tailVertexSet = getTailVertexSet(edges);
        updateVertexDegree(false, tailVertexSet);
    }

    private void updateVertexDegree(boolean isHead, Set<Long> vertexSet) {
        for (Long vertex : vertexSet) {
            updateVertexDegree(isHead, vertex);
        }
    }

    private void updateVertexDegree(boolean isHead, long vertex) {
        int degree = countDegree(isHead, vertex);
        doUpdateVertexDegree(isHead, vertex, degree);
    }

    public static final RowMapper<Integer> counterMapper = new RowMapper<Integer>() {
        @Override
        public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getInt("rst");
        }
    };

    private int countDegree(boolean isHead, long vertex) {
        String sql = isHead ? countHeadDegreeSql : countTailDegreeSql;
        // 读库可能还没有sync到主库的内容，所以直接从写库读
        return jdbcTemplateFactory.getWriteJdbcTemplate().queryForObject(sql, counterMapper, vertex);
    }

    private void doUpdateVertexDegree(boolean isHead, long vertex, int degree) {
        String sql = isHead ? insertOrUpdateHeadVertexDegreeSql : insertOrUpdateTailVertexDegreeSql;
        long time = System.currentTimeMillis();
        jdbcTemplateFactory.getWriteJdbcTemplate().update(sql, vertex, degree, time, time);
    }

    private Set<Long> getHeadVertexSet(Edge[] edges) {
        Set<Long> headVertexSet = Sets.newHashSet();
        for (Edge edge : edges) {
            headVertexSet.add(edge.getHead());
        }

        return headVertexSet;
    }

    private Set<Long> getTailVertexSet(Edge[] edges) {
        Set<Long> tailVertexSet = Sets.newHashSet();
        for (Edge edge : edges) {
            tailVertexSet.add(edge.getTail());
        }

        return tailVertexSet;
    }

    protected <T> T queryForObject(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
        if (VirgoLogger.isDebugEnabled()) {
            VirgoLogger.debug("class:{}, queryForObject sql:{}, params:{}, rowmapper:{}", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
        }

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            if (VirgoLogger.isDebugEnabled()) {
                VirgoLogger.debug("class:{}, queryForObject sql:{}, params:{}, rowmapper:{}, no_result", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
            }
            return null;
        }
    }

    protected <T> ImmutableList<T> queryForList(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
        if (VirgoLogger.isDebugEnabled()) {
            VirgoLogger.debug("class:{}, queryForList sql:{}, params:{}, rowmapper:{}", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
        }

        try {
            List<T> result = jdbcTemplate.query(sql, rowMapper, args);
            if (CollectionUtils.isNotEmpty(result)) {
                return ImmutableList.copyOf(result);
            }
        } catch (Exception e) {
            if (VirgoLogger.isDebugEnabled()) {
                VirgoLogger.debug("class:{}, queryForList sql:{}, params:{}, rowmapper:{}, exception:{}, no_result", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName(), e.getMessage());
            }
        }

        return ImmutableList.of();
    }
}
