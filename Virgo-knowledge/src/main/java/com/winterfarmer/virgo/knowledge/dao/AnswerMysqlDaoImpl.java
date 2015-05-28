package com.winterfarmer.virgo.knowledge.dao;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.IndexType;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.binary.ExtInfoColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.TextColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import com.winterfarmer.virgo.knowledge.model.Answer;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.storage.id.dao.IdModelMysqlDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangtianhang on 15/5/18.
 */
@Repository(value = "answerMysqlDao")
public class AnswerMysqlDaoImpl extends IdModelMysqlDao<Answer> {
    public static final String ANSWER_TABLE_NAME = "answer";

    private static final BigintColumn answerId = Columns.newIdColumn("answer_id", true, false);

    private static final BigintColumn questionId = Columns.newLongColumn("question_id", false);
    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final VarcharColumn imageIds = (VarcharColumn) new VarcharColumn("image_ids", 256).
            setAllowNull(false).setComment("逗号分隔的image id").setUnique(false);
    private static final TextColumn content = (TextColumn) new TextColumn("content").
            setAllowNull(false).setComment("content");
    private static final TinyIntColumn state = Columns.newStateColumn(false);
    private static final BigintColumn createAtMs = Columns.newLongColumn("create_at_ms", false);
    private static final BigintColumn updateAtMs = Columns.newLongColumn("update_at_ms", false);
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public AnswerMysqlDaoImpl() {
        super(ANSWER_TABLE_NAME, answerId, rowMapper);
    }

    private static final RowMapper<Answer> rowMapper = new RowMapper<Answer>() {
        @Override
        public Answer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Answer answer = new Answer();
            answer.setId(answerId.getValue(rs));
            answer.setQuestionId(questionId.getValue(rs));
            answer.setUserId(userId.getValue(rs));
            answer.setImageIds(imageIds.getValue(rs));
            answer.setContent(content.getValue(rs));
            answer.setCommonState(CommonState.valueByIndex(state.getValue(rs)));
            answer.setCreateAtMs(createAtMs.getValue(rs));
            answer.setUpdateAtMs(updateAtMs.getValue(rs));
            answer.setProperties(extInfo.getValue(rs));
            return answer;
        }
    };

    public static final String CREATE_DDL = new MysqlDDLBuilder(ANSWER_TABLE_NAME).
            addColumn(answerId).addColumn(questionId).addColumn(userId).
            addColumn(imageIds).addColumn(content).addColumn(state).
            addColumn(createAtMs).addColumn(updateAtMs).addColumn(extInfo).
            setPrimaryKey(IndexType.btree, answerId).addIndex(questionId).addIndex(userId).
            addIndex(IndexType.btree, createAtMs).setAutoIncrement(6800).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(CREATE_DDL, BaseMysqlDao.dropDDL(ANSWER_TABLE_NAME), dropBeforeCreate);
    }

    private static final String INSERT_ANSWER_SQL = insertIntoSQL(ANSWER_TABLE_NAME,
            questionId, userId, imageIds, content, state, createAtMs, updateAtMs, extInfo);

    @Override
    protected PreparedStatement createInsertPreparedStatement(Connection connection, Answer answer) throws SQLException {
        VirgoLogger.info(INSERT_ANSWER_SQL);

        PreparedStatement ps = connection.prepareStatement(INSERT_ANSWER_SQL, new String[]{answerId.getName()});

        ps.setLong(1, answer.getQuestionId());
        ps.setLong(2, answer.getUserId());
        ps.setString(3, answer.getImageIds());
        ps.setString(4, answer.getContent());
        ps.setInt(5, answer.getCommonState().getIndex());
        ps.setLong(6, answer.getCreateAtMs());
        ps.setLong(7, answer.getUpdateAtMs());
        setExtForPreparedStatement(ps, 8, answer.getProperties());

        return ps;
    }

    private static final String UPDATE_ANSWER_SQL =
            updateSql(ANSWER_TABLE_NAME,
                    questionId, userId, imageIds, content,
                    state, createAtMs, updateAtMs,
                    extInfo) +
                    new WhereClauseBuilder(answerId.eqWhich()).build();

    @Override
    protected int doUpdate(Answer answer) {
        return update(UPDATE_ANSWER_SQL,
                answer.getQuestionId(), answer.getUserId(), answer.getImageIds(), answer.getContent(),
                answer.getCommonState().getIndex(), answer.getCreateAtMs(), answer.getUpdateAtMs(),
                ExtInfoColumn.toBytes(answer.getProperties()), answer.getId());
    }

    private static final String SELECT_ANSWERS_BY_QUESTION_ID =
            selectAllSql(ANSWER_TABLE_NAME) + new WhereClauseBuilder(questionId.eqWhich()).and(state.eq(1)).limitOffset();

    public List<Answer> listByQuestionId(long questionId, int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_ANSWERS_BY_QUESTION_ID, rowMapper, questionId, limit, offset);
    }

    private static final String SELECT_ANSWERS_BY_USER_ID =
            selectAllSql(ANSWER_TABLE_NAME) + new WhereClauseBuilder(userId.eqWhich()).and(state.eq(1)).limitOffset();

    public List<Answer> listByUserId(long userId, int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_ANSWERS_BY_USER_ID, rowMapper, userId, limit, offset);
    }
}
