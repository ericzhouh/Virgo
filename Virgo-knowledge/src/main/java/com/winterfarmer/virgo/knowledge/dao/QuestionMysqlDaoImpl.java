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
import com.winterfarmer.virgo.knowledge.model.Question;
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
 * Created by yangtianhang on 15/5/17.
 */
@Repository(value = "questionMysqlDao")
public class QuestionMysqlDaoImpl extends IdModelMysqlDao<Question> {
    public static final String QUESTION_TABLE_NAME = "question";

    private static final BigintColumn questionId = Columns.newIdColumn("question_id", true, false);

    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final VarcharColumn subject = (VarcharColumn) new VarcharColumn("subject", 128).
            setAllowNull(false).setComment("问题题目").setUnique(false);
    private static final VarcharColumn imageIds = (VarcharColumn) new VarcharColumn("image_ids", 256).
            setAllowNull(false).setComment("逗号分隔的image id").setUnique(false);
    private static final TextColumn content = (TextColumn) new TextColumn("content").
            setAllowNull(false).setComment("content");
    private static final TinyIntColumn state = Columns.newStateColumn(false);
    private static final BigintColumn createAtMs = Columns.newLongColumn("create_at_ms", false);
    private static final BigintColumn updateAtMs = Columns.newLongColumn("update_at_ms", false);
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    private static final RowMapper<Question> rowMapper = new RowMapper<Question>() {
        @Override
        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
            Question question = new Question();
            question.setId(questionId.getValue(rs));
            question.setUserId(userId.getValue(rs));
            question.setSubject(subject.getValue(rs));
            question.setImageIds(imageIds.getValue(rs));
            question.setContent(content.getValue(rs));
            question.setCommonState(CommonState.valueByIndex(state.getValue(rs)));
            question.setCreateAtMs(createAtMs.getValue(rs));
            question.setUpdateAtMs(updateAtMs.getValue(rs));
            question.setProperties(extInfo.getValue(rs));
            return question;
        }
    };

    public QuestionMysqlDaoImpl() {
        super(QUESTION_TABLE_NAME, questionId, rowMapper);
    }

    public static final String CREATE_DDL = new MysqlDDLBuilder(QUESTION_TABLE_NAME).
            addColumn(questionId).addColumn(userId).addColumn(subject).
            addColumn(imageIds).addColumn(content).addColumn(state).
            addColumn(createAtMs).addColumn(updateAtMs).addColumn(extInfo).
            setPrimaryKey(IndexType.btree, questionId).addIndex(userId).addIndex(IndexType.btree, createAtMs).setAutoIncrement(6800).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(CREATE_DDL, BaseMysqlDao.dropDDL(QUESTION_TABLE_NAME), dropBeforeCreate);
    }

    private static final String SELECT_QUESTIONS =
            selectAllSql(QUESTION_TABLE_NAME) + new WhereClauseBuilder(state.getName() + "=1 ").orderBy(createAtMs).limitOffset();

    public List<Question> list(int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_QUESTIONS, rowMapper, limit, offset);
    }

    private static final String SELECT_QUESTIONS_BY_USER =
            selectAllSql(QUESTION_TABLE_NAME) + new WhereClauseBuilder(userId.eqWhich()).and(state.eq(1)).orderBy(createAtMs).limitOffset();

    public List<Question> listByUser(long userId, int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_QUESTIONS_BY_USER, rowMapper, userId, limit, offset);
    }

    private static final String SELECT_QUESTIONS_BY_SUBJECT =
            selectAllSql(QUESTION_TABLE_NAME) + new WhereClauseBuilder(subject.likeWhich()).and(state.eq(1)).limitOffset();

    public List<Question> searchBySubject(String keywords, int limit, int offset) {
        return queryForList(getReadJdbcTemplate(), SELECT_QUESTIONS_BY_SUBJECT, rowMapper, "%" + keywords + "%", limit, offset);
    }

    private static final String INSERT_QUESTION_SQL = insertIntoSQL(QUESTION_TABLE_NAME,
            userId, subject, imageIds, content, state, createAtMs, updateAtMs, extInfo);

    @Override
    protected PreparedStatement createInsertPreparedStatement(Connection connection, Question question) throws SQLException {
        VirgoLogger.info(INSERT_QUESTION_SQL);

        PreparedStatement ps = connection.prepareStatement(INSERT_QUESTION_SQL, new String[]{questionId.getName()});

        ps.setLong(1, question.getUserId());
        ps.setString(2, question.getSubject());
        ps.setString(3, question.getImageIds());
        ps.setString(4, question.getContent());
        ps.setInt(5, question.getCommonState().getIndex());
        ps.setLong(6, question.getCreateAtMs());
        ps.setLong(7, question.getUpdateAtMs());
        setExtForPreparedStatement(ps, 8, question.getProperties());

        return ps;
    }

    private static final String UPDATE_QUESTION_SQL =
            updateSql(QUESTION_TABLE_NAME,
                    userId, subject, imageIds, content,
                    state, createAtMs, updateAtMs,
                    extInfo) +
                    new WhereClauseBuilder(questionId.eqWhich()).build();

    @Override
    public int doUpdate(Question question) {
        return update(UPDATE_QUESTION_SQL,
                question.getUserId(), question.getSubject(), question.getImageIds(), question.getContent(),
                question.getCommonState().getIndex(), question.getCreateAtMs(), question.getUpdateAtMs(),
                ExtInfoColumn.toBytes(question.getProperties()), question.getId());
    }
}
