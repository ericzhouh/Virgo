package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.ExpertApplying;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.date.DatetimeColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangtianhang on 15/6/3.
 */
@Repository(value = "applyExpertMysqlDao")
public class ApplyExpertMysqlDao extends BaseMysqlDao {
    public static final String APPLY_EXPERT_TABLE_NAME = "apply_expert";

    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final VarcharColumn reason = (VarcharColumn) new VarcharColumn("reason", 1024).
            setAllowNull(false).setComment("原因").setUnique(false);
    private static final DatetimeColumn applyingTime = (DatetimeColumn) new DatetimeColumn("applying_time").setComment("申请时间").setAllowNull(false);
    private static final TinyIntColumn state = Columns.newStateColumn(false);

    public static final String createDDL = new MysqlDDLBuilder(APPLY_EXPERT_TABLE_NAME).
            addColumn(userId).addColumn(applyingTime).addColumn(reason).
            addColumn(state).
            setPrimaryKey(userId).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseMysqlDao.dropDDL(APPLY_EXPERT_TABLE_NAME), dropBeforeCreate);
    }

    private static final String insert_applying_expert_sql =
            insertIntoSQL(APPLY_EXPERT_TABLE_NAME, userId, reason, applyingTime, state);

    public ExpertApplying create(long userId, String reason, long applyingTime, int state) {
        if (update(insert_applying_expert_sql, userId, reason, applyingTime, state) > 0) {
            return new ExpertApplying(userId, reason, applyingTime, state);
        } else {
            return null;
        }
    }

    private static final String update_applying_expert_sql =
            updateSql(APPLY_EXPERT_TABLE_NAME, reason, state) + new WhereClauseBuilder(userId.eqWhich());

    public boolean updateApplyingExpert(long userId, String reason, int state) {
        return update(update_applying_expert_sql, reason, state, userId) > 0;
    }

    public static final RowMapper<ExpertApplying> expertApplyingRowMapper = new RowMapper<ExpertApplying>() {
        @Override
        public ExpertApplying mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExpertApplying expertApplying = new ExpertApplying();

            expertApplying.setUserId(userId.getValue(rs));
            expertApplying.setState(state.getValue(rs));
            expertApplying.setReason(reason.getValue(rs));
            expertApplying.setApplyingTime(applyingTime.getValue(rs));

            return expertApplying;
        }
    };

    private static final String select_all_sql =
            selectAllSql(APPLY_EXPERT_TABLE_NAME) + new WhereClauseBuilder().orderBy(applyingTime).limitOffset();

    private static final String select_by_state_sql =
            selectAllSql(APPLY_EXPERT_TABLE_NAME) + new WhereClauseBuilder(state.eqWhich()).orderBy(applyingTime).limitOffset();

    public List<ExpertApplying> retrieve(Integer state, int limit, int offset) {
        if (state == null) {
            return queryForList(getReadJdbcTemplate(), select_all_sql, expertApplyingRowMapper, limit, offset);
        } else {
            return queryForList(getReadJdbcTemplate(), select_by_state_sql, expertApplyingRowMapper, state, limit, offset);
        }
    }

    private static final String select_last_by_user_id_sql =
            selectAllSql(APPLY_EXPERT_TABLE_NAME) + new WhereClauseBuilder(userId.eqWhich()).orderBy(applyingTime).limit();

    public ExpertApplying retrieveLastByUserId(long userId) {
        return queryForObject(getReadJdbcTemplate(), select_last_by_user_id_sql, expertApplyingRowMapper, userId, 1);
    }
}
