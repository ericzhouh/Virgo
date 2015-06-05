package com.winterfarmer.virgo.storage.counter.dao;

import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.IndexType;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.MediumintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class CounterMysqlDaoImpl extends BaseMysqlDao implements CounterDao {
    private final String tableName;
    private final String selectCountSql;
    private final String insertOrUpdateCountSql;
    private final String createCounterTableDDL;

    private final RowMapper<Integer> countRowMapper = new RowMapper<Integer>() {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return count.getValue(rs);
        }
    };

    public CounterMysqlDaoImpl(String tableName) {
        this.tableName = tableName;
        this.selectCountSql = selectSql(this.tableName, count) + new WhereClauseBuilder(id.eqWhich()).and(type.eqWhich());
        this.insertOrUpdateCountSql = "INSERT INTO " + this.tableName + " (id, type, count) "
                + " VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE "
                + " id=VALUES(id), "
                + " type=VALUES(type), "
                + " count=VALUES(count)";
        this.createCounterTableDDL = new MysqlDDLBuilder(this.tableName).
                addColumn(id).addColumn(type).addColumn(count).
                setPrimaryKey(id, type).addIndex(IndexType.btree, count).
                buildCreateDDL();
    }

    private static final BigintColumn id = (BigintColumn) new BigintColumn("id").
            setDisplaySize(20).setAllowNull(false).setComment("counter id");
    private static final TinyIntColumn type = (TinyIntColumn) new TinyIntColumn("type").
            setAllowNull(false).setComment("counter类型");
    private static final MediumintColumn count = (MediumintColumn) new MediumintColumn("count").
            setAllowNull(false).setDefaultValue("0").setComment("计数");

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createCounterTableDDL, BaseMysqlDao.dropDDL(tableName), dropBeforeCreate);
    }

    @Override
    public Integer getCount(long id, int type) {
        return queryForObject(getReadJdbcTemplate(), selectCountSql, countRowMapper, id, type);
    }

    @Override
    public boolean setCount(long id, int type, Integer count) {
        if (count == null) {
            return false;
        }

        return getWriteJdbcTemplate().update(insertOrUpdateCountSql, id, type, count) > 0;
    }
}
