package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.IndexType;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import com.winterfarmer.virgo.knowledge.model.AnswerComment;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.storage.id.dao.IdModelMysqlDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/28.
 */
@Repository(value = "answerCommentMysqlDao")
public class AnswerCommentMysqlDaoImpl extends IdModelMysqlDao<AnswerComment> {
    public static final String ANSWER_COMMENT_TABLE_NAME = "answer_comment";

    private static final BigintColumn answerCommentId = Columns.newIdColumn("answer_comment_id", true, false);
    private static final BigintColumn answerId = Columns.newLongColumn("answer_id", false);

    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final BigintColumn toUserId = (BigintColumn) new BigintColumn("to_user_id").setDisplaySize(20).setAutoIncrease(false).setAllowNull(false);

    private static final TinyIntColumn state = Columns.newStateColumn(false);
    private static final VarcharColumn content = (VarcharColumn) new VarcharColumn("content", 1024).
            setAllowNull(false).setComment("评论内容 最多1024个字").setUnique(false);
    private static final BigintColumn createAtMs = Columns.newLongColumn("create_at_ms", false);
    private static final BigintColumn updateAtMs = Columns.newLongColumn("update_at_ms", false);

    public AnswerCommentMysqlDaoImpl() {
        super(ANSWER_COMMENT_TABLE_NAME, answerCommentId, rowMapper);
    }

    private static final RowMapper<AnswerComment> rowMapper = new RowMapper<AnswerComment>() {
        @Override
        public AnswerComment mapRow(ResultSet rs, int rowNum) throws SQLException {
            AnswerComment answerComment = new AnswerComment();

            answerComment.setId(answerCommentId.getValue(rs));
            answerComment.setAnswerId(answerId.getValue(rs));
            answerComment.setUserId(userId.getValue(rs));
            answerComment.setToUserId(toUserId.getValue(rs));
            answerComment.setState(CommonState.valueByIndex(state.getValue(rs)));
            answerComment.setContent(content.getValue(rs));
            answerComment.setCreateAtMs(createAtMs.getValue(rs));
            answerComment.setUpdateAtMs(updateAtMs.getValue(rs));
            return answerComment;
        }
    };

    public static final String CREATE_DDL = new MysqlDDLBuilder(ANSWER_COMMENT_TABLE_NAME).
            addColumn(answerCommentId).addColumn(answerId).addColumn(userId).
            addColumn(toUserId).addColumn(state).addColumn(content).
            addColumn(createAtMs).addColumn(updateAtMs).
            setPrimaryKey(answerCommentId).
            addIndex(answerId).addIndex(userId).addIndex(toUserId).addIndex(IndexType.btree, createAtMs).
            setAutoIncrement(6800).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(CREATE_DDL, BaseMysqlDao.dropDDL(ANSWER_COMMENT_TABLE_NAME), dropBeforeCreate);
    }

    private static final String INSERT_ANSWER_COMMENT_SQL = insertIntoSQL(ANSWER_COMMENT_TABLE_NAME,
            answerId, userId, toUserId, state, content, createAtMs, updateAtMs);

    @Override
    protected PreparedStatement createInsertPreparedStatement(Connection connection, AnswerComment answerComment) throws SQLException {
        VirgoLogger.info(INSERT_ANSWER_COMMENT_SQL);

        PreparedStatement ps = connection.prepareStatement(INSERT_ANSWER_COMMENT_SQL, new String[]{answerCommentId.getName()});

        ps.setLong(1, answerComment.getAnswerId());
        ps.setLong(2, answerComment.getUserId());
        ps.setLong(3, answerComment.getToUserId());
        ps.setInt(4, answerComment.getState().getIndex());
        ps.setString(5, answerComment.getContent());
        ps.setLong(6, answerComment.getCreateAtMs());
        ps.setLong(7, answerComment.getUpdateAtMs());

        return ps;
    }

    private static final String UPDATE_ANSWER_COMMENT_SQL =
            updateSql(ANSWER_COMMENT_TABLE_NAME,
                    answerId, userId, toUserId, state, content, createAtMs, updateAtMs) +
                    new WhereClauseBuilder(answerCommentId.eqWhich()).build();

    @Override
    protected int doUpdate(AnswerComment answerComment) {
        return update(UPDATE_ANSWER_COMMENT_SQL,
                answerComment.getAnswerId(),
                answerComment.getUserId(),
                answerComment.getState().getIndex(),
                answerComment.getContent(),
                answerComment.getCreateAtMs(),
                answerComment.getUpdateAtMs(),
                answerComment.getId());
    }

    private static final String SELECT_ANSWER_COMMENTS_BY_ANSWER_ID =
            selectAllSql(ANSWER_COMMENT_TABLE_NAME) + new WhereClauseBuilder(answerId.eqWhich()).and(state.eq(1)).limitOffset();

    public List<AnswerComment> listAnswerComment(long answerId, int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_ANSWER_COMMENTS_BY_ANSWER_ID, rowMapper, answerId, limit, offset);
    }

    private static final String SELECT_ANSWER_COMMENT_COUNT_SQL =
            "select count(1) as counter from " + ANSWER_COMMENT_TABLE_NAME + new WhereClauseBuilder(answerId.eqWhich()).
                    and(state.eq(CommonState.NORMAL.getIndex()));

    public Integer getAnswerCommentCount(long answerId, boolean fromWrite) {
        JdbcTemplate jdbcTemplate = fromWrite ? getWriteJdbcTemplate() : getReadJdbcTemplate();

        return queryForObject(jdbcTemplate, SELECT_ANSWER_COMMENT_COUNT_SQL, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("counter");
            }
        }, answerId);
    }
}
